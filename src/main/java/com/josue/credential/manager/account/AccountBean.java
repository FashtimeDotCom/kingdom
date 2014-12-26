/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.Domain;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

/**
 *
 * @author Josue
 */
@Named
@RequestScoped
public class AccountBean {

    private List<Domain> domains;

    public AccountBean() {
        domains = new ArrayList<>();

        Domain domain = new Domain();
        domain.setName("domain-name");
        domain.setDescription("descriotpn-kshkjsd");
        domains.add(domain);
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public void updateDomain(ValueChangeEvent event) {
        LOG.log(Level.INFO, "SWITCHING TO DOMAIN: {0}", event.getNewValue());
    }
    private static final Logger LOG = Logger.getLogger(AccountBean.class.getName());

}
