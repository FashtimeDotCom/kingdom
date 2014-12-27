/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.APIDomainCredential;
import com.josue.credential.manager.auth.Domain;
import com.josue.credential.manager.auth.DomainCredential;
import com.josue.credential.manager.auth.cdi.Current;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author iFood
 */
@ApplicationScoped
public class AccountControl {

    @Inject
    AccountRepository repository;

    @Inject
    @Current
    Manager currentManager;

    public Manager getManagerByCredential(String credentialUuid) {
        return repository.getManagerByCredential(credentialUuid);
    }

    public List<DomainCredential> getJoinedDomains() {
        Subject subject = SecurityUtils.getSubject();
        List<DomainCredential> joinedDomains = repository.getJoinedDomainsByCredential(subject.getPrincipal().toString());
        for (DomainCredential dc : joinedDomains) {
            //TODO check if is needed to clear Credentials fields before return on REST endpoint
        }
        return joinedDomains;
    }

    public List<Domain> getOwnedDomains() {
        return repository.getOwnedDomainsByManager(currentManager.getUuid());
    }

    //Every APICredential should be bound to a Domain, so make no sense return APICredential only
    public List<APIDomainCredential> getAPICredentials() {
        List<APIDomainCredential> apiCredentials = repository.getApiCredentialsByManager(currentManager.getUuid());
        //obfuscate api key
        //TODO improve
        for (APIDomainCredential apiCredential : apiCredentials) {
            String apiKey = apiCredential.getCredential().getApiKey();
            String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
            apiCredential.getCredential().setApiKey(obfuscatedApiKey);
        }

        return apiCredentials;
    }

    public AccountControl() {
    }

    public ManagerInvitation invite(ManagerInvitation managerInvitation) {
        return null;
    }

    public ManagerInvitation confirm(ManagerInvitation managerInvitation) {
        return null;
    }
}
