/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
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
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class CredentialRepository extends JpaRepository {

    public List<APICredential> getAPICredentials(String appUuid, String domainUuid, Integer limit, Integer offset) {
        TypedQuery<APICredential> query = em.createQuery("SELECT api FROM APICredential api WHERE api.membership.domain.uuid = :domainUuid AND api.application.uuid = :appUuid", APICredential.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<APICredential> memberships = query.getResultList();
        return memberships;
    }

    public List<APICredential> getAPICredentials(String appUuid, String domainUuid, String managerUuid, Integer limit, Integer offset) {
        TypedQuery<APICredential> query = em.createQuery("SELECT api FROM APICredential api WHERE api.membership.domain.uuid = :domainUuid AND api.membership.manager.uuid = :managerUuid AND api.application.uuid = :appUuid", APICredential.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("appUuid", appUuid);
        query.setParameter("managerUuid", managerUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<APICredential> memberships = query.getResultList();
        return memberships;
    }

    //Control changes some data, we dont want to update it on database
    public APICredential getAPICredential(String appUuid, String apiCredUuid) {
        Query query = em.createQuery("SELECT apicred FROM APICredential apicred WHERE apicred.uuid = :apiCredUuid AND apicred.application.uuid = :appUuid", APICredential.class);
        query.setParameter("apiCredUuid", apiCredUuid);
        query.setParameter("appUuid", appUuid);
        List<APICredential> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    public long countAPICredential(String appUuid, String domainUuid, String managerUuid) {
        Query query = em.createQuery("SELECT COUNT(apicred.uuid) FROM APICredential apicred WHERE apicred.application.uuid = :appUuid AND apicred.membership.domain.uuid = :domainUuid AND apicred.membership.manager.uuid = :managerUuid", Long.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        return (long) query.getSingleResult();
    }

    public long countAPICredential(String appUuid, String domainUuid) {
        Query query = em.createQuery("SELECT COUNT(apicred.uuid) FROM APICredential apicred WHERE apicred.membership.domain.uuid = :domainUuid", Long.class);
        query.setParameter("domainUuid", domainUuid);
        return (long) query.getSingleResult();
    }

    public List<Manager> getManagers(String appUuid, Integer limit, Integer offset) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man WHERE man.application.uuid = :appUuid", Manager.class);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<Manager> managers = query.getResultList();
        return managers;
    }

    public Manager getManagerByEmail(String appUuid, String email) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man WHERE man.email = :email AND man.application.uuid = :appUuid", Manager.class);
        query.setParameter("email", email);
        query.setParameter("appUuid", appUuid);
        List<Manager> managers = query.getResultList();
        return extractSingleResultFromList(managers);
    }

    public Manager getManagerByUsername(String appUuid, String username) {
        TypedQuery<Manager> query = em.createQuery("SELECT man FROM Manager man WHERE man.application.uuid = :appUuid AND man.username = :username", Manager.class);
        query.setParameter("username", username);
        query.setParameter("appUuid", appUuid);
        List<Manager> managers = query.getResultList();
        return extractSingleResultFromList(managers);
    }

    public ManagerMembership getManagerMembership(String appUuid, String domainUuid, String managerUuid) {
        TypedQuery<ManagerMembership> query = em.createQuery("SELECT man FROM ManagerMembership man WHERE man.application.uuid = :appUuid AND man.domain.uuid = :domainUuid AND man.manager.uuid = :managerUuid", ManagerMembership.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        List<ManagerMembership> memberships = query.getResultList();
        return extractSingleResultFromList(memberships);
    }

    public List<ManagerMembership> getManagerMembershipByManager(String appUuid, String managerUuid, Integer limit, Integer offset) {
        TypedQuery<ManagerMembership> query = em.createQuery("SELECT man FROM ManagerMembership man WHERE man.application.uuid = :appUuid AND man.manager.uuid = :managerUuid", ManagerMembership.class);
        query.setParameter("appUuid", appUuid);
        query.setParameter("managerUuid", managerUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<ManagerMembership> memberships = query.getResultList();
        return memberships;
    }

    public List<ManagerMembership> getManagerMembershipByDomain(String appUuid, String domainUuid, Integer limit, Integer offset) {
        TypedQuery<ManagerMembership> query = em.createQuery("SELECT man FROM ManagerMembership man WHERE man.application.uuid = :appUuid AND man.domain.uuid = :domainUuid", ManagerMembership.class);
        query.setParameter("appUuid", appUuid);
        query.setParameter("domainUuid", domainUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<ManagerMembership> memberships = query.getResultList();
        return memberships;
    }

    public PasswordChangeEvent getPasswordResetEvent(String appUuid, String token) {
        TypedQuery<PasswordChangeEvent> query = em.createQuery("SELECT pre FROM PasswordChangeEvent pre WHERE pre.application.uuid = :appUuid AND pre.token = :token", PasswordChangeEvent.class);
        query.setParameter("appUuid", appUuid);
        query.setParameter("token", token);
        List<PasswordChangeEvent> events = query.getResultList();
        return extractSingleResultFromList(events);
    }

    public List<PasswordChangeEvent> getPasswordResetEvents(String appUuid, String targetManagerUuid) {
        TypedQuery<PasswordChangeEvent> query = em.createQuery("SELECT pre FROM PasswordChangeEvent pre WHERE pre.application.uuid = :appUuid AND pre.targetManager.uuid = :targetManagerUuid ORDER BY pre.dateCreated", PasswordChangeEvent.class);
        query.setParameter("appUuid", appUuid);
        query.setParameter("targetManagerUuid", targetManagerUuid);
        List<PasswordChangeEvent> events = query.getResultList();
        return events;
    }

}
