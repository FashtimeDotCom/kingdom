/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.credential.APICredential;
import com.josue.credential.manager.auth.credential.ManagerCredential;
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
public class AuthRepository extends JpaRepository {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public APICredential findApiCredentialByToken(String token) {
        TypedQuery<APICredential> query = em.createQuery("SELECT cred FROM APICredential cred WHERE cred.apiKey = :token", APICredential.class);
        query.setParameter("token", token);
        return query.getResultList().get(0);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<DomainCredential> getApiDomainCredentials(String credentialUuid) {
        Query query = em.createQuery("SELECT apiCred FROM APIDomainCredential apiCred WHERE apiCred.credential.uuid = :credentialUuid", DomainCredential.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<DomainCredential> resultList = query.getResultList();
        return resultList;
    }

    //******* Manager credential ********
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public ManagerCredential findManagerCredentialByLogin(String login) {
        TypedQuery<ManagerCredential> query = em.createQuery("SELECT cred FROM ManagerCredential cred WHERE cred.login = :login", ManagerCredential.class);
        query.setParameter("login", login);
        return query.getResultList().get(0);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<DomainCredential> getManagerDomainCredentials(String credentialUuid) {
        Query query = em.createQuery("SELECT manCred FROM ManagerDomainCredential manCred WHERE manCred.credential.uuid = :credentialUuid", DomainCredential.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<DomainCredential> resultList = query.getResultList();
        return resultList;
    }
}
