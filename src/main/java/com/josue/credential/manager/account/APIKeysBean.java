/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.account;

import com.josue.credential.manager.auth.credential.APIDomainCredential;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Josue
 */
@Named(value = "apiKeyBean")
@RequestScoped
public class APIKeysBean {

    @Inject
    AccountControl control;

    private List<APIDomainCredential> apiCredentials;

    @PostConstruct
    public void init() {
        this.apiCredentials = control.getAPICredentials();
    }

    public List<APIDomainCredential> getApiCredentials() {
        return apiCredentials;
    }

    public void setApiCredentials(List<APIDomainCredential> apiCredentials) {
        this.apiCredentials = apiCredentials;
    }

}
