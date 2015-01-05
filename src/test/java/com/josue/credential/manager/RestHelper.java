/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Josue
 */
public class RestHelper {

    private static final String BASE_URI = "http://localhost:8080/credential-manager-test/api";
    public static final String API_KEY = "ApiKey";
    public static final String MEDIA_TYPE = "application/json;charset=utf-8";

    private static final String DEFAULT_APIKEY = "tmfkrkileqo65hjl9udm550hip";

    private static Client client;
    private static WebResource webResource;
    private static ObjectMapper mapper;

    private static final Logger logger = Logger.getLogger(RestHelper.class.getName());

    /*
     * Instantiate a new Jersey Client using a custom Jackson mapper, for custom date deserialization
     */
    public static WebResource getWebResource() {
        if (webResource != null) {
            return webResource;
        }
        logger.log(Level.INFO, "*** INITIALIZING WEBRESOURCE FOR JERSEYCLIENT ***");

        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(mapper);

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(jacksonJsonProvider);

        client = Client.create(clientConfig);
        webResource = client.resource(BASE_URI);
        return webResource;
    }

    public static ClientResponse doGetRequest(String... paths) {
        WebResource wr = getWebResource();
        for (String path : paths) {
            wr = wr.path(path);
        }
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).get(ClientResponse.class);
    }

    public static ClientResponse doPostRequest(Object resource, String... paths) {
        WebResource wr = getWebResource();
        for (String path : paths) {
            wr = wr.path(path);
        }
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).post(ClientResponse.class, resource);
    }

    public static ClientResponse doPutRequest(Object resource, String... paths) {
        WebResource wr = getWebResource();
        for (String path : paths) {
            wr = wr.path(path);
        }
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).put(ClientResponse.class, resource);
    }

}
