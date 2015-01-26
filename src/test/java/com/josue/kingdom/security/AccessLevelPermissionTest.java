/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

import com.josue.kingdom.domain.entity.Domain;
import com.josue.kingdom.domain.entity.DomainPermission;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Josue
 */
public class AccessLevelPermissionTest {

    @Test
    public void testImplies() {

        Domain domain = new Domain();
        domain.setUuid("domain1-uuid");

        DomainPermission domainPermLevel1 = new DomainPermission(1);
        domainPermLevel1.setDomain(domain);
        DomainPermission domainPermLevel2 = new DomainPermission(2);
        domainPermLevel2.setDomain(domain);
        DomainPermission domainPermLevel3 = new DomainPermission(3);
        domainPermLevel3.setDomain(domain);

        AccessLevelPermission accessLevel = new AccessLevelPermission(domain, domainPermLevel1);//actual
        boolean implies1 = accessLevel.implies(new AccessLevelPermission(domain, domainPermLevel2));//needed
        assertFalse(implies1);

        accessLevel = new AccessLevelPermission(domain, domainPermLevel2);//actual
        boolean implies2 = accessLevel.implies(new AccessLevelPermission(domain, domainPermLevel2));//needed
        assertTrue(implies2);

        accessLevel = new AccessLevelPermission(domain, domainPermLevel3);//actual
        boolean implies3 = accessLevel.implies(new AccessLevelPermission(domain, domainPermLevel2));//needed
        assertTrue(implies3);

    }

}
