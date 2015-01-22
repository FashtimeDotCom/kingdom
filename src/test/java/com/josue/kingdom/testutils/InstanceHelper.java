/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.DomainStatus;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.invitation.entity.Invitation;
import com.josue.kingdom.invitation.entity.InvitationStatus;
import com.josue.kingdom.rest.Resource;
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

    public static final String APP_ID = "926caa10-43a4-11e4-916c-0800200c9a66";

    public static Date mysqlMilliSafeTimestamp() {

        //TIP: http://www.coderanch.com/t/530003/java/java/Comparing-Date-Timestamp-unexpected-result
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        cal.set(Calendar.MILLISECOND, 0); //reset milliseconds

        return cal.getTime();
    }

    public static Resource getDefaultTestApplication() {
        Resource res = new Resource();
        res.setUuid(APP_ID);
        return res;
    }

    //#### Permission ####
    public static DomainPermission createPermission(Domain domain) {
        DomainPermission permission = new DomainPermission();
        permission.setDescription("Permission description");
        permission.setLevel(new Random().nextInt());
        permission.setName(new BigInteger(130, random).toString(8));
        permission.setDomain(domain);
        permission.setApplication(getDefaultTestApplication());
        return permission;
    }

    //#### APICredential ###
    public static APICredential createAPICredential(ManagerMembership membership) {
        APICredential apiCredential = new APICredential();
        apiCredential.setApplication(getDefaultTestApplication());
        apiCredential.setName("API-TEST-NAME");
        apiCredential.setApiKey(new BigInteger(130, random).toString(32));
        apiCredential.setStatus(AccountStatus.ACTIVE);
        apiCredential.setMembership(membership);

        return apiCredential;
    }

    //#### Manager ####
    public static Manager createManager() {
        String rand = Long.toHexString(Double.doubleToLongBits(Math.random()));
        Manager manager = new Manager();
        manager.setEmail(rand + "@email.com");
        manager.setFirstName("josue");
        manager.setLastName("Eduardo");
        manager.setUsername(rand);
        manager.setPassword("pass123");
        manager.setStatus(AccountStatus.ACTIVE);
        manager.setApplication(getDefaultTestApplication());
        return manager;
    }

    //#### Domain ####
    public static Domain createDomain(Manager owner) {
        Domain domain = new Domain();
        domain.setName(Long.toHexString(Double.doubleToLongBits(Math.random())));
        domain.setStatus(DomainStatus.ACTIVE);
        domain.setDescription("Description 123");
        domain.setOwner(owner);
        domain.setApplication(getDefaultTestApplication());
        return domain;
    }

    //#### Invitation ###
    public static Invitation createInvitation(Domain domain, Manager author, DomainPermission permission) {
        Invitation invitation = new Invitation();
        invitation.setDomain(domain);
        invitation.setPermission(permission);
        invitation.setAuthorManager(author);
        invitation.setTargetEmail(Long.toHexString(Double.doubleToLongBits(Math.random())) + "@email.com");
        invitation.setStatus(InvitationStatus.CREATED);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setApplication(getDefaultTestApplication());

        Calendar cal = Calendar.getInstance();
        cal.setTime(mysqlMilliSafeTimestamp());
        cal.set(Calendar.DAY_OF_MONTH, 2);
        invitation.setValidUntil(cal.getTime());

        return invitation;
    }

    //#### Domain ####
    public static ManagerMembership createManagerMembership(Manager manager, Domain domain, DomainPermission permission) {
        ManagerMembership membership = new ManagerMembership();
        membership.setDomain(domain);
        membership.setManager(manager);
        membership.setPermission(permission);
        membership.setApplication(getDefaultTestApplication());
        return membership;

    }

    //#### FULL ENTITY TREE CREATION ####
    public static ManagerMembership createFullManagerMembership(JpaRepository repository) {
        Manager manager = InstanceHelper.createManager();
        repository.create(manager);

        Domain domain = InstanceHelper.createDomain(manager);
        repository.create(domain);

        DomainPermission permission = InstanceHelper.createPermission(domain);
        repository.create(permission);

        ManagerMembership membership = InstanceHelper.createManagerMembership(manager, domain, permission);
        repository.create(membership);

        return membership;
    }

    //#### FULL ENTITY TREE CREATION ####
    public static APICredential createFullManagerMembershipAPICredential(JpaRepository repository) {
        ManagerMembership membership = createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        return apiCredential;
    }

}
