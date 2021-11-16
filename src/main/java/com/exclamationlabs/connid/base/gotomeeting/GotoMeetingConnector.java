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

package com.exclamationlabs.connid.base.gotomeeting;

import com.exclamationlabs.connid.base.connector.BaseFullAccessConnector;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.gotomeeting.adapter.GotoMeetingGroupsAdapter;
import com.exclamationlabs.connid.base.gotomeeting.adapter.GotoMeetingUsersAdapter;
import com.exclamationlabs.connid.base.gotomeeting.configuration.GotoMeetingConfiguration;
import com.exclamationlabs.connid.base.gotomeeting.driver.rest.GotoMeetingDriver;
import org.identityconnectors.framework.spi.ConnectorClass;

@ConnectorClass(displayNameKey = "gotomeeting.connector.display",
        configurationClass = GotoMeetingConfiguration.class)
public class GotoMeetingConnector extends BaseFullAccessConnector<GotoMeetingConfiguration> {

    public GotoMeetingConnector() {
        super(GotoMeetingConfiguration.class);
        setAuthenticator((Authenticator) new OAuth2TokenPasswordAuthenticator());
        setDriver(new GotoMeetingDriver());
        setAdapters(new GotoMeetingUsersAdapter(), new GotoMeetingGroupsAdapter());

    }

}
