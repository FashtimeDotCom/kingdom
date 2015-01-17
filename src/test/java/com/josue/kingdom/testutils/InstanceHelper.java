/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.CredentialStatus;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.APIDomainCredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.domain.entity.ManagerDomainCredential;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Josue
 */
public abstract class InstanceHelper {

    private static final SecureRandom random = new SecureRandom();

    public static Date mysqlMilliSafeTimestamp() {

        //TIP: http://www.coderanch.com/t/530003/java/java/Comparing-Date-Timestamp-unexpected-result
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        cal.set(Calendar.MILLISECOND, 0); //reset milliseconds

        return cal.getTime();
    }

    //#### Permission ####
    public static DomainPermission createPermission(Domain domain) {
        DomainPermission permission = new DomainPermission();
        permission.setDescription("Permission description");
        permission.setLevel(new Random().nextInt());
        permission.setName("ADMIN");
        permission.setDomain(domain);
        return permission;
    }

    //#### APICredential ###
    public static APICredential createAPICredential(Manager manager) {
        APICredential apiCredential = new APICredential();
        apiCredential.setApiKey(new BigInteger(130, random).toString(32));
        apiCredential.setManager(manager);
        apiCredential.setStatus(CredentialStatus.ACTIVE);
        return apiCredential;
    }

    //#### ManagerCredential ####
    public static ManagerCredential createManagerCredential(Manager manager) {
        ManagerCredential credential = new ManagerCredential();
        credential.setLogin(UUID.randomUUID().toString());
        credential.setPassword("password-123");
        credential.setStatus(CredentialStatus.ACTIVE);
        credential.setManager(manager);
        return credential;
    }

    //#### Manager ####
    public static Manager createManager() {
        Manager manager = new Manager();
        manager.setEmail(Long.toHexString(Double.doubleToLongBits(Math.random())) + "@email.com");
        manager.setFirstName("josue");
        manager.setLastName("Eduardo");
        return manager;
    }

    //#### Domain ####
    public static Domain createDomain(Manager owner) {
        Domain domain = new Domain();
        domain.setName(Long.toHexString(Double.doubleToLongBits(Math.random())));
        domain.setStatus(DomainStatus.ACTIVE);
        domain.setDescription("Description 123");
        domain.setOwner(owner);
        return domain;
    }

    //#### ManagerDomainCredential ###
    public static ManagerDomainCredential createManagerDomainCredential(Domain domain, ManagerCredential credential, DomainPermission permission) {
        ManagerDomainCredential domainCredential = new ManagerDomainCredential();
        domainCredential.setDomain(domain);
        domainCredential.setCredential(credential);
        domainCredential.setPermission(permission);
        return domainCredential;
    }

    //#### ManagerDomainCredential ###
    public static APIDomainCredential createAPIDomainCredential(Domain domain, APICredential credential, DomainPermission permission) {
        APIDomainCredential domainCredential = new APIDomainCredential();
        domainCredential.setName("api-key-name");
        domainCredential.setDomain(domain);
        domainCredential.setCredential(credential);
        domainCredential.setPermission(permission);
        return domainCredential;
    }

    //#### FULL ENTITY TREE CREATION ####
    public static APIDomainCredential createFullAPIDomainCredential(JpaRepository repository) {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        APICredential credapiCredential = InstanceHelper.createAPICredential(manager);
        repository.create(credapiCredential);

        DomainPermission permission = InstanceHelper.createPermission(domain);
        repository.create(permission);

        APIDomainCredential domainCredential = InstanceHelper.createAPIDomainCredential(domain, credapiCredential, permission);
        repository.create(domainCredential);

        return domainCredential;
    }

    public static ManagerDomainCredential createFullManagerDomainCredential(JpaRepository repository) {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        ManagerCredential credential = InstanceHelper.createManagerCredential(manager);
        repository.create(credential);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        APICredential credapiCredential = InstanceHelper.createAPICredential(manager);
        repository.create(credapiCredential);

        DomainPermission permission = InstanceHelper.createPermission(domain);
        repository.create(permission);

        ManagerDomainCredential domainCredential = InstanceHelper.createManagerDomainCredential(domain, credential, permission);
        repository.create(domainCredential);

        return domainCredential;
    }

}
