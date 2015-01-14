/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.invitation;

import com.josue.credential.manager.JpaRepository;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
public class InvitationRepository extends JpaRepository {

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<ManagerInvitation> getInvitations(String authorUuid, Integer limit, Integer offset) {
        TypedQuery<ManagerInvitation> query = em.createQuery("SELECT inv FROM ManagerInvitation inv WHERE inv.authorManager.uuid = :authorUuid", ManagerInvitation.class);
        query.setParameter("authorUuid", authorUuid);
        query.setMaxResults(limit).setFirstResult(offset);
        return query.getResultList();
    }

    public ManagerInvitation getInvitationByToken(String token) {
        TypedQuery<ManagerInvitation> query = em.createQuery("SELECT inv FROM ManagerInvitation inv WHERE inv.token = :token", ManagerInvitation.class);
        query.setParameter("token", token);
        List<ManagerInvitation> result = query.getResultList();
        return extractSingleResultFromList(result);
    }

    public long getInvitationsCount(String authorUuid) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(inv.uuid) FROM ManagerInvitation inv WHERE inv.authorManager.uuid = :authorUuid", Long.class);
        query.setParameter("authorUuid", authorUuid);
        return query.getFirstResult();
    }

}
