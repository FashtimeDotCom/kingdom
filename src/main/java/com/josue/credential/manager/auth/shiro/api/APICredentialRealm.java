/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.shiro.api;

import com.josue.credential.manager.account.AccountRepository;
import com.josue.credential.manager.auth.APICredential;
import com.josue.credential.manager.auth.shiro.AccessLevelPermission;
import java.util.Arrays;
import java.util.HashSet;
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
public class APICredentialRealm extends AuthorizingRealm {

    //Here we can inject other beans because 'JpaRepository' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AccountRepository persistence;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        APICredential token = (APICredential) authToken;

        //Make use of JPA
        APICredential apiCredentialToken = persistence.find(APICredential.class, token.getApiKey());

        if (apiCredentialToken != null) {
            return new SimpleAuthenticationInfo(apiCredentialToken.getUuid(), apiCredentialToken.getCredentials(), getName());
        }
        throw new AuthenticationException("Not credential found for APIKEY: " + token.getApiKey());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO check if can reuse the fetched entity, and if its safe
        String principalToken = (String) getAvailablePrincipal(principals);

        //TODO improve this... should not fetch the entire entity to use the Role
        APICredential fetchedCredential = persistence.find(APICredential.class, principalToken);
        //Role role = fetchedCredential.getRole();

        //TODO how o check which resource manager is accessing ?
        String fetchedDomainName = "uuid-doc-123-TODO-check-if-OK";
        // ... multiple permissions map

        AccessLevelPermission perm = new AccessLevelPermission();
        perm.addAccessLevel(fetchedDomainName, null);;//TODO null... here goes the role

        Set<Permission> permissions = new HashSet<>();
        permissions.add(perm);
        info.setObjectPermissions(permissions);

        info.setRoles(new HashSet<>(Arrays.asList(fetchedDomainName)));
        return info;
    }
}
