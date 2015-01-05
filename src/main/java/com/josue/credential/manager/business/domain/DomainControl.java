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
import com.josue.credential.manager.rest.ex.ResourceNotFoundException;
import com.josue.credential.manager.rest.ex.RestException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final Set<String> updatebleDomainFilds = new HashSet<>(Arrays.asList("description", "status"));
    private final Set<String> creatableDomainFilds = new HashSet<>(Arrays.asList("name", "description", "status"));

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

    public Domain createDomain(Domain domain) throws RestException {
        //TODO check if its possible to use injected manager
        checkCreatableField(domain);
        Manager actualManager = repository.find(Manager.class, currentCredential.getManager().getUuid());
        Domain foundDomain = repository.getDomainByName(domain.getName());
        if (foundDomain != null) {
            //do throw exception
            throw new RestException(Domain.class, foundDomain.getUuid(), "Domain already exists with name '" + domain.getName() + "'", Response.Status.OK);
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
        checkUpdatebleField(foundDomain, domain);
        foundDomain = repository.edit(foundDomain);
        return foundDomain;
    }

    public void deleteDomain(String domainUuid) throws RestException {
        //TODO improve business logic...
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

    //TODO possible refactoring
    private void checkCreatableField(Object domain) {
        try {
            for (Field field : domain.getClass().getDeclaredFields()) {
                if (!creatableDomainFilds.contains(field.getName())) {
                    field.setAccessible(true);
                    field.set(domain, null);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(DomainControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //TODO possible refactoring
    private void checkUpdatebleField(Domain actualObject, Domain newObject) {
        try {
            for (Field field : newObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!updatebleDomainFilds.contains(field.getName())) {
                    field.setAccessible(true);
                    field.set(newObject, null);
                } else {
                    //Copy allowed values to the actual object
                    Field targetField = newObject.getClass().getDeclaredField(field.getName());
                    Object newValue = field.get(newObject);
                    targetField.setAccessible(true);
                    targetField.set(actualObject, newValue);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(DomainControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
