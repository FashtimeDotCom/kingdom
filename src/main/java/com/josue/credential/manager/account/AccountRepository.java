/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.APICredential;
import com.josue.credential.manager.auth.ManagerDomainCredential;
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

    public List<ManagerDomainCredential> getManagerCredentials(String managerUuid) {
        Query query = em.createQuery("SELECT mdc FROM ManagerDomainCredential mdc WHERE MDC.uuid = :managerUuid", ManagerDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<ManagerDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    public APICredential findByToken(String token) {
        TypedQuery<APICredential> query = em.createQuery("SELECT cred FROM APICredential cred WHERE cred.apiKey = :token", APICredential.class);
        query.setParameter("token", token);
        return query.getResultList().get(0);
    }
}
