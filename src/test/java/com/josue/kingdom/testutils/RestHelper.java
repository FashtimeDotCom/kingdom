/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.testutils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;

/**
 *
 * @author Josue
 */
public class RestHelper {

    private static final String BASE_URI = "http://localhost:9080/kingdom-test/api";
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
        mapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(mapper);

        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getSingletons().add(jacksonJsonProvider);

        client = Client.create(clientConfig);
        webResource = client.resource(BASE_URI);
        return webResource;
    }

    private static WebResource setPath(WebResource wr, String... paths) {
        for (String path : paths) {
            wr = wr.path("/" + path);
        }
        return wr;
    }

    private static void setQueryParams(WebResource wr, Map<String, Object> queryParams) {
        for (Map.Entry<String, Object> entrySet : queryParams.entrySet()) {
            String key = entrySet.getKey();
            Object value = entrySet.getValue();
            wr.setProperty(key, value);
        }
    }

    public static ClientResponse doGetRequest(Map<String, Object> queryParams, String... paths) {
        WebResource wr = getWebResource();
        wr = setPath(wr, paths);
        setQueryParams(wr, queryParams);
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).get(ClientResponse.class);
    }

    public static ClientResponse doGetRequest(String... paths) {
        WebResource wr = getWebResource();
        wr = setPath(wr, paths);
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).get(ClientResponse.class);
    }

    public static ClientResponse doPostRequest(Object resource, String... paths) {
        WebResource wr = getWebResource();
        wr = setPath(wr, paths);
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).post(ClientResponse.class, resource);
    }

    public static ClientResponse doPutRequest(Object resource, String... paths) {
        WebResource wr = getWebResource();
        wr = setPath(wr, paths);
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).put(ClientResponse.class, resource);
    }

    public static ClientResponse doDeleteRequest(String... paths) {
        WebResource wr = getWebResource();
        wr = setPath(wr, paths);
        return wr.header(API_KEY, DEFAULT_APIKEY).type(MEDIA_TYPE).accept(MEDIA_TYPE).delete(ClientResponse.class);
    }

    //This method is useful for detail message response
    // when we get a close stream when reading the body twice
    public static void assertStatusCode(int expected, ClientResponse response) {
        if (response.getStatus() != expected) {
            String errorMessage = response.getEntity(new GenericType<String>() {
            });
            Assert.fail(String.format("java.lang.AssertionError: expected:<%d> but was:<%d> - body: %s", new Object[]{expected, response.getStatus(), errorMessage}));
        }
    }
}
