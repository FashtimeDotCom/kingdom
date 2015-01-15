/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro.web;

import com.josue.kingdom.credential.AuthRepository;
import com.josue.kingdom.credential.entity.ManagerCredential;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
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
    AuthRepository persistence;

    public ManagerCredentialRealm() {
        setAuthenticationTokenClass(UsernamePasswordToken.class);

    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
//org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        ManagerCredential token;
        if (authToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken upt = (UsernamePasswordToken) authToken;
            token = new ManagerCredential(upt.getUsername(), new String(upt.getPassword()));
        } else if (authToken instanceof ManagerCredential) {
            token = (ManagerCredential) authToken;
        } else {
            throw new AuthenticationException("Invalid authentication token");
        }

        //Make use of JPA
        ManagerCredential foundApiCredential = persistence.getManagerCredentialByLogin(token.getLogin());

        if (foundApiCredential != null) {
            //Here we put the entire APICredential class, so we can fetch it using Subject subject = SecurityUtils.getSubject();
            return new SimpleAuthenticationInfo(foundApiCredential, foundApiCredential.getCredentials(), getName());
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
        return token instanceof ManagerCredential || token instanceof UsernamePasswordToken;
    }

}
