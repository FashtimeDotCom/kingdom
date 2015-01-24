package com.josue.kingdom.domain;

import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ListResourceUtils;
import com.josue.kingdom.rest.ex.AuthorizationException;
import com.josue.kingdom.rest.ex.InvalidResourceArgException;
import com.josue.kingdom.rest.ex.ResourceAlreadyExistsException;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.Current;
import com.josue.kingdom.security.KingdomSecurity;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class DomainControl {

    @Inject
    DomainRepository repository;

    @Inject
    @Current
    KingdomSecurity security;

    public ListResource<Domain> getOwnedDomains(Integer limit, Integer offset) throws RestException {
        long totalCount = repository.countOwnedDomains(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid());
        List<Domain> ownedDomains = repository.getOwnedDomains(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid(), limit, offset);
        return ListResourceUtils.buildListResource(ownedDomains, totalCount, limit, offset);
    }

    public ListResource<Domain> getJoinedDomains(Integer limit, Integer offset) throws RestException {
        List<Domain> joinedDomains = repository.getJoinedDomains(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid(), limit, offset);

        long totalCount = repository.countJoinedDomains(security.getCurrentApplication().getUuid(), security.getCurrentManager().getUuid());
        return ListResourceUtils.buildListResource(joinedDomains, totalCount, limit, offset);
    }

    public Domain getJoinedDomain(String domainUuid) throws RestException {
        Domain joinedDomain = repository.find(Domain.class, security.getCurrentApplication().getUuid(), domainUuid);
        if (joinedDomain == null) {
            throw new ResourceNotFoundException(ManagerMembership.class, domainUuid);
        }
        return joinedDomain;
    }

    public Domain getOwnedDomain(String domainUuid) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        return foundDomain;
    }

    public Domain createDomain(Domain domain) throws RestException {
        domain.removeNonCreatable();
        domain.setStatus(DomainStatus.ACTIVE);
        domain.setOwner(security.getCurrentManager());

        //TODO this should run inside the TX
        repository.create(domain);
        createDefaultPermissions(domain);

        return domain;
    }

    //TODO improve business logic for this class
    public Domain updateDomain(String domainUuid, Domain domain) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        checkOwnerAccess(foundDomain);
        foundDomain.copyUpdatable(domain);
        foundDomain = repository.update(foundDomain);
        return foundDomain;
    }

    public void deleteDomain(String domainUuid) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        checkOwnerAccess(foundDomain);
        //TODO improve business logic:... deactivate all docs and clean everything
        repository.purgeDomain(security.getCurrentApplication().getUuid(), foundDomain.getUuid());
    }

    public DomainPermission createDomainPermission(String domainUuid, DomainPermission domainPermission) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        checkOwnerAccess(foundDomain);
        domainPermission.removeNonCreatable();

        if (domainPermission.getLevel() == 0) {
            throw new InvalidResourceArgException(DomainPermission.class, "level", "0");
        }

        DomainPermission foundPermission = repository.getDomainPermission(security.getCurrentApplication().getUuid(), domainUuid, domainPermission.getLevel());
        if (foundPermission != null) { //Permission level already exists
            throw new ResourceAlreadyExistsException(DomainPermission.class, "level", domainPermission.getLevel());
        }

        domainPermission.setDomain(foundDomain);
        repository.create(domainPermission);
        return domainPermission;
    }

    public DomainPermission updateDomainPermission(String domainUuid, String permissionUuid, DomainPermission domainPermission) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        checkOwnerAccess(foundDomain);
        DomainPermission foundPermission = repository.getDomainPermission(security.getCurrentApplication().getUuid(), domainUuid, domainPermission.getLevel());
        if (foundPermission != null) { //Permission level already exists
            throw new ResourceAlreadyExistsException(DomainPermission.class, "level", domainPermission.getLevel());
        }
        if (domainPermission.getLevel() == 0) {
            throw new InvalidResourceArgException(DomainPermission.class, "level", "0");
        }
        DomainPermission permissionByUuid = repository.find(DomainPermission.class, security.getCurrentApplication().getUuid(), permissionUuid);
        if (permissionByUuid == null) {
            throw new ResourceNotFoundException(DomainPermission.class, permissionUuid);
        }

        permissionByUuid.copyUpdatable(domainPermission);

        repository.update(permissionByUuid);
        return permissionByUuid;
    }

    //1. Domain should have at least 1 permission
    //2. if user provide a replacement, update all managers that have the delete permission
    //3. if no replacement provided and user still have the permission, an exception should be throw
    /**
     *
     * @param domainUuid
     * @param permissionUuid
     * @param replacementUuid
     * @throws RestException
     */
    public void deleteDomainPermission(String domainUuid, String permissionUuid, String replacementUuid) throws RestException {
        Domain foundDomain = checkDomainExists(domainUuid);
        checkOwnerAccess(foundDomain);
        // fixed limit, we just need to know if its size is equal to 1
        List<DomainPermission> domainPermissions = repository.getDomainPermissions(security.getCurrentApplication().getUuid(), domainUuid, 50, 0);
        if (domainPermissions.size() == 1) {
            throw new RestException(DomainPermission.class, domainUuid, "A domain should have at least one permission", Response.Status.BAD_REQUEST);
        }

        DomainPermission foundPermission = null;
        DomainPermission foundReplacementPermission = null;
        for (DomainPermission r : domainPermissions) { //reusing the result
            if (r.getUuid().equals(permissionUuid)) {
                foundPermission = r;
            }
            if (r.getUuid().equals(replacementUuid)) {
                foundReplacementPermission = r;
            }
        }

        if (foundPermission == null) {
            throw new ResourceNotFoundException(DomainPermission.class, permissionUuid);
        }
        //TODO this block should run inside the same transaction
        if (replacementUuid != null) {
            if (foundReplacementPermission == null) {
                //replacement permission doesnt exists
                //not using ResourceNotFound because its an optional param
                throw new RestException(DomainPermission.class, replacementUuid, "", Response.Status.BAD_REQUEST);
            } else {
                //replacement exists and its valid...
                //TODO change managers permissions and remove this Permission
            }
        }
        repository.delete(foundPermission);
    }

    public ListResource<DomainPermission> getDomainPermissions(String domainUuid, Integer limit, Integer offset) {
        List<DomainPermission> permissions = repository.getDomainPermissions(security.getCurrentApplication().getUuid(), domainUuid, limit, offset);
        return ListResourceUtils.buildListResource(permissions, permissions.size(), permissions.size(), 0);
    }

    //Created a simple method because its important responsability
    //Returns true if a the actual credential can update the domain
    protected void checkOwnerAccess(Domain domain) throws RestException {
        if (!domain.getOwner().equals(security.getCurrentManager())) {
            throw new AuthorizationException();
        }
    }

    //returns an existent domain, if none found, throw a exception
    protected Domain checkDomainExists(String domainUuid) throws RestException {
        Domain foundDomain = repository.find(Domain.class, security.getCurrentApplication().getUuid(), domainUuid);
        if (foundDomain == null) {
            throw new ResourceNotFoundException(Domain.class, domainUuid);
        }
        return foundDomain;
    }

    private void createDefaultPermissions(Domain domain) {
        DomainPermission permission = new DomainPermission();
        permission.setLevel(1);
        permission.setName("LEVEL_1");
        permission.setDescription("Permission level 1");
        permission.setDomain(domain);
        repository.create(permission);

        permission = new DomainPermission();
        permission.setLevel(2);
        permission.setName("LEVEL_2");
        permission.setDescription("Permission level 2");
        permission.setDomain(domain);
        repository.create(permission);

        permission = new DomainPermission();
        permission.setLevel(3);
        permission.setName("LEVEL_3");
        permission.setDescription("Permission level 3");
        permission.setDomain(domain);
        repository.create(permission);
    }
}
