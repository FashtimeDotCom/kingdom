/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.util.Current;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

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
    Manager currentManager;

    public List<Domain> getOwnedDomains() {
        return repository.getOwnedDomainsByManager(currentManager.getUuid());
    }

    public List<DomainCredential> getJoinedDomains() {
        Subject subject = SecurityUtils.getSubject();
        List<DomainCredential> joinedDomains = repository.getJoinedDomainsByCredential(subject.getPrincipal().toString());
        for (DomainCredential dc : joinedDomains) {
            //TODO check if is needed to clear Credentials fields before return on REST endpoint
        }
        return joinedDomains;
    }

    public Domain createDomain(Domain domain) {
        Manager actualManager = repository.find(Manager.class, currentManager.getUuid());
        domain.setOwner(actualManager);
        repository.create(domain);
        return domain;
    }
}
