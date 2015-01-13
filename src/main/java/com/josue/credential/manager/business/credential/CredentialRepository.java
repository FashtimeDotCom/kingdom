/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.credential;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.credential.ManagerCredential;
import com.josue.credential.manager.auth.domain.APIDomainCredential;
import com.josue.credential.manager.auth.manager.Manager;
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
public class CredentialRepository extends JpaRepository {

    //Control changes some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<APIDomainCredential> getApiCredentials(String managerUuid, Integer limit, Integer offset) {
        Query query = em.createQuery("SELECT apiDomCred FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid", APIDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<APIDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    //Control changes some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<APIDomainCredential> getApiCredentials(String managerUuid, String domainUuid, Integer limit, Integer offset) {
        Query query = em.createQuery("SELECT apiDomCred FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid AND apiDomCred.domain.uuid = :domainUuid", APIDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("domainUuid", domainUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<APIDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    //Control changes some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public APIDomainCredential getApiCredential(String managerUuid, String domainUuid, String apiKeyUuid) {
        Query query = em.createQuery("SELECT apiDomCred FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid AND apiDomCred.domain.uuid = :domainUuid AND apiDomCred.credential.uuid = :apiKeyUuid", APIDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("apiKeyUuid", apiKeyUuid);
        List<APIDomainCredential> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Manager getManager(String credentialUuid) {
        TypedQuery<Manager> query = em.createQuery("SELECT cred.manager FROM ManagerCredential cred WHERE cred.uuid = :credentialUuid", Manager.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<Manager> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public long countAPICredential(String domainUuid, String managerUuid) {
        Query query = em.createQuery("SELECT COUNT(apiDomCred.uuid) FROM APIDomainCredential apiDomCred WHERE apiDomCred.domain.uuid = :domainUuid AND apiDomCred.credential.manager.uuid = :managerUuid", Long.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("managerUuid", managerUuid);
        //We can safely execute this query using getFirstResult
        return (long) query.getSingleResult();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public long countAPICredential(String managerUuid) {
        Query query = em.createQuery("SELECT COUNT(apiDomCred.uuid) FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid", Long.class);
        query.setParameter("managerUuid", managerUuid);
        //We can safely execute this query using getFirstResult
        return (long) query.getSingleResult();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public ManagerCredential getManagerCredentialByManager(String managerUuid) {
        TypedQuery<ManagerCredential> query = em.createQuery("SELECT cred FROM ManagerCredential cred WHERE cred.manager.uuid = :managerUuid", ManagerCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<ManagerCredential> managerList = query.getResultList();
        return extractSingleResultFromList(managerList);
    }
}
