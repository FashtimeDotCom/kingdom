/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import com.josue.kingdom.account.entity.Manager;
import com.josue.kingdom.account.Current;
import com.josue.kingdom.util.ListResourceUtil;
import com.josue.kingdom.rest.ListResource;
import com.josue.kingdom.rest.ex.ResourceNotFoundException;
import com.josue.kingdom.rest.ex.RestException;
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
    Credential currentCredential;

    public ListResource<Domain> getOwnedDomains(Integer limit, Integer offset) {
        long totalCount = repository.countOwnedDomains(currentCredential.getManager().getUuid());
        List<Domain> ownedDomains = repository.getOwnedDomainsByManager(currentCredential.getManager().getUuid(), limit, offset);
        return ListResourceUtil.buildListResource(ownedDomains, totalCount, limit, offset);
    }

    public ListResource<ManagerDomainCredential> getJoinedDomains(Integer limit, Integer offset) {
        List<ManagerDomainCredential> joinedDomains = repository.getJoinedDomainsByManager(currentCredential.getManager().getUuid(), limit, offset);
        for (ManagerDomainCredential dc : joinedDomains) {
            dc.setCredential(null);
        }
        long totalCount = repository.countDomainCredentials(currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(joinedDomains, totalCount, limit, offset);
    }

    public ManagerDomainCredential getJoinedDomain(String manDomCredUuid) {
        ManagerDomainCredential joinedDomain = repository.find(ManagerDomainCredential.class, manDomCredUuid);
        joinedDomain.setCredential(null);
        return joinedDomain;
    }

    public Domain createDomain(Domain domain) throws RestException {
        //removes not allowed user input
        domain.removeNonCreatableFields();
        domain.setStatus(DomainStatus.ACTIVE);
        Manager actualManager = repository.find(Manager.class, currentCredential.getManager().getUuid());
        Domain foundDomain = repository.getDomainByName(domain.getName());
        if (foundDomain != null) {
            //do throw exception
            throw new RestException(Domain.class, foundDomain.getUuid(), "Domain already exists with name '" + domain.getName() + "'", Response.Status.BAD_REQUEST);
        }
        domain.setOwner(actualManager);
        repository.create(domain);
        return domain;
    }

    public Domain updateDomain(String domainUuid, Domain domain) throws RestException {
        //TODO improve business logic...
        Domain foundDomain = repository.find(Domain.class, domainUuid);
        if (foundDomain == null) {
            //TODO exceptions... ?!!!
            throw new ResourceNotFoundException(Domain.class, domainUuid);
        }
        foundDomain.copyUpdatebleFields(domain);
        foundDomain = repository.update(foundDomain);
        return foundDomain;
    }

    public void deleteDomain(String domainUuid) throws RestException {
        //TODO improve business logic:... deactivate all docs and clean everything
        Domain foundDomain = repository.find(Domain.class, domainUuid);
        if (foundDomain == null) {
            //TODO exceptions... ?!!!
            throw new ResourceNotFoundException(Domain.class, domainUuid);
        }
        repository.delete(foundDomain);
    }
}