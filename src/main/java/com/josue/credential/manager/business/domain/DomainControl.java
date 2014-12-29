/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainCredential;
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
    ManagerCredential currentCredential;

    public List<Domain> getOwnedDomains() {
        return repository.getOwnedDomainsByManager(currentCredential.getManager().getUuid());
    }

    public List<DomainCredential> getJoinedDomains() {
        List<DomainCredential> joinedDomains = repository.getJoinedDomainsByCredential(currentCredential.getUuid());
        for (DomainCredential dc : joinedDomains) {
            //TODO check if is needed to clear Credentials fields before return on REST endpoint, update tests
        }
        return joinedDomains;
    }

    public Domain createDomain(Domain domain) {
        //TODO check if its possible to use injected manager
        Manager actualManager = repository.find(Manager.class, currentCredential.getManager().getUuid());
        List<String> domainUuids = repository.getDomainUuidByName(domain.getName());
        if(!domainUuids.isEmpty()){
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
}
