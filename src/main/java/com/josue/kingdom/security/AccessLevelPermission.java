/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

import com.josue.kingdom.domain.entity.DomainPermission;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authz.Permission;

/**
 *
 * @author Josue
 */
public class AccessLevelPermission implements Permission {

    private final Map<Object, DomainPermission> accessLevels;

    public AccessLevelPermission(Map<Object, DomainPermission> accessLevels) {
        this.accessLevels = accessLevels;
    }

    public AccessLevelPermission(Object domain, DomainPermission permission) {
        this.accessLevels = new HashMap<>();
        this.accessLevels.put(domain, permission);
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

        for (Map.Entry<Object, DomainPermission> entry : permission.getAccessLevels().entrySet()) {
            Object domain = entry.getKey();
            DomainPermission requiredPermission = entry.getValue();

            if (this.getAccessLevels().containsKey(domain)) {
                DomainPermission thisPermission = this.getAccessLevels().get(domain);
                if (thisPermission.getLevel() >= requiredPermission.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<Object, DomainPermission> getAccessLevels() {
        return accessLevels;
    }

    public boolean addAccessLevel(Object key, DomainPermission permission) {
        return this.accessLevels.put(key, permission) != null;
    }

}
