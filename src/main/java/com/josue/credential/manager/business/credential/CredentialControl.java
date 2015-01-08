/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.domain.APIDomainCredential;
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
public class CredentialControl {

    @Inject
    CredentialRepository repository;

    @Inject
    @Current
    Credential currentCredential;

    public Manager getManagerByCredential(String credentialUuid) {
        return repository.getManagerByCredential(credentialUuid);
    }

    public List<APIDomainCredential> getAPICredentials() {
        List<APIDomainCredential> apiCredentials = repository.getApiCredentialsByManager(currentCredential.getManager().getUuid());
        //obfuscate api key
        //TODO improve
        for (APIDomainCredential apiCredential : apiCredentials) {
            String apiKey = apiCredential.getCredential().getApiKey();
            String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
            apiCredential.getCredential().setApiKey(obfuscatedApiKey);
        }
        return apiCredentials;
    }

}
