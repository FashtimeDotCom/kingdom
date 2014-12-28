/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.manager.Manager;
import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.credential.APIDomainCredential;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainCredential;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)//TODO chage
public class AccountRepository extends JpaRepository {

    //Control change some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<DomainCredential> getJoinedDomainsByCredential(String credentialUuid) {
        Query query = em.createQuery("SELECT manCred FROM ManagerDomainCredential manCred WHERE manCred.credential.uuid = :credentialUuid", DomainCredential.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<DomainCredential> resultList = query.getResultList();
        return resultList;
    }

    //Control change some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<APIDomainCredential> getApiCredentialsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT apiDomCred FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid", APIDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<APIDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    public List<Domain> getDomainsByCredential(String credentialUuid) {
        Query query = em.createQuery("SELECT manCred.domain FROM ManagerDomainCredential manCred WHERE manCred.credential.uuid = :credentialUuid", Domain.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<Domain> resultList = query.getResultList();
        return resultList;
    }

    public Manager getManagerByCredential(String credentialUuid) {
        TypedQuery<Manager> query = em.createQuery("SELECT cred.manager FROM ManagerCredential cred WHERE cred.uuid = :credentialUuid", Manager.class);
        query.setParameter("credentialUuid", credentialUuid);
        Manager manager = query.getSingleResult();
        return manager;
    }

    public List<Domain> getOwnedDomainsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT domain FROM Domain domain WHERE domain.owner.uuid = :managerUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        List<Domain> domains = query.getResultList();
        return domains;
    }

}
