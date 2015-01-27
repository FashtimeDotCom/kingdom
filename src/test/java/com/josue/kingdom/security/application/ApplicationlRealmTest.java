/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security.application;

import com.josue.kingdom.security.manager.ManagerToken;
import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.rest.ex.HeaderRequiredException;
import com.josue.kingdom.rest.ex.RestException;
import com.josue.kingdom.security.AccessLevelPermission;
import com.josue.kingdom.security.AuthRepository;
import com.josue.kingdom.security.KingdomSecurity;
import java.util.ArrayList;
import java.util.List;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Josue
 */
public class ApplicationlRealmTest {

    @Mock
    AuthRepository persistence;

    @InjectMocks
    ApplicationlRealm realm = new ApplicationlRealm();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = AuthenticationException.class)
    public void testDoGetAuthenticationInfoAppNotFound() {
        String appKey = "appKey";
        char[] appSecret = "app-secret".toCharArray();
        ManagerToken manToken = new ManagerToken("man-username", "man-pass123".toCharArray());
        ApplicationToken appToken = new ApplicationToken(appKey, appSecret, manToken);

        when(persistence.getApplication((String) appToken.getPrincipal(), new String(appSecret))).thenReturn(null);
        realm.doGetAuthenticationInfo(appToken);
        fail();

    }

    @Test
    public void testDoGetAuthenticationInfoNoManagerToken() {
        String appKey = "appKey";
        char[] appSecret = "app-secret".toCharArray();
        ApplicationToken appToken = new ApplicationToken(appKey, appSecret);

        ApplicationToken spyAppToken = Mockito.spy(appToken);
        Application app = Mockito.mock(Application.class);

        when(persistence.getApplication((String) spyAppToken.getPrincipal(), new String(appSecret))).thenReturn(app);

        AuthenticationInfo info = realm.doGetAuthenticationInfo(appToken);

        PrincipalCollection principals = info.getPrincipals();
        assertTrue(principals.getPrimaryPrincipal() instanceof KingdomSecurity);
        KingdomSecurity security = (KingdomSecurity) principals.getPrimaryPrincipal();
        assertEquals(app, security.getCurrentApplication());

        try {
            security.getCurrentManager();
            fail();
        } catch (RestException ex) {
            assertTrue(ex instanceof HeaderRequiredException);
        }
    }

    @Test
    public void testDoGetAuthenticationInfoUnauthenticated() {

        String manEmail = "man@email.com";
        char[] manPassword = "man-pass123".toCharArray();
        ManagerToken manToken = new ManagerToken(manEmail, manPassword);

        String appKey = "appKey";
        char[] appSecret = "app-secret".toCharArray();
        ApplicationToken appToken = new ApplicationToken(appKey, appSecret, manToken);

        ApplicationToken spyAppToken = Mockito.spy(appToken);
        Application app = Mockito.mock(Application.class);

        when(persistence.getApplication((String) spyAppToken.getPrincipal(), new String(appSecret))).thenReturn(app);
        when(persistence.getManagerByEmail(appKey, manEmail, new String(manPassword))).thenReturn(null);

        AuthenticationInfo info = realm.doGetAuthenticationInfo(appToken);

        PrincipalCollection principals = info.getPrincipals();
        assertTrue(principals.getPrimaryPrincipal() instanceof KingdomSecurity);
        KingdomSecurity security = (KingdomSecurity) principals.getPrimaryPrincipal();
        assertEquals(app, security.getCurrentApplication());
        assertEquals(KingdomSecurity.ManagerStatus.UNAUTHENTICATED, security.getManagerStatus());

        try {
            security.getCurrentManager();
            fail();
        } catch (RestException ex) {
        }

    }

    @Test
    public void testDoGetAuthenticationInfoAuthenticated() {

        String manEmail = "man@email.com";
        char[] manPassword = "man-pass123".toCharArray();
        String manUsername = "man-username";
        ManagerToken manToken = new ManagerToken(manEmail, manPassword);
        Manager foundManager = Mockito.mock(Manager.class);

        String appKey = "appKey";
        char[] appSecret = "app-secret".toCharArray();
        ApplicationToken appToken = new ApplicationToken(appKey, appSecret, manToken);

        ApplicationToken spyAppToken = Mockito.spy(appToken);
        Application app = Mockito.mock(Application.class);

        when(persistence.getApplication((String) spyAppToken.getPrincipal(), new String(appSecret))).thenReturn(app);

        when(persistence.getManagerByEmail(app.getUuid(), manEmail, new String(manPassword))).thenReturn(foundManager);

        AuthenticationInfo info = realm.doGetAuthenticationInfo(appToken);

        PrincipalCollection principals = info.getPrincipals();
        assertTrue(principals.getPrimaryPrincipal() instanceof KingdomSecurity);
        KingdomSecurity security = (KingdomSecurity) principals.getPrimaryPrincipal();
        assertEquals(app, security.getCurrentApplication());
        assertEquals(KingdomSecurity.ManagerStatus.AUTHENTICATED, security.getManagerStatus());

        try {
            assertEquals(foundManager, security.getCurrentManager());
        } catch (RestException ex) {
            fail();
        }

    }

    @Test
    public void testDoGetAuthenticationInfoByUsername() {

        char[] manPassword = "man-pass123".toCharArray();
        String manUsername = "man-username";
        ManagerToken manToken = new ManagerToken(manUsername, manPassword);
        Manager foundManager = Mockito.mock(Manager.class);

        String appKey = "appKey";
        char[] appSecret = "app-secret".toCharArray();
        ApplicationToken appToken = new ApplicationToken(appKey, appSecret, manToken);

        ApplicationToken spyAppToken = Mockito.spy(appToken);
        Application app = Mockito.mock(Application.class);

        when(persistence.getApplication((String) spyAppToken.getPrincipal(), new String(appSecret))).thenReturn(app);

        when(persistence.getManagerByUsername(app.getUuid(), manUsername, new String(manPassword))).thenReturn(foundManager);

        AuthenticationInfo info = realm.doGetAuthenticationInfo(appToken);

        PrincipalCollection principals = info.getPrincipals();
        assertTrue(principals.getPrimaryPrincipal() instanceof KingdomSecurity);
        KingdomSecurity security = (KingdomSecurity) principals.getPrimaryPrincipal();
        assertEquals(app, security.getCurrentApplication());
        assertEquals(KingdomSecurity.ManagerStatus.AUTHENTICATED, security.getManagerStatus());

        try {
            assertEquals(foundManager, security.getCurrentManager());
        } catch (RestException ex) {
            fail();
        }

    }

    @Test
    public void testDoGetAuthorizationInfoEmptyManager() {
        Application app = Mockito.mock(Application.class);
        Manager foundManager = Mockito.mock(Manager.class);
        KingdomSecurity security = new KingdomSecurity(app, foundManager, KingdomSecurity.ManagerStatus.EMPTY);

        PrincipalCollection principals = new SimplePrincipalCollection(security, realm.getName());

        AuthorizationInfo info = realm.doGetAuthorizationInfo(principals);
        assertNull(info.getObjectPermissions());
        assertNull(info.getRoles());
        assertNull(info.getStringPermissions());

    }

    @Test
    public void testDoGetAuthorizationInfo() {
        Application app = Mockito.mock(Application.class);
        Manager foundManager = Mockito.mock(Manager.class);
        KingdomSecurity security = new KingdomSecurity(app, foundManager, KingdomSecurity.ManagerStatus.AUTHENTICATED);
        List<ManagerMembership> memberships = Mockito.spy(new ArrayList<ManagerMembership>());
        ManagerMembership membership = new ManagerMembership();
        Domain domain = new Domain();
        domain.setUuid("domain-uuid");
        DomainPermission domainPerm = new DomainPermission();
        membership.setDomain(domain);
        membership.setPermission(domainPerm);
        memberships.add(membership);

        PrincipalCollection principals = new SimplePrincipalCollection(security, realm.getName());

        when(persistence.getManagerMemberships(security.getCurrentApplication().getUuid(), foundManager.getUuid())).thenReturn(memberships);

        AuthorizationInfo info = realm.doGetAuthorizationInfo(principals);
        assertEquals(memberships.size(), info.getObjectPermissions().size());
        assertTrue(info.getObjectPermissions().toArray()[0] instanceof AccessLevelPermission);
        AccessLevelPermission foundPermission = (AccessLevelPermission) info.getObjectPermissions().toArray()[0];
        assertTrue(foundPermission.getAccessLevels().containsKey(domain.getUuid()));
        foundPermission.getAccessLevels().get(domain.getUuid()).equals(domainPerm);

    }

}
