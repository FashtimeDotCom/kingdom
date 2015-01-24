/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.ManagerMembership;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
//Authentication specific Repository
@ApplicationScoped
public class AuthRepository extends JpaRepository {

    //TODO working only with email
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Manager getManager(String appKey, String email, String password) {
        Query query = em.createQuery("SELECT man FROM Manager man WHERE man.email = :email AND man.password = :password AND man.application.uuid = :appKey", Manager.class);
        query.setParameter("email", email);
        query.setParameter("password", password);
        query.setParameter("appKey", appKey);
        List<Manager> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<ManagerMembership> getManagerMemberships(String appUuid, String managerUuid) {
        Query query = em.createQuery("SELECT manMen FROM ManagerMembership manMen WHERE manMen.manager.uuid = :managerUuid AND manMen.application.uuid = :appUuid", ManagerMembership.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);

        List<ManagerMembership> resultList = query.getResultList();
        return resultList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public APICredential getAPICredentialByKey(String appUuid, String apiKey) {
        TypedQuery<APICredential> query = em.createQuery("SELECT api FROM APICredential api WHERE api.apiKey = :apiKey AND api.application.uuid = :appUuid", APICredential.class);
        query.setParameter("apiKey", apiKey);
        query.setParameter("appUuid", appUuid);
        List<APICredential> apiCredentials = query.getResultList();
        return extractSingleResultFromList(apiCredentials);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Application getApplication(String appKey, String appSecret) {
        TypedQuery<Application> query = em.createQuery("SELECT app FROM Application app WHERE app.appKey = :appKey AND app.secret = :appSecret", Application.class);
        query.setParameter("appKey", appKey);
        query.setParameter("appSecret", appSecret);
        List<Application> apiCredentials = query.getResultList();
        return extractSingleResultFromList(apiCredentials);
    }
}
