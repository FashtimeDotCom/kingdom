/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.config;

import java.util.Properties;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ConfigurationProducer {

    @Config
    @Inject
    Properties properties;

    @Produces
    @Config
    public String getStringValue(InjectionPoint ip) {
        String key = ip.getAnnotated().getAnnotation(Config.class).key();
        String prop = properties.getProperty(key);
        return prop;
    }

    @Produces
    @Config
    public int getIntValue(InjectionPoint ip) {
        String key = ip.getAnnotated().getAnnotation(Config.class).key();
        String prop = properties.getProperty(key);
        return Integer.parseInt(prop);
    }

    @Produces
    @Config
    public double getDoubleValue(InjectionPoint ip) {
        String key = ip.getAnnotated().getAnnotation(Config.class).key();
        String prop = properties.getProperty(key);
        return Double.parseDouble(prop);
    }
}
