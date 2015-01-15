/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro.api;

import com.josue.kingdom.credential.AuthRepository;
import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.Credential;
import com.josue.kingdom.credential.entity.ManagerCredential;
import com.josue.kingdom.domain.entity.DomainCredential;
import com.josue.kingdom.domain.entity.DomainRole;
import com.josue.kingdom.shiro.AccessLevelPermission;
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
public class APICredentialRealm extends AuthorizingRealm {

    //Here we can inject other beans because 'JpaRepository' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AuthRepository persistence;

    public APICredentialRealm() {
        setAuthenticationTokenClass(APICredential.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        APICredential token = (APICredential) authToken;

        //Make use of JPA
        APICredential foundApiCredential = persistence.getAPICredentialByToken(token.getApiKey());

        if (foundApiCredential != null) {
            //Here we put the entire APICredential class, so we can fetch it using Subject subject = SecurityUtils.getSubject();
            return new SimpleAuthenticationInfo(foundApiCredential, foundApiCredential.getCredentials(), getName());
        }
        throw new AuthenticationException("No credential found for APIKEY: " + token.getApiKey());
    }
    /*
     This method actually validate the TWO available credentials:
     APICredential type: When is purelly restful, and
     ManagerCredential type: When user is already logged in and whe need a Permissin check for a rest action based on ManagerCredentials
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO check if can reuse the fetched entity, and if its safe

        Credential credential;
        List<DomainCredential> domainCredentials;
        Object availablePrincipal = getAvailablePrincipal(principals);

        if (availablePrincipal instanceof APICredential) {

            credential = (APICredential) getAvailablePrincipal(principals);
            domainCredentials = persistence.getAPIDomainCredentials(credential.getUuid());

        } /*
         Returns AuthorizationInfo for ManagerCredential based Permission Check
         */ else if (availablePrincipal instanceof ManagerCredential) {
            credential = (ManagerCredential) getAvailablePrincipal(principals);
            domainCredentials = persistence.getManagerDomainCredentials(credential.getUuid());
        } else {
            throw new AuthenticationException("Illegal Credential type");
        }

        Map<Object, DomainRole> roles = new HashMap<>();
        for (DomainCredential domainCredential : domainCredentials) {
            roles.put(domainCredential.getDomain().getUuid(), domainCredential.getRole());
        }

//        String fetchedDomainName = "uuid-doc-123-TODO-check-if-OK";
        AccessLevelPermission permissions = new AccessLevelPermission(roles);

        Set<Permission> permSet = new HashSet<>();
        permSet.add(permissions);
        info.setObjectPermissions(permSet);

        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof APICredential;
    }

}
