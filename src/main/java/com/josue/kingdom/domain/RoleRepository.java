/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.domain;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.domain.entity.Role;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class RoleRepository extends JpaRepository {

    //TODO role should be always fetched by its level... change the related WSs to receive only the level
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Role findRole(String roleName) {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r WHERE r.name = :roleName", Role.class);
        query.setParameter("roleName", roleName);
        List<Role> roles = query.getResultList();
        return extractSingleResultFromList(roles);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Role findRole(int roleLevel) {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r WHERE r.level = :roleLevel", Role.class);
        query.setParameter("roleLevel", roleLevel);
        List<Role> roles = query.getResultList();
        return extractSingleResultFromList(roles);
    }

}
