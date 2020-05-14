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

import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

import java.util.Properties;

public class GotoMeetingConfiguration extends AbstractConfiguration {

    private final static String TOKEN_API_PATH = "exclamationlabs.connector.gotomeeting.token.api.path";
    private final static String CONSUMER_KEY = "exclamationlabs.connector.gotomeeting.consumer.key";
    private final static String CONSUMER_SECRET = "exclamationlabs.connector.gotomeeting.consumer.secret";
    private final static String USER_ID = "exclamationlabs.connector.gotomeeting.user.id";
    private final static String USER_PASSWORD = "exclamationlabs.connector.gotomeeting.user.password";

    private final static String ADMIN_API_PATH = "exclamationlabs.connector.gotomeeting.admin.api.path";
    private final static String ACCOUNT_KEY = "exclamationlabs.connector.gotomeeting.account.key";


    private static Properties configurationProperties;

    private String configurationFilePath;


    @Override
    public void validate() {
        if (StringUtils.isBlank(configurationFilePath)) {
            throw new ConfigurationException("Configuration path not given for Connector.");
        }
    }

    @ConfigurationProperty(
            displayMessageKey = "GotoMeeting Configuration File Path",
            helpMessageKey = "File path for the GotoMeeting Configuration File",
            required = true)
    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    @SuppressWarnings("unused")
    public void setConfigurationFilePath(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;
    }

    @Override
    public String toString() {
        return String.format(
                "GotoMeetingConfiguration{configurationFilePath='%s'}",
                configurationFilePath);
    }

    public static void validateProperties(Properties input) throws ConfigurationException {
        if (input==null) {
            throw new ConfigurationException("Configuration could not read. Properties is null.");
        }
        configurationProperties = input;

        String[] propertyArray = new String[] {
                TOKEN_API_PATH, CONSUMER_KEY, CONSUMER_SECRET, USER_ID, USER_PASSWORD,
                ADMIN_API_PATH, ACCOUNT_KEY
        };

        for (String currentProperty: propertyArray) {
            if (configurationProperties.getProperty(currentProperty) == null) {
                throw new ConfigurationException("Missing configuration property: " + currentProperty);
            }
        }

    }

    String getAdminApiPath() { return configurationProperties.getProperty(ADMIN_API_PATH); }

    String getAccountKey() { return configurationProperties.getProperty(ACCOUNT_KEY); }

    String getUserId() { return configurationProperties.getProperty(USER_ID); }

    String getUserPassword() { return configurationProperties.getProperty(USER_PASSWORD); }

    String getConsumerKey() { return configurationProperties.getProperty(CONSUMER_KEY); }

    String getConsumerSecret() { return configurationProperties.getProperty(CONSUMER_SECRET); }

    String getTokenApiPath() { return configurationProperties.getProperty(TOKEN_API_PATH); }

    void setConfigurationProperties(Properties testProperties) {
        configurationProperties = testProperties;
    }
}
