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
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingUser;
import com.exclamationlabs.connid.base.gotomeeting.model.request.UserCreationRequest;
import com.exclamationlabs.connid.base.gotomeeting.model.response.ListUsersResponse;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class GotoMeetingUserInvocator implements DriverInvocator<GotoMeetingDriver, GotoMeetingUser> {

    @Override
    public String create(GotoMeetingDriver driver, GotoMeetingUser userModel) throws ConnectorException {
        UserCreationRequest requestData = new UserCreationRequest();
        requestData.setUsers(new ArrayList<>());
        requestData.getUsers().add(userModel);
        GotoMeetingUser[] newUsers = driver.executePostRequest(driver.getAccountUrl() + "/users?allOrNothing=false",
                GotoMeetingUser[].class, requestData).getResponseObject();

        if (newUsers == null || newUsers.length == 0) {
            throw new ConnectorException("Response from user creation was invalid");
        }
        return newUsers[0].getKey();
    }

    @Override
    public void update(GotoMeetingDriver driver, String userId, GotoMeetingUser userModel) throws ConnectorException {
        String userKey = userModel.getKey();
        userModel.setKey(null); // cannot pass key value in update JSON, can only be present in URL

        driver.executePutRequest(driver.getAccountUrl() + "/users/" + userKey + "?allOrNothing=true", null, userModel);

        String newGroupId = userModel.getGroupKey();

        GotoMeetingUser currentUser = getOne(driver, userId, new HashMap<>());
        String oldGroupId = currentUser.getGroupKey();

        if (StringUtils.isNotBlank(oldGroupId) &&
                (!StringUtils.equalsIgnoreCase(oldGroupId, newGroupId))) {
            removeGroupFromUser(driver, userKey);
        }

        if (StringUtils.isNotBlank(newGroupId) &&
                (!StringUtils.equalsIgnoreCase(oldGroupId, newGroupId))) {
            addGroupToUser(driver, oldGroupId, userKey);
        }
    }

    @Override
    public void delete(GotoMeetingDriver driver, String userId) throws ConnectorException {
        driver.executeDeleteRequest(driver.getAccountUrl() + "/users/" + userId, null);
    }

    @Override
    public Set<GotoMeetingUser> getAll(GotoMeetingDriver driver, ResultsFilter filter,
                                        ResultsPaginator paginator, Integer max) throws ConnectorException {
        ListUsersResponse response = driver.executeGetRequest(driver.getAccountUrl() + "/users", ListUsersResponse.class).getResponseObject();
        return response.getResults();
    }

    @Override
    public GotoMeetingUser getOne(GotoMeetingDriver driver, String userId, Map<String, Object> map) throws ConnectorException {
        return driver.executeGetRequest(driver.getAccountUrl() + "/users/" + userId, GotoMeetingUser.class).getResponseObject();
    }

    private void addGroupToUser(GotoMeetingDriver driver, String groupId, String userId) throws ConnectorException {
        driver.executePutRequest(driver.getAccountUrl() + "/groups/" + groupId + "/users/" + userId,
                null, null);
    }

    private void removeGroupFromUser(GotoMeetingDriver driver, String userId) throws ConnectorException {
        // Note: GotoMeeting only seems to support removing a user from ALL groups it
        // currently belongs to.  So that is the logic that is invoked here.
        driver.executeDeleteRequest(driver.getAccountUrl() + "/users/" + userId + "/groups", null);
    }
}
