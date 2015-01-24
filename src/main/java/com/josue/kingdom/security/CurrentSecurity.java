/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.security;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class CurrentSecurity {

    @Produces
    @Current
    @RequestScoped
    public KingdomSecurity currentSecurity() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            KingdomSecurity kingdomSecurity = (KingdomSecurity) subject;
            return kingdomSecurity;

        }
        throw new RuntimeException("Could not load credentials");

    }

}
