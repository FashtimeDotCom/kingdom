/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.env;

import com.josue.kingdom.credential.CredentialMailService;
import com.josue.kingdom.invitation.InvitationMailService;
import com.josue.kingdom.util.config.Config;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 * @author Josue
 */
public class EnvironmentProducer {

    @Inject
    @Any
    Instance<CredentialMailService> credServiceBeans;

    @Inject
    @Any
    Instance<InvitationMailService> invServiceBeans;

    @Inject
    @Config(key = "kingdom.env")
    String environment;

    @Produces
    public CredentialMailService credentialServiceProvider(InjectionPoint ip) {
        return getProperStageBean(credServiceBeans);
    }

    @Produces
    public InvitationMailService invitationServiceProvider(InjectionPoint ip) {
        return getProperStageBean(invServiceBeans);
    }

    /*
     * Returns the proper bean of type T by the specified Instance<T>
     */
    private <T> T getProperStageBean(Instance<T> instance) {

        if (environment == null) {
            environment = Stage.PRODUCTION.name();
        }
        Instance<T> services = instance.select(new StageQualifier(environment));

        if (!services.isUnsatisfied() && !services.isAmbiguous()) {
            return services.get();
        }
        throw new RuntimeException("Error while injecting bean");
    }

    public static class StageQualifier extends AnnotationLiteral<Environment> implements Environment {

        private final String value;

        public StageQualifier(String value) {
            this.value = value;
        }

        @Override
        public Stage stage() {
            return Stage.valueOf(value.toUpperCase());
        }
    }

}
