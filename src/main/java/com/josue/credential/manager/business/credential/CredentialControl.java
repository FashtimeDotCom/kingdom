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
import com.josue.credential.manager.business.ListResourceUtil;
import com.josue.credential.manager.rest.ListResource;
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
        return repository.getManager(credentialUuid);
    }

    public ListResource<APIDomainCredential> getAPICredentials(String domainUuid, Integer limit, Integer offset) {
        List<APIDomainCredential> apiDomainCredentials = repository.getApiCredentials(currentCredential.getManager().getUuid(), domainUuid, limit, offset);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }

        long totalCount = repository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    public ListResource<APIDomainCredential> getAPICredentials(Integer limit, Integer offset) {
        List<APIDomainCredential> apiDomainCredentials = repository.getApiCredentials(currentCredential.getManager().getUuid(), limit, offset);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }
        long totalCount = repository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    //This method should not executed inside the same transaction of ANY repository
    //TODO improve
    private void obfuscateKeys(APICredential apiCredential) {
        String apiKey = apiCredential.getApiKey();
        String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
        apiCredential.setApiKey(obfuscatedApiKey);
    }

}
