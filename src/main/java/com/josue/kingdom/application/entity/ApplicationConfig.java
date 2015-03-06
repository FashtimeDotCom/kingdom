/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.application.entity;

import com.josue.kingdom.rest.TenantResource;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Josue
 */
@Entity
@Table(name = "application_config", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"application_uuid"})
})
public class ApplicationConfig extends TenantResource {

    private static final String RESOURCE_PATH = "/configurations";

    @NotNull
    @Column(name = "application_url")
    private String applicationUrl;

    @NotNull
    @Column(name = "password_callback_url")
    private String passwordCallbackUrl;

    @NotNull
    @Column(name = "account_callback_url")
    private String accountCallbackUrl;

    @Column(name = "password_email_template")
    private String passwordEmailTemplate;

    @Column(name = "invitation_email_template")
    private String invitationEmailTemplate;

    @Column(name = "loginrec_email_template")
    private String loginRecoveryEmailTemplate;

    @Override
    public String getHref() {
        return RESOURCE_PATH + "/" + getUuid();
    }

    public String getPasswordCallbackUrl() {
        return passwordCallbackUrl;
    }

    public void setPasswordCallbackUrl(String passwordCallbackUrl) {
        this.passwordCallbackUrl = passwordCallbackUrl;
    }

    public String getAccountCallbackUrl() {
        return accountCallbackUrl;
    }

    public void setAccountCallbackUrl(String accountCallbackUrl) {
        this.accountCallbackUrl = accountCallbackUrl;
    }

    public String getPasswordEmailTemplate() {
        return passwordEmailTemplate;
    }

    public void setPasswordEmailTemplate(String passwordEmailTemplate) {
        this.passwordEmailTemplate = passwordEmailTemplate;
    }

    public String getInvitationEmailTemplate() {
        return invitationEmailTemplate;
    }

    public void setInvitationEmailTemplate(String invitationEmailTemplate) {
        this.invitationEmailTemplate = invitationEmailTemplate;
    }

    public String getLoginRecoveryEmailTemplate() {
        return loginRecoveryEmailTemplate;
    }

    public void setLoginRecoveryEmailTemplate(String loginRecoveryEmailTemplate) {
        this.loginRecoveryEmailTemplate = loginRecoveryEmailTemplate;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.applicationUrl);
        hash = 11 * hash + Objects.hashCode(this.passwordCallbackUrl);
        hash = 11 * hash + Objects.hashCode(this.accountCallbackUrl);
        hash = 11 * hash + Objects.hashCode(this.passwordEmailTemplate);
        hash = 11 * hash + Objects.hashCode(this.invitationEmailTemplate);
        hash = 11 * hash + Objects.hashCode(this.loginRecoveryEmailTemplate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationConfig other = (ApplicationConfig) obj;
        if (!Objects.equals(this.applicationUrl, other.applicationUrl)) {
            return false;
        }
        if (!Objects.equals(this.passwordCallbackUrl, other.passwordCallbackUrl)) {
            return false;
        }
        if (!Objects.equals(this.accountCallbackUrl, other.accountCallbackUrl)) {
            return false;
        }
        if (!Objects.equals(this.passwordEmailTemplate, other.passwordEmailTemplate)) {
            return false;
        }
        if (!Objects.equals(this.invitationEmailTemplate, other.invitationEmailTemplate)) {
            return false;
        }
        return Objects.equals(this.loginRecoveryEmailTemplate, other.loginRecoveryEmailTemplate);
    }

}
