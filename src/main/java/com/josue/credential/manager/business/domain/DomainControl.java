/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.util.Current;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    public Domain createDomain(Domain domain) {
        //TODO check if its possible to use injected manager
        Manager actualManager = repository.find(Manager.class, currentCredential.getManager().getUuid());
        List<String> domainUuids = repository.getDomainUuidByName(domain.getName());
        if (!domainUuids.isEmpty()) {
            //do throw exception
            throw new RuntimeException("TODO change me");
        }
        domain.setOwner(actualManager);
        repository.create(domain);
        return domain;
    }

    public void deleteDomain(String domainUuid) {
        //TODO improve business logic...
        Domain foundDomain = repository.find(Domain.class, domainUuid);
        if (foundDomain == null) {
            //TODO exceptions... ?!!!
            throw new RuntimeException("TODO change me");
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
