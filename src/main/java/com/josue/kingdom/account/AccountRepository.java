/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.account;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.account.entity.Manager;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class AccountRepository extends JpaRepository {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<Manager> getManagers(Integer limit, Integer offset) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man", Manager.class);
        query.setMaxResults(limit).setFirstResult(offset);
        List<Manager> managers = query.getResultList();
        return managers;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Manager getManagerByEmail(String email) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man WHERE man.email = :email", Manager.class);
        query.setParameter("email", email);
        List<Manager> managers = query.getResultList();
        return extractSingleResultFromList(managers);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Manager getManagerByLogin(String login) {
        TypedQuery<Manager> query = em.createQuery("SELECT manCred.manager FROM ManagerCredential manCred WHERE manCred.login = :login", Manager.class);
        query.setParameter("login", login);
        List<Manager> managers = query.getResultList();
        return extractSingleResultFromList(managers);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Manager getManagerByCredential(String credentialUuid) {
        TypedQuery<Manager> query = em.createQuery("SELECT cred.manager FROM ManagerCredential cred WHERE cred.uuid = :credentialUuid", Manager.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<Manager> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }
}
