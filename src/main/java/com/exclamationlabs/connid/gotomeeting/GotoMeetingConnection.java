/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.gotomeeting;

import com.exclamationlabs.connid.gotomeeting.model.User;
import com.exclamationlabs.connid.gotomeeting.util.GotoMeetingFaultProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GotoMeetingConnection {

    private static final Log LOG = Log.getLog(GotoMeetingConnection.class);

    private static GotoMeetingFaultProcessor faultProcessor;
    private static GsonBuilder gsonBuilder;

    private Map<String, String> additionalHeadersMap;
    private GotoMeetingAuthenticator authenticator;
    private HttpClient stubClient; // for unit testing
    private String accessToken;

    static {
        gsonBuilder = new GsonBuilder();
    }

    private GotoMeetingConfiguration configuration;

    public GotoMeetingConnection(GotoMeetingConfiguration inputConfiguration,
                                 GotoMeetingAuthenticator authenticatorIn) {
        authenticator = authenticatorIn;
        additionalHeadersMap = new HashMap<>();
        configuration = inputConfiguration;
        faultProcessor = new GotoMeetingFaultProcessor(gsonBuilder);
        setupAuthentication();
    }

    private void setupAuthentication() {
        accessToken = authenticator.authenticate(configuration);
    }

    void dispose() {
        additionalHeadersMap = new HashMap<>();
        faultProcessor = null;
        configuration = null;
    }

    void test() {
        try {
            HttpGet request = new HttpGet(configuration.getAdminApiPath() + "/me");
            prepareHeaders(request);
            User response = executeRequest(request, User.class);
            if (response == null) {
                throw new ConnectorException("Self-identification for GotoMeeting connection user returned invalid response.");
            }
        } catch (Exception e) {
            throw new ConnectorException("Self-identification for GotoMeeting connection user failed.", e);
        }
    }

    public HttpGet createGetRequest(String restUri) {
        HttpGet request = new HttpGet(getGotoMeetingUrl() + restUri);
        prepareHeaders(request);
        return request;
    }

    public HttpDelete createDeleteRequest(String restUri) {
        HttpDelete request = new HttpDelete(getGotoMeetingUrl() + restUri);
        prepareHeaders(request);
        return request;
    }

    public HttpPost createPostRequest(String restUri, Object requestBody) {
        HttpPost request = new HttpPost(getGotoMeetingUrl() + restUri);
        prepareHeaders(request);
        setupJsonRequestBody(request, requestBody);
        return request;
    }

    public HttpPut createPutRequest(String restUri, Object requestBody) {
        HttpPut request = new HttpPut(getGotoMeetingUrl() + restUri);
        prepareHeaders(request);
        if (requestBody != null) {
            setupJsonRequestBody(request, requestBody);
        }
        return request;
    }

    private String getGotoMeetingUrl() {
        return configuration.getAdminApiPath() + "/accounts/" +
                configuration.getAccountKey();
    }

    private void prepareHeaders(HttpRequestBase request) {
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        for (Map.Entry<String, String> entry : additionalHeadersMap.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private HttpClient createClient() {
        return stubClient != null ? stubClient : HttpClients.createDefault();
    }

    public <T>T executeRequest(HttpRequestBase request, Class<T> returnType) {
        if (gsonBuilder == null || faultProcessor == null || configuration == null) {
            throw new ConnectionBrokenException("Connection invalidated or disposed, request cannot " +
                    "be performed.  Gsonbuilder: " + gsonBuilder + "; faultProcessor: " +
                    faultProcessor + "; configuration: " + configuration);
        }

        HttpClient client = createClient();
        HttpResponse response;
        T result = null;

        try {

            LOG.info("Request details: {0} to {1}", request.getMethod(),
                    request.getURI());
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("request status code is {0}", statusCode);

            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                LOG.info("request execution failed; status code is {0}", statusCode);
                faultProcessor.process(response);
            }

            if (returnType != null && statusCode != HttpStatus.SC_NOT_FOUND) {
                String rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
                LOG.info("Received {0} response for {1} {2}, raw JSON: {3}", statusCode,
                        request.getMethod(), request.getURI(), rawJson);

                Gson gson = gsonBuilder.create();
                result = gson.fromJson(rawJson, returnType);
            }

        } catch (ClientProtocolException e) {
            throw new ConnectorException(
                    "Unexpected ClientProtocolException occurred while attempting GotoMeeting call: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ConnectorException(
                    "Unexpected IOException occurred while attempting GotoMeeting call: " + e.getMessage(), e);

        }

        return result;
    }

    private void setupJsonRequestBody(HttpEntityEnclosingRequestBase request, Object requestBody) {

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(requestBody);
        LOG.info("JSON formatted request for {0}: {1}", requestBody.getClass().getName(), json);
        try {
            StringEntity stringEntity = new StringEntity(json);
            request.setEntity(stringEntity);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Request body encoding failed for data: " + json, e);
        }
    }

    public void addAdditionalHeader(String key, String value) {
        additionalHeadersMap.put(key, value);
    }



    public void setStubClient(HttpClient stubClient) {
        this.stubClient = stubClient;
    }

    public void setConfigurationProperties(Properties testProperties) {
        configuration.setConfigurationProperties(testProperties);
    }

    public GotoMeetingConfiguration getConfiguration() {
        return configuration;
    }
}
