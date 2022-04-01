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

package com.exclamationlabs.connid.base.gotomeeting.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.gotomeeting.configuration.GotoMeetingConfiguration;
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingGroup;
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingUser;
import com.exclamationlabs.connid.base.gotomeeting.util.GotoMeetingFaultProcessor;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class GotoMeetingDriver extends BaseRestDriver<GotoMeetingConfiguration> {

    public GotoMeetingDriver() {
        super();
        addInvocator(GotoMeetingUser.class, new GotoMeetingUserInvocator());
        addInvocator(GotoMeetingGroup.class, new GotoMeetingGroupInvocator());
    }

    @Override
    protected RestFaultProcessor getFaultProcessor() {
        return GotoMeetingFaultProcessor.getInstance();
    }

    @Override
    protected String getBaseServiceUrl() {
        return configuration.getServiceUrl();
    }

    String getAccountUrl() {
        return configuration.getAccountKey();
    }

    @Override
    protected boolean usesBearerAuthorization() {
        return true;
    }

    static String getPaginationString(ResultsPaginator paginator) {
        String response = "";
        if (paginator.hasPagination()) {
            // Note: subtract 1 from offset; GotoMeeting offset is zero-based; Midpoint is one-based
            response += "pageSize=" + paginator.getPageSize() +
                    "&offset=" + (paginator.getCurrentOffset() - 1) + "&";
        }
        return response;
    }

    @Override
    public void test() throws ConnectorException {
        try {
            RestResponseData<GotoMeetingUser> meUser = executeGetRequest("/me", GotoMeetingUser.class);
            if (meUser.getResponseObject() == null) {
                throw new ConnectorException("Self-identification for GotoMeeting connection user returned invalid response.");
            }
        } catch (Exception e) {
            throw new ConnectorException("Self-identification for GotoMeeting connection user failed.", e);
        }
    }

    @Override
    public void close() {
        configuration = null;
        authenticator = null;
    }


}