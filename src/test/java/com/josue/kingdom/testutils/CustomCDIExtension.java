/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author Josue
 */
public class CustomCDIExtension implements Extension {

    private Bean<DatabaseHelper> instance;

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        AnnotatedType<DatabaseHelper> at = bm.createAnnotatedType(DatabaseHelper.class);
        final InjectionTarget<DatabaseHelper> it = bm.createInjectionTarget(at);
        instance = new Bean<DatabaseHelper>() {

            @Override
            public Class<?> getBeanClass() {
                return DatabaseHelper.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public boolean isNullable() {
                return false;
            }

            @Override
            public DatabaseHelper create(CreationalContext<DatabaseHelper> creationalContext) {
                DatabaseHelper instance = it.produce(creationalContext);
                it.inject(instance, creationalContext);
                it.postConstruct(instance);
                return instance;
            }

            @Override
            public void destroy(DatabaseHelper instance, CreationalContext<DatabaseHelper> creationalContext) {
                it.preDestroy(instance);
                it.dispose(instance);
                creationalContext.release();
            }

            @Override
            public Set<Type> getTypes() {
                Set<Type> types = new HashSet<>();
                types.add(DatabaseHelper.class);
                types.add(Object.class);
                return types;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                Set<Annotation> qualifiers = new HashSet<>();
                qualifiers.add(new AnnotationLiteral<Default>() {
                });
                qualifiers.add(new AnnotationLiteral<Any>() {
                });
                return qualifiers;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return ApplicationScoped.class;
            }

            @Override
            public String getName() {
                return "databaseHelper";
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public boolean isAlternative() {
                return false;
            }
        };
        abd.addBean(instance);
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        try {
            manager.getReference(instance, instance.getBeanClass(), manager.createCreationalContext(instance)).toString();
        } catch (Exception ex) {
            event.addDeploymentProblem(ex);
        }
    }
}
