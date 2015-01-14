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
    public List<Invitation> getInvitations(String authorUuid, Integer limit, Integer offset) {
        TypedQuery<Invitation> query = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.authorManager.uuid = :authorUuid", Invitation.class);
        query.setParameter("authorUuid", authorUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        return query.getResultList();
    }

    public Invitation getInvitationByToken(String token) {
        TypedQuery<Invitation> query = em.createQuery("SELECT inv FROM Invitation inv WHERE inv.token = :token", Invitation.class);
        query.setParameter("token", token);
        List<Invitation> result = query.getResultList();
        return extractSingleResultFromList(result);
    }

    public long getInvitationsCount(String authorUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(inv.uuid) FROM Invitation inv WHERE inv.authorManager.uuid = :authorUuid", Long.class);
        query.setParameter("authorUuid", authorUuid);
        return query.getFirstResult();
    }

}
