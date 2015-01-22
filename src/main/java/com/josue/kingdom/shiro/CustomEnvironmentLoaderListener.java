/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.shiro;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

//Check web.xml listener
public class CustomEnvironmentLoaderListener extends EnvironmentLoaderListener {

    private static final Logger LOG = Logger.getLogger(CustomEnvironmentLoaderListener.class.getName());

    @Inject
    private ApplicationlRealm apiCredentialJPARealm;

    //http://stackoverflow.com/questions/15605038/unable-to-inject-my-dao-in-a-custom-apache-shiro-authorizingrealm
    @Override
    protected WebEnvironment createEnvironment(ServletContext pServletContext) {

        LOG.log(Level.INFO, "########## INITIALIZING CUSTOMENVIRONMENTLOADERLISTENER ##########");

        WebEnvironment environment = super.createEnvironment(pServletContext);
        RealmSecurityManager rsm = (RealmSecurityManager) environment.getSecurityManager();
        //TODO check for how to read from shiro.ini

//        PasswordService passwordService = new DefaultPasswordService();
//        PasswordMatcher passwordMatcher = new PasswordMatcher();
//        passwordMatcher.setPasswordService(passwordService);
//        jpaRealm.setCredentialsMatcher(passwordMatcher);
        //TODO add another realms here
        Set<Realm> realms = new HashSet<>();
        realms.add(apiCredentialJPARealm);
        rsm.setRealms(realms);

        rsm.setCacheManager(new MemoryConstrainedCacheManager());

        ((DefaultWebEnvironment) environment).setSecurityManager(rsm);

        return environment;
    }

}
