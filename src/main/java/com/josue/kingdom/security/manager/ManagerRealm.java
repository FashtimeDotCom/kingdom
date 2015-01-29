/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.manager;

import com.josue.kingdom.credential.entity.AccountStatus;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.AccessLevelPermission;
import com.josue.kingdom.security.AuthRepository;
import com.josue.kingdom.security.KingdomSecurity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author Josue
 */
public class ManagerRealm extends AuthorizingRealm {

    //Here we can inject other beans because 'JpaRepository' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AuthRepository persistence;

    public ManagerRealm() {
        setAuthenticationTokenClass(ManagerToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {

        ManagerToken managerToken = (ManagerToken) authToken;

        if (managerToken.getAppUuid() == null) {
            throw new AuthenticationException("Application uuid is needed to authenticate user");
        }

        Manager foundManager;
        String login = managerToken.getPrincipal().toString();
        String password = new String((char[]) managerToken.getCredentials());

        if (managerToken.getType().equals(ManagerToken.CredentialType.EMAIL)) {//email.... TODO improve?
            foundManager = persistence.getManagerByEmail(managerToken.getAppUuid(), login, password);
        } else {
            foundManager = persistence.getManagerByUsername(managerToken.getAppUuid(), login, password);
        }

        if (foundManager == null) {
            throw new AuthenticationException("Invalid username or password, login: " + managerToken.getPrincipal());
        }
        if (!foundManager.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AuthenticationException("Inactive user: " + managerToken.getPrincipal());
        }

        //Here we put the entire APICredential class, so we can fetch it using Subject subject = SecurityUtils.getSubject();
        return new SimpleAuthenticationInfo(foundManager, managerToken.getCredentials(), getName());

    }

    @Override//TODO implement ? i think is not needed
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Object availablePrincipal = getAvailablePrincipal(principals);
        KingdomSecurity kingdomSecurity = (KingdomSecurity) availablePrincipal;

        Manager currentManager;
        try {//TODO hidding exception ? how to throw to REST endpoint
            currentManager = kingdomSecurity.getCurrentManager();
        } catch (RestException ex) {
            return info;
        }

        List<ManagerMembership> memberships = persistence.getManagerMemberships(kingdomSecurity.getCurrentApplication().getUuid(), currentManager.getUuid());

        Map<Object, DomainPermission> permissions = new HashMap<>();
        for (ManagerMembership membership : memberships) {
            permissions.put(membership.getDomain().getUuid(), membership.getPermission());
        }
        AccessLevelPermission permissionsLevel = new AccessLevelPermission(permissions);

        Set<Permission> permSet = new HashSet<>();
        permSet.add(permissionsLevel);
        info.setObjectPermissions(permSet);

        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof ManagerToken;
    }

}
