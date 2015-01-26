/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.application;

import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.AccessLevelPermission;
import com.josue.kingdom.security.AuthRepository;
import com.josue.kingdom.security.KingdomSecurity;
import com.josue.kingdom.security.KingdomSecurity.ManagerStatus;
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
public class ApplicationlRealm extends AuthorizingRealm {

    //Here we can inject other beans because 'JpaRepository' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AuthRepository persistence;

    public ApplicationlRealm() {
        setAuthenticationTokenClass(APICredential.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {

        ApplicationToken appToken = (ApplicationToken) authToken;
        char[] appSecret = (char[]) appToken.getCredentials();
        Application foundApp = persistence.getApplication((String) appToken.getPrincipal(), new String(appSecret)); //TODO this and down here
        KingdomSecurity security;
        ManagerStatus managerStatus = ManagerStatus.EMPTY;

        if (foundApp != null) {
            Manager foundManager = null;
            if (appToken.getManagerToken() != null) {
                //can be username or email
                //TODO search for email or username
                String manLogin = appToken.getManagerToken().getPrincipal().toString();
                char[] manPsw = (char[]) appToken.getManagerToken().getCredentials();
                if (manPsw.length != 0 || manLogin.length() != 0) {
                    if (manLogin.contains("@")) {//email.... TODO improve?
                        foundManager = persistence.getManagerByEmail(foundApp.getUuid(), manLogin, new String(manPsw));
                    } else {
                        foundManager = persistence.getManagerByUsername(foundApp.getUuid(), manLogin, new String(manPsw));
                    }

                    if (foundManager != null) {
                        managerStatus = ManagerStatus.AUTHENTICATED;
                    } else {
                        managerStatus = ManagerStatus.UNAUTHENTICATED;
                    }
                }
            }

            security = new KingdomSecurity(foundApp, foundManager, managerStatus);
            //Here we put the entire APICredential class, so we can fetch it using Subject subject = SecurityUtils.getSubject();
            return new SimpleAuthenticationInfo(security, foundApp.getSecret(), getName());
        }
        throw new AuthenticationException("Invalid username or password, APP: " + appToken.getPrincipal());
    }

    @Override
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
        return token instanceof ApplicationToken;
    }

}
