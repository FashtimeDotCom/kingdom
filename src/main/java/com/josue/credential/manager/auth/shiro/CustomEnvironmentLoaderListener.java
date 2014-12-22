/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.auth.shiro;

import com.josue.credential.manager.auth.shiro.api.APICredentialRealm;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

//Check web.xml listener
public class CustomEnvironmentLoaderListener extends EnvironmentLoaderListener {

    private static final Logger LOG = Logger.getLogger(CustomEnvironmentLoaderListener.class.getName());

    @Inject
    private APICredentialRealm jpaRealm;

    //http://stackoverflow.com/questions/15605038/unable-to-inject-my-dao-in-a-custom-apache-shiro-authorizingrealm
    @Override
    protected WebEnvironment createEnvironment(ServletContext pServletContext) {
        WebEnvironment environment = super.createEnvironment(pServletContext);
        RealmSecurityManager rsm = (RealmSecurityManager) environment.getSecurityManager();

        PasswordService passwordService = new DefaultPasswordService();
        PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(passwordService);

        jpaRealm.setCredentialsMatcher(passwordMatcher);
        rsm.setRealm(jpaRealm);
        ((DefaultWebEnvironment) environment).setSecurityManager(rsm);

        LOG.log(Level.INFO, "########## INITIALIZING CustomEnvironmentLoaderListener ##########");
        return environment;
    }

}
