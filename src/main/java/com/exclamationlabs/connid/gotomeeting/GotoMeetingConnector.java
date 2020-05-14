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

import com.exclamationlabs.connid.gotomeeting.adapter.AccessManagementAdapter;
import com.exclamationlabs.connid.gotomeeting.adapter.GroupsAdapter;
import com.exclamationlabs.connid.gotomeeting.adapter.UsersAdapter;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@ConnectorClass(displayNameKey = "gotomeeting.connector.display", configurationClass = GotoMeetingConfiguration.class)
public class GotoMeetingConnector implements PoolableConnector, SchemaOp, DeleteOp, CreateOp, UpdateOp, SearchOp<String>, TestOp {

    private static final Log LOG = Log.getLog(GotoMeetingConnector.class);

    private boolean setupComplete;

    private GotoMeetingConfiguration configuration;
    private GotoMeetingAuthenticator authenticator;

    private GotoMeetingConnection connection;
    private Schema schema;

    @Override
    public GotoMeetingConfiguration getConfiguration() {
        return configuration;
    }

    public GotoMeetingConnection getConnection() {
        return connection;
    }

    @Override
    public void init(Configuration configuration) {

        if (connection == null) {

            LOG.info("Connection null, perform setup and authenticate...");
            this.configuration = (GotoMeetingConfiguration) configuration;
            configureResource();

            setupComplete = true;
        } else {
            LOG.info("Connection already established, reuse it.");
        }

    }
    @Override
    public Schema schema() {
        if (schema == null) {
            schema = GotoMeetingSchemaBuilder.build();
        }
        return schema;
    }

    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME) || objectClass.is(ObjectClass.GROUP_NAME)) {
            return new GotoMeetingFilterTranslator();
        } else {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        }
    }

    @Override
    public void checkAlive() {
        test();
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, final String query, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        Map<String, Object> options = operationOptions != null ? operationOptions.getOptions() : null;
        String[] atttributesToGet = operationOptions != null ? operationOptions.getAttributesToGet() : null;
        LOG.info("### EXECUTE_QUERY METHOD OBJECTCLASS, QUERY: {0}; {1}; {2}; {3}", objectClass, query, options, atttributesToGet);
        AccessManagementAdapter adapter = getAdapter(objectClass);
        adapter.get(query, resultsHandler);
    }

    @Override
    public Uid create(final ObjectClass objectClass, final Set<Attribute> attributes, final OperationOptions options) {
        LOG.info("### CREATE for {0}", objectClass);
        AccessManagementAdapter adapter = getAdapter(objectClass);
        return adapter.create(attributes);
    }

    @Override
    public Uid update(final ObjectClass objectClass, final Uid uid, final Set<Attribute> attributes, final OperationOptions options) {
        LOG.info("### UPDATE for {0}, {1}", objectClass, uid);
        AccessManagementAdapter adapter = getAdapter(objectClass);
        return adapter.update(uid, attributes);
    }

    @Override
    public void delete(final ObjectClass objectClass, final Uid uid, final OperationOptions options) {
        LOG.info("### DELETE for {0}, {1}", objectClass, uid);
        AccessManagementAdapter adapter = getAdapter(objectClass);
        adapter.delete(uid);
    }

    private void configureResource() {

        String propertiesPath = getConfiguration().getConfigurationFilePath();

        LOG.info("*** Properties path: " + propertiesPath);
        Properties configurationProperties = new Properties();
        try {
            configurationProperties.load(new FileReader(propertiesPath));
            GotoMeetingConfiguration.validateProperties(configurationProperties);
        } catch (IOException ex) {
            LOG.error(ex, "Error reading Config File at {0}", propertiesPath);
            throw new ConfigurationException("Failed to read Config File", ex);
        }
        LOG.info("*** Properties validated");

        if (authenticator == null) {
            authenticator = new GotoMeetingAuthenticator();
        }

        connection = new GotoMeetingConnection(getConfiguration(), authenticator);
    }

    @Override
    public void dispose() {
        configuration = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }
        setupComplete = false;
    }

    @Override
    public void test() {
        if (!setupComplete) {
            throw new ConfigurationException("Connection setup incomplete or invalid");
        }
        connection.test();
    }

    private AccessManagementAdapter getAdapter(ObjectClass objectClass) {
        AccessManagementAdapter adapter;
        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            adapter = new UsersAdapter(connection);
        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            adapter = new GroupsAdapter(connection);
        } else {
            throw new ConnectorException("Unsupported object class: " + objectClass);
        }

        return adapter;
    }


}