/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.role;

import com.josue.credential.manager.JpaRepository;
import com.josue.credential.manager.auth.role.Role;
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

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Role findRoleByName(String roleName) {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r WHERE r.name = :roleName", Role.class);
        query.setParameter("roleName", roleName);
        List<Role> roles = query.getResultList();
        return extractSingleResultFromList(roles);
    }

}
