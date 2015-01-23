/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.invitation.entity.Invitation;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
public class InvitationRepository extends JpaRepository {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<Invitation> getInvitations(String appUuid, String authorUuid, Integer limit, Integer offset) {
        TypedQuery<Invitation> query = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.authorManager.uuid = :authorUuid AND inv.application.uuid = :appUuid", Invitation.class);
        query.setParameter("authorUuid", authorUuid);
        query.setParameter("appUuid", appUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        return query.getResultList();
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Invitation getInvitation(String appUuid, String domainUuid, String targetEmail) {
        TypedQuery<Invitation> query = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.targetManager.email = :targetEmail AND inv.domain.uuid = :domainUuid AND inv.application.uuid = :appUuid", Invitation.class);
        query.setParameter("appUuid", appUuid);
        query.setParameter("targetEmail", targetEmail);
        query.setParameter("domainUuid", domainUuid);
        List<Invitation> resultList = query.getResultList();
        return extractSingleResultFromList(resultList);
    }

    public Invitation getInvitationByToken(String appUuid, String token) {
        TypedQuery<Invitation> query = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.token = :token AND inv.application.uuid = :appUuid", Invitation.class);
        query.setParameter("token", token);
        query.setParameter("appUuid", appUuid);
        List<Invitation> result = query.getResultList();
        return extractSingleResultFromList(result);
    }

    public long getInvitationsCount(String appUuid, String authorUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(inv.uuid) FROM Invitation inv WHERE inv.authorManager.uuid = :authorUuid AND inv.application.uuid = :appUuid", Long.class);
        query.setParameter("authorUuid", authorUuid);
        query.setParameter("appUuid", appUuid);
        return query.getSingleResult();
    }

}
