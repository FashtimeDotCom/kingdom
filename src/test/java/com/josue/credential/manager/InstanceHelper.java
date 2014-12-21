/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.account.Manager;
import com.josue.credential.manager.auth.APICredential;
import com.josue.credential.manager.auth.APIDomainCredential;
import com.josue.credential.manager.auth.CredentialStatus;
import com.josue.credential.manager.auth.Domain;
import com.josue.credential.manager.auth.DomainStatus;
import com.josue.credential.manager.auth.ManagerCredential;
import com.josue.credential.manager.auth.ManagerDomainCredential;
import com.josue.credential.manager.auth.Role;
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

    private static SecureRandom random = new SecureRandom();

    public static Date mysqlMilliSafeTimestamp() {

        //TIP: http://www.coderanch.com/t/530003/java/java/Comparing-Date-Timestamp-unexpected-result
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        cal.set(Calendar.MILLISECOND, 0); //reset milliseconds

        return cal.getTime();
    }

    //#### Role ####
    public static Role createRole() {
        Role role = new Role();
        role.setDescription("Role description");
        role.setLevel(new Random().nextInt());
        role.setName("ADMIN");
        return role;
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
        manager.setEmail(Long.toHexString(Double.doubleToLongBits(Math.random())) + "@gmail.com");
        manager.setFirstName("josue");
        manager.setLastName("Eduardo");
        return manager;
    }

    //#### Domain ####
    public static Domain createDomain(Manager owner) {
        Domain domain = new Domain();
        domain.setName("default-domain-name");
        domain.setStatus(DomainStatus.ACTIVE);
        domain.setDescription("Description 123");
        domain.setOwner(owner);
        return domain;
    }

    //#### ManagerDomainCredential ###
    public static ManagerDomainCredential createManagerDomainCredential(Domain domain, ManagerCredential credential, Role role) {
        ManagerDomainCredential domainCredential = new ManagerDomainCredential();
        domainCredential.setDomain(domain);
        domainCredential.setCredential(credential);
        domainCredential.setRole(role);
        return domainCredential;
    }

    //#### ManagerDomainCredential ###
    public static APIDomainCredential createAPIDomainCredential(Domain domain, APICredential credential, Role role) {
        APIDomainCredential domainCredential = new APIDomainCredential();
        domainCredential.setName("api-key-name");
        domainCredential.setDomain(domain);
        domainCredential.setCredential(credential);
        domainCredential.setRole(role);
        return domainCredential;
    }

}
