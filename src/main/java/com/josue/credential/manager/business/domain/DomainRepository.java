/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.DomainCredential;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class DomainRepository extends JpaRepository {

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Domain> getDomainsByCredential(String credentialUuid) {
        Query query = em.createQuery("SELECT manCred.domain FROM ManagerDomainCredential manCred WHERE manCred.credential.uuid = :credentialUuid", Domain.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<Domain> resultList = query.getResultList();
        return resultList;
    }

    //Control change some data, we dont want to update it on database
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<DomainCredential> getJoinedDomainsByCredential(String credentialUuid) {
        Query query = em.createQuery("SELECT manCred FROM ManagerDomainCredential manCred WHERE manCred.credential.uuid = :credentialUuid", DomainCredential.class);
        query.setParameter("credentialUuid", credentialUuid);
        List<DomainCredential> resultList = query.getResultList();
        return resultList;
    }

    public List<Domain> getOwnedDomainsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT domain FROM Domain domain WHERE domain.owner.uuid = :managerUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        List<Domain> domains = query.getResultList();
        return domains;
    }
}
