/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.invitation.entity.Invitation;
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

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<Domain> getJoinedDomains(String appUuid, String managerUuid, Integer limit, Integer offset) {
        Query query = em.createQuery("SELECT membership.domain FROM ManagerMembership membership WHERE membership.manager.uuid = :managerUuid AND membership.application.uuid = :appUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<Domain> resultList = query.getResultList();
        return resultList;
    }

    //Control change some data, we dont want to update it on database
    //This should return only one result
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Domain getJoinedDomain(String appUuid, String domainUuid, String managerUuid) {
        Query query = em.createQuery("SELECT membership.domain FROM ManagerMembership membership WHERE membership.manager.uuid = :managerUuid AND membership.domain.uuid = :domainUuid AND membership.application.uuid = :appUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("appUuid", appUuid);
        List<Domain> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    public Domain getOwnedDomain(String appUuid, String domainUuid, String ownerUuid) {
        Query query = em.createQuery("SELECT dom FROM Domain dom WHERE dom.uuid = :domainUuid AND dom.owner.uuid = :ownerUuid AND dom.application.uuid = :appUuid", Domain.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("ownerUuid", ownerUuid);
        query.setParameter("appUuid", appUuid);
        List<Domain> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    public List<Domain> getOwnedDomains(String appUuid, String managerUuid, Integer limit, Integer offset) {
        Query query = em.createQuery("SELECT dom FROM Domain dom WHERE dom.owner.uuid = :managerUuid AND dom.application.uuid = :appUuid", Domain.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        query.setMaxResults(limit).setFirstResult(offset);
        List<Domain> domains = query.getResultList();
        return domains;
    }

    public Long countOwnedDomains(String appUuid, String managerUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(dom.uuid) FROM Domain dom WHERE dom.owner.uuid = :managerUuid AND dom.application.uuid = :appUuid", Long.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        Long count = query.getSingleResult();
        return count;
    }

    public Long countJoinedDomains(String appUuid, String managerUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(membership.uuid) FROM ManagerMembership membership WHERE membership.manager.uuid = :managerUuid AND membership.application.uuid = :appUuid", Long.class);
        query.setParameter("managerUuid", managerUuid);
        query.setParameter("appUuid", appUuid);
        Long count = query.getSingleResult();
        return count;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<DomainPermission> getDomainPermissions(String appUuid, String domainUuid, Integer limit, Integer offset) {
        TypedQuery<DomainPermission> query = em.createQuery("SELECT r FROM DomainPermission r WHERE  r.domain.uuid = :domainUuid AND r.application.uuid = :appUuid", DomainPermission.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        List<DomainPermission> permissions = query.getResultList();
        return permissions;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public DomainPermission getDomainPermission(String appUuid, String domainUuid, String permissionName) {
        TypedQuery<DomainPermission> query = em.createQuery("SELECT r FROM DomainPermission r WHERE r.domain.uuid = :domainUuid AND r.name = :permissionName AND r.application.uuid = :appUuid", DomainPermission.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("permissionName", permissionName);
        query.setParameter("appUuid", appUuid);
        List<DomainPermission> permissions = query.getResultList();
        return extractSingleResultFromList(permissions);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public DomainPermission getDomainPermission(String appUuid, String domainUuid, int permissionLevel) {
        TypedQuery<DomainPermission> query = em.createQuery("SELECT domainperm FROM DomainPermission domainperm WHERE  domainperm.domain.uuid = :domainUuid AND domainperm.level = :permissionLevel AND domainperm.application.uuid = :appUuid", DomainPermission.class);
        query.setParameter("domainUuid", domainUuid);
        query.setParameter("permissionLevel", permissionLevel);
        query.setParameter("appUuid", appUuid);
        List<DomainPermission> permissions = query.getResultList();
        return extractSingleResultFromList(permissions);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void purgeDomain(String appUuid, String domainUuid) {

        Query apiCredentialQuery = em.createQuery("SELECT apicred FROM APICredential apicred WHERE apicred.membership.domain.uuid = :domainUuid AND apicred.application.uuid = :appUuid", APICredential.class);
        apiCredentialQuery.setParameter("domainUuid", domainUuid);
        apiCredentialQuery.setParameter("appUuid", appUuid);
        List<APICredential> resultList = apiCredentialQuery.getResultList();
        for (APICredential apicred : resultList) {
            em.remove(apicred);
        }

        Query membershipQuery = em.createQuery("SELECT membership FROM ManagerMembership membership WHERE membership.domain.uuid = :domainUuid AND membership.application.uuid = :appUuid", ManagerMembership.class);
        membershipQuery.setParameter("domainUuid", domainUuid);
        membershipQuery.setParameter("appUuid", appUuid);
        List<ManagerMembership> memberships = membershipQuery.getResultList();
        for (ManagerMembership membership : memberships) {
            em.remove(membership);
        }

        Query invitationQuery = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.domain.uuid = :domainUuid AND inv.application.uuid = :appUuid", Invitation.class);
        invitationQuery.setParameter("domainUuid", domainUuid);
        invitationQuery.setParameter("appUuid", appUuid);
        List<Invitation> invitations = invitationQuery.getResultList();
        for (Invitation invitation : invitations) {
            em.remove(invitation);
        }

        Query domainPermissionQuery = em.createQuery("SELECT domainperm FROM DomainPermission domainperm WHERE domainperm.domain.uuid = :domainUuid AND domainperm.application.uuid = :appUuid", DomainPermission.class);
        domainPermissionQuery.setParameter("domainUuid", domainUuid);
        domainPermissionQuery.setParameter("appUuid", appUuid);
        List<DomainPermission> domainPermissions = domainPermissionQuery.getResultList();
        for (DomainPermission domainPermission : domainPermissions) {
            em.remove(domainPermission);
        }

        Query domainQuery = em.createQuery("SELECT dom FROM Domain dom WHERE dom.uuid = :domainUuid AND dom.application.uuid = :appUuid", Domain.class);
        domainQuery.setParameter("domainUuid", domainUuid);
        domainQuery.setParameter("appUuid", appUuid);
        List<Domain> domains = domainQuery.getResultList();
        for (Domain domain : domains) {
            em.remove(domain);
        }
    }

}
