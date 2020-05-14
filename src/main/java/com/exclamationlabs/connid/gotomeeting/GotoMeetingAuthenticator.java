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

import com.exclamationlabs.connid.gotomeeting.model.response.GotoMeetingAccessTokenResponse;
import com.google.gson.Gson;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class GotoMeetingAuthenticator {

    private static final Log LOG = Log.getLog(GotoMeetingAuthenticator.class);

    private static final String TRUST_STORE_TYPE_PROPERTY = "javax.net.ssl.trustStoreType";
    private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

    public String authenticate(final GotoMeetingConfiguration configuration) {
        try {
            clearTrustStoreProperties();

            HttpPost request = new HttpPost(configuration.getTokenApiPath());

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("username", configuration.getUserId()));
            params.add(new BasicNameValuePair("password", configuration.getUserPassword()));
            request.setEntity(new UrlEncodedFormEntity(params));

            String encodedSecret = DatatypeConverter.printBase64Binary((configuration.getConsumerKey() + ":" +
                    configuration.getConsumerSecret()).getBytes(Charsets.UTF_8.name()));

            LOG.info("Encoded secret: " + encodedSecret);

            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecret);

            HttpClient client = setupClient();
            LOG.info("Authenticate request uri: " + request.getURI().toString());
            HttpResponse response = client.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            String rawJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            LOG.info("Authenticator JSON raw response: {0}", rawJson);
            if (statusCode == HttpStatus.SC_OK) {
                Gson gson = new Gson();
                GotoMeetingAccessTokenResponse tokenResponse = gson.fromJson(rawJson, GotoMeetingAccessTokenResponse.class);
                LOG.info("Access token retrieved from GotoMeeting: {0}", tokenResponse.getAccessToken());
                LOG.info("Access token details: scope - {0}, type - {1}, expires - {2}", tokenResponse.getScope(),
                        tokenResponse.getTokenType(), tokenResponse.getExpiresIn());
                return tokenResponse.getAccessToken();
            }
            else {
                throw new ConnectorSecurityException("GotoMeeting authentication failed with status: " + statusCode);
            }
        } catch (IOException e) {
            throw new ConnectorIOException("GotoMeeting authentication failed due to IO or Authentication Error", e);
        }
    }

    private static void clearTrustStoreProperties() {

        LOG.info("Authentication to FIS starting, obtaining new access token ...");
        LOG.info("Clearing out property {0} for FIS auth support.  Value was {1}",
                TRUST_STORE_TYPE_PROPERTY, System.getProperty(TRUST_STORE_TYPE_PROPERTY));
        LOG.info("Clearing out property {0} for FIS auth support.  Value was {1}",
                TRUST_STORE_PROPERTY, System.getProperty(TRUST_STORE_PROPERTY));

        System.clearProperty(TRUST_STORE_TYPE_PROPERTY);
        System.clearProperty(TRUST_STORE_PROPERTY);
    }

    private HttpClient setupClient() {

        BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager();
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

}
