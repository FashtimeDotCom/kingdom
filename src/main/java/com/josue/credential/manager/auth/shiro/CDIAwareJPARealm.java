/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.shiro;

import com.josue.credential.manager.account.AccountRepository;
import com.josue.credential.manager.auth.APICredential;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author Josue
 */
public class CDIAwareJPARealm extends AuthorizingRealm {

    //Here we can inject other beans because 'CDIAwareJPARealm' is CDI aware (see CustomEnvironmentLoaderListener)
    @Inject
    AccountRepository persistence;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        APICredential token = (APICredential) authToken;

        //Make use of JPA
        APICredential apiCredentialToken = persistence.find(APICredential.class, token.getApiKey());

        if (apiCredentialToken != null) {
            return new SimpleAuthenticationInfo(apiCredentialToken.getUuid(), apiCredentialToken.getCredentials(), getName());
        } else {
            return null;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
