/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainStatus;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.util.Current;
import com.josue.credential.manager.rest.ex.ResourceNotFoundException;
import com.josue.credential.manager.rest.ex.RestException;
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

    public List<Domain> getOwnedDomains() {
        return repository.getOwnedDomainsByManager(currentCredential.getManager().getUuid());
    }

    public List<ManagerDomainCredential> getJoinedDomains() {
        List<ManagerDomainCredential> joinedDomains = repository.getJoinedDomainsByManager(currentCredential.getManager().getUuid());
        for (ManagerDomainCredential dc : joinedDomains) {
            dc.setCredential(null);
        }
        return joinedDomains;
    }

    public ManagerDomainCredential getJoinedDomainByUuid(String uuid) {
        ManagerDomainCredential joinedDomain = repository.find(ManagerDomainCredential.class, uuid);
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
        foundDomain = repository.edit(foundDomain);
        return foundDomain;
    }

    public void deleteDomain(String domainUuid) throws RestException {
        //TODO improve business logic:... deactivate all docs and clean everything
        Domain foundDomain = repository.find(Domain.class, domainUuid);
        if (foundDomain == null) {
            //TODO exceptions... ?!!!
            throw new ResourceNotFoundException(Domain.class, domainUuid);
        }
        repository.remove(foundDomain);
    }

    public long countDomainCredentials() {
        return repository.countDomainCredentials(currentCredential.getManager().getUuid());
    }

    public long countOwnedDomains() {
        return repository.countOwnedDomains(currentCredential.getManager().getUuid());
    }
}
