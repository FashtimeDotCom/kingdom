/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.shiro.web;

import com.josue.credential.manager.account.AccountRepository;
import com.josue.credential.manager.auth.ManagerCredential;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author Josue
 */
public class ManagerCredentialRealm extends AuthorizingRealm {

    //Here we can inject other beans because 'JpaRepository' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AccountRepository persistence;

    public ManagerCredentialRealm() {
        setAuthenticationTokenClass(ManagerCredential.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        ManagerCredential token = (ManagerCredential) authToken;

        //Make use of JPA
        ManagerCredential foundApiCredential = persistence.findManagerCredentialByLogin(token.getLogin());

        if (foundApiCredential != null) {
            return new SimpleAuthenticationInfo(foundApiCredential.getUuid(), foundApiCredential.getCredentials(), getName());
        }
        throw new AuthenticationException("No credential found for login: " + token.getLogin());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        //TODO check if can reuse the fetched entity, and if its safe
//        String principalUuid = (String) getAvailablePrincipal(principals);
//
//        //TODO improve this... should not fetch the entire entity to use the Role
//        List<ManagerDomainCredential> managerCredentials = persistence.getApiDomainCredentials(principalUuid);
//        Map<Object, Role> roles = new HashMap<>();
//        for (APIDomainCredential adc : managerCredentials) {
//            roles.put(adc.getDomain().getUuid(), adc.getRole());
//        }
//
////        String fetchedDomainName = "uuid-doc-123-TODO-check-if-OK";
//        AccessLevelPermission permissions = new AccessLevelPermission(roles);
//
//        Set<Permission> permSet = new HashSet<>();
//        permSet.add(permissions);
//        info.setObjectPermissions(permSet);
////        info.setRoles(new HashSet<>(Arrays.asList(fetchedDomainName)));
        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof ManagerCredential;
    }

}
