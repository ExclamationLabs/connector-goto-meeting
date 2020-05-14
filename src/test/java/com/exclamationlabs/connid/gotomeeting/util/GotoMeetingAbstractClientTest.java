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

package com.exclamationlabs.connid.gotomeeting.util;

import com.exclamationlabs.connid.gotomeeting.GotoMeetingAuthenticator;
import com.exclamationlabs.connid.gotomeeting.GotoMeetingConfiguration;
import com.exclamationlabs.connid.gotomeeting.GotoMeetingConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;

abstract public class GotoMeetingAbstractClientTest {

    protected GotoMeetingConnection connection;

    @Mock
    protected HttpClient stubClient;

    @Mock
    protected HttpResponse stubResponse;

    @Mock
    protected HttpEntity stubResponseEntity;

    @Mock
    protected StatusLine stubStatusLine;

    @Before
    public void setup() throws IOException {
        GotoMeetingConfiguration configuration = new TestGotoMeetingConfiguration();
        Properties testProperties = new Properties();
        testProperties.load(new FileReader(configuration.getConfigurationFilePath()));
        GotoMeetingConfiguration.validateProperties(testProperties);


        connection = new GotoMeetingConnection(configuration, new GotoMeetingAuthenticator() {
            public String authenticate(GotoMeetingConfiguration configuration) {
                return "dummy";
            }
        });
        connection.setConfigurationProperties(testProperties);
        connection.setStubClient(stubClient);
    }

    protected void prepareMockResponse(String responseData) throws IOException {
        Mockito.when(stubResponseEntity.getContent()).thenReturn(new ByteArrayInputStream(responseData.getBytes()));
        Mockito.when(stubResponse.getEntity()).thenReturn(stubResponseEntity);
        Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
        Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
    }

    protected void prepareMockResponseEmpty() throws IOException {
        Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
        Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
    }


}
