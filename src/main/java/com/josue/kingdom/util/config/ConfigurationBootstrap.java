/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util.config;

import com.josue.kingdom.util.startup.RunAtStartup;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Josue
 */
@ApplicationScoped
@RunAtStartup
public class ConfigurationBootstrap {

    private Properties properties;
    private static final Logger log = Logger.getLogger(ConfigurationProducer.class.getName());

    @PostConstruct
    public void loadProperties() {
        try {
            log.info("************** Initializing properties **************");
            InputStream inputStream = ConfigurationProducer.class.getClassLoader().getResourceAsStream("app.properties");
            this.properties = new Properties();
            this.properties.load(inputStream); //shouldnt be null
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                log.log(Level.INFO, "{0} => {1}", new Object[]{entry.getKey(), entry.getValue()});
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Config
    @Produces
    public Properties produceProperties() {
        return properties;
    }

}
