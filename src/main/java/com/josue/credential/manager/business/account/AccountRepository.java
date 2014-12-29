/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.credential.APIDomainCredential;
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
public class AccountRepository extends JpaRepository {

    //Control change some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<APIDomainCredential> getApiCredentialsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT apiDomCred FROM APIDomainCredential apiDomCred WHERE apiDomCred.credential.manager.uuid = :managerUuid", APIDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<APIDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Manager getManagerByCredential(String credentialUuid) {
        TypedQuery<Manager> query = em.createQuery("SELECT cred.manager FROM ManagerCredential cred WHERE cred.uuid = :credentialUuid", Manager.class);
        query.setParameter("credentialUuid", credentialUuid);
        Manager manager = query.getSingleResult();
        return manager;
    }
}
