/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.auth.credential.APICredential;
import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.credential.CredentialStatus;
import com.josue.credential.manager.auth.domain.APIDomainCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.auth.shiro.AccessLevelPermission;
import com.josue.credential.manager.auth.util.Current;
import com.josue.credential.manager.business.ListResourceUtil;
import com.josue.credential.manager.business.role.RoleRepository;
import com.josue.credential.manager.rest.ListResource;
import com.josue.credential.manager.rest.ex.AuthorizationException;
import com.josue.credential.manager.rest.ex.InvalidResourceArgException;
import com.josue.credential.manager.rest.ex.ResourceNotFoundException;
import com.josue.credential.manager.rest.ex.RestException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CredentialControl {

    @Inject
    CredentialRepository repository;

    @Inject
    RoleRepository roleRepository;

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
            //Optional.. removing non usable fields
            apiDomCredential.getDomain().setOwner(null);
            apiDomCredential.getCredential().setManager(null);
        }

        long totalCount = repository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    //Not used yet, return all credential for all Domains
    public ListResource<APIDomainCredential> getAPICredentials(Integer limit, Integer offset) {
        List<APIDomainCredential> apiDomainCredentials = repository.getApiCredentials(currentCredential.getManager().getUuid(), limit, offset);
        for (APIDomainCredential apiDomCredential : apiDomainCredentials) {
            obfuscateKeys(apiDomCredential.getCredential());
        }
        long totalCount = repository.countAPICredential(currentCredential.getManager().getUuid(), currentCredential.getManager().getUuid());
        return ListResourceUtil.buildListResource(apiDomainCredentials, totalCount, limit, offset);
    }

    public APIDomainCredential getAPICredential(String domainUuid, String apiKeyUuid) {
        APIDomainCredential apiDomainCredentials = repository.getApiCredential(currentCredential.getManager().getUuid(), domainUuid, apiKeyUuid);
        obfuscateKeys(apiDomainCredentials.getCredential());
        return apiDomainCredentials;
    }

    public APIDomainCredential updateAPICredential(String domainUuid, String credentialUuid, APIDomainCredential domainCredential) throws RestException {
        APIDomainCredential foundCredential = repository.find(APIDomainCredential.class, credentialUuid);
        if (foundCredential == null) {
            throw new ResourceNotFoundException(APICredential.class, credentialUuid);
        }

        Role foundRole = roleRepository.findRoleByName(domainCredential.getRole().getName());
        if (foundRole == null) {
            throw new InvalidResourceArgException(APICredential.class, "Role name", domainCredential.getRole().getName());
        }

        //Check permission for create API Role level
        if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, foundRole))) {
            throw new AuthorizationException(domainCredential.getRole());
        }

        foundCredential.copyUpdatebleFields(domainCredential);
        APIDomainCredential updated = repository.edit(foundCredential);
        repository.edit(foundCredential.getCredential());
        return updated;

    }

    public APIDomainCredential createAPICredential(String domainUuid, APIDomainCredential domainCredential) throws RestException {

        domainCredential.removeNonCreatableFields();
        Role foundRole = roleRepository.findRoleByName(domainCredential.getRole().getName());
        if (foundRole == null) {
            throw new InvalidResourceArgException(APICredential.class, "Role name", domainCredential.getRole().getName());
        }

        //Check permission for create API Role level
        if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, foundRole))) {
            throw new AuthorizationException(domainCredential.getRole());
        }

        Domain currentDomain = repository.find(Domain.class, domainUuid);
        if (currentDomain == null) {
            throw new InvalidResourceArgException(Domain.class, "Domain", domainUuid);
        }

        domainCredential.setRole(foundRole);
        domainCredential.setDomain(currentDomain);
        domainCredential.getCredential().setApiKey(generateAPIKey());
        domainCredential.getCredential().setStatus(CredentialStatus.ACTIVE);
        domainCredential.getCredential().setManager(currentCredential.getManager());

        //TODO This block should be executed within sae transaction
        repository.create(domainCredential.getCredential());
        repository.create(domainCredential);

        return domainCredential;

    }

    public void deleteAPiCredential(String domainUuid, String domainCredentialUuid) throws ResourceNotFoundException {
        APIDomainCredential apiDomCred = repository.find(APIDomainCredential.class, domainCredentialUuid);
        if (apiDomCred == null) {
            throw new ResourceNotFoundException(APIDomainCredential.class, domainCredentialUuid);
        }
        //TODO This block should run within the same TX
        repository.remove(apiDomCred);
        repository.remove(apiDomCred.getCredential());
    }

    //This method should not executed inside the same transaction of ANY repository
    //TODO improve
    private void obfuscateKeys(APICredential apiCredential) {
        String apiKey = apiCredential.getApiKey();
        String obfuscatedApiKey = "************" + apiKey.substring(apiKey.length() - 5);
        apiCredential.setApiKey(obfuscatedApiKey);
    }

    private String generateAPIKey() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

}
