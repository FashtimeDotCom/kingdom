/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.account;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.manager.Manager;
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
    public Manager findManagerByEmail(String email) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man WHERE man.email = :email", Manager.class);
        query.setParameter("email", email);
        List<Manager> managers = query.getResultList();
        return extractSingleResultFromList(managers);
    }

}
