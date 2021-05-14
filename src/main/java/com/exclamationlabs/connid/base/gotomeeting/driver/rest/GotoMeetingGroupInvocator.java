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

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingGroup;
import com.exclamationlabs.connid.base.gotomeeting.model.response.ListGroupsResponse;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;
import java.util.Map;

public class GotoMeetingGroupInvocator implements DriverInvocator<GotoMeetingDriver, GotoMeetingGroup> {

    @Override
    public String create(GotoMeetingDriver driver, GotoMeetingGroup groupModel) throws ConnectorException {
        return driver.executePostRequest("/groups", String.class, groupModel).getResponseObject();
    }

    @Override
    public void update(GotoMeetingDriver driver, String groupId, GotoMeetingGroup groupModel) throws ConnectorException {
        GotoMeetingGroup modifyGroup = new GotoMeetingGroup();
        // Cannot send key in update JSON, and name is the only field to update,
        // so create a new object w/ just the name set
        modifyGroup.setName(groupModel.getName());

        driver.executePutRequest("/groups/" + groupModel.getKey(), null, modifyGroup);
    }

    @Override
    public void delete(GotoMeetingDriver driver, String groupId) throws ConnectorException {
        driver.executeDeleteRequest("/groups/" + groupId, null);
    }

    @Override
    public List<GotoMeetingGroup> getAll(GotoMeetingDriver driver, Map<String, Object> map) throws ConnectorException {
        ListGroupsResponse response = driver.executeGetRequest("/groups", ListGroupsResponse.class).getResponseObject();
        return response.getResults();
    }

    @Override
    public GotoMeetingGroup getOne(GotoMeetingDriver driver, String groupId, Map<String, Object> map) throws ConnectorException {
        return driver.executeGetRequest("/groups/" + groupId, GotoMeetingGroup.class).getResponseObject();
    }
}
