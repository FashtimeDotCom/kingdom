/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.domain;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.domain.Domain;
import com.josue.credential.manager.auth.domain.ManagerDomainCredential;
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
    public List<ManagerDomainCredential> getJoinedDomainsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT manCred FROM ManagerDomainCredential manCred WHERE manCred.credential.manager.uuid = :managerUuid", ManagerDomainCredential.class);
        query.setParameter("managerUuid", managerUuid);
        List<ManagerDomainCredential> resultList = query.getResultList();
        return resultList;
    }

    public List<Domain> getOwnedDomainsByManager(String managerUuid) {
        Query query = em.createQuery("SELECT domain FROM Domain domain WHERE domain.owner.uuid = :managerUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        List<Domain> domains = query.getResultList();
        return domains;
    }

    public String getDomainUuidByName(String domainName) {
        Query query = em.createQuery("SELECT dom.uuid FROM Domain dom WHERE dom.name = :domainName", String.class);
        query.setParameter("domainName", domainName);
        String domainsUuid = extractSingleResultFromList(query);
        return domainsUuid;
    }

    public Long countDomainCredentials(String managerUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(manCred.uuid) FROM ManagerDomainCredential manCred WHERE manCred.credential.manager.uuid = :credentialUuid", Long.class);
        query.setParameter("credentialUuid", managerUuid);
        Long count = query.getSingleResult();
        return count;
    }

    public Long countOwnedDomains(String managerUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(domain.uuid) FROM Domain domain WHERE domain.owner.uuid = :managerUuid", Long.class);
        query.setParameter("managerUuid", managerUuid);
        Long count = query.getSingleResult();
        return count;
    }
}
