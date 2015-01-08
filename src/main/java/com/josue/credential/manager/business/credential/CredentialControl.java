/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.credential.APICredential;
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

    public List<APIDomainCredential> getApiCredentialsByManagerDomain(String domainUuid) {
        List<APIDomainCredential> apiDomainCredentials = repository.getApiCredentialsByManagerDomain(currentCredential.getManager().getUuid(), domainUuid);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }
        return apiDomainCredentials;
    }

    public List<APIDomainCredential> getAPICredentials() {
        List<APIDomainCredential> apiDomainCredentials = repository.getApiCredentialsByManager(currentCredential.getManager().getUuid());
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }
        return apiDomainCredentials;
    }

    //This method should not executed inside the same transaction of ANY repository
    //TODO improve
    private void obfuscateKeys(APICredential apiCredential) {
        String apiKey = apiCredential.getApiKey();
        String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
        apiCredential.setApiKey(obfuscatedApiKey);
    }

}
