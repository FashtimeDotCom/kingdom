/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro;

import com.josue.kingdom.domain.entity.DomainRole;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authz.Permission;

/**
 *
 * @author Josue
 */
public class AccessLevelPermission implements Permission {

    private final Map<Object, DomainRole> accessLevels;

    public AccessLevelPermission(Map<Object, DomainRole> accessLevels) {
        this.accessLevels = accessLevels;
    }

    public AccessLevelPermission(Object domain, DomainRole role) {
        this.accessLevels = new HashMap<>();
        this.accessLevels.put(domain, role);
    }

    public AccessLevelPermission() {
        this.accessLevels = new HashMap<>();
    }

    @Override
    public boolean implies(Permission p) {
        if (!(p instanceof AccessLevelPermission)) {
            return false;
        }
        AccessLevelPermission permission = (AccessLevelPermission) p;

        for (Map.Entry<Object, DomainRole> entry : permission.getAccessLevels().entrySet()) {
            Object domain = entry.getKey();
            DomainRole requiredRole = entry.getValue();

            if (this.getAccessLevels().containsKey(domain)) {
                DomainRole thisRole = this.getAccessLevels().get(domain);
                if (thisRole.getLevel() >= requiredRole.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<Object, DomainRole> getAccessLevels() {
        return accessLevels;
    }

    public boolean addAccessLevel(Object key, DomainRole role) {
        return this.accessLevels.put(key, role) != null;
    }

}
