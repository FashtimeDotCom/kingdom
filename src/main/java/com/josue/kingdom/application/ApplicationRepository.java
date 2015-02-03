/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.application;

import com.josue.kingdom.JpaRepository;
import com.josue.kingdom.application.entity.Application;
import com.josue.kingdom.application.entity.ApplicationConfig;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class ApplicationRepository extends JpaRepository {

    public Application getApplication(String appUuid) {
        TypedQuery<Application> query = em.createQuery("SELECT app FROM Application app WHERE app.uuid = :appUuid", Application.class);
        query.setParameter("appUuid", appUuid);
        List<Application> memberships = query.getResultList();
        return extractSingleResultFromList(memberships);
    }

    public ApplicationConfig getApplicationConfig(String appUuid) {
        TypedQuery<ApplicationConfig> query = em.createQuery("SELECT conf FROM ApplicationConfig conf WHERE conf.application.uuid = :appUuid", ApplicationConfig.class);
        query.setParameter("appUuid", appUuid);
        List<ApplicationConfig> configs = query.getResultList();
        return extractSingleResultFromList(configs);
    }
}
