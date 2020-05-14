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

package com.exclamationlabs.connid.gotomeeting.client;

import com.exclamationlabs.connid.gotomeeting.GotoMeetingConnection;
import com.exclamationlabs.connid.gotomeeting.model.User;
import com.exclamationlabs.connid.gotomeeting.model.request.UserCreationRequest;
import org.apache.http.client.methods.HttpPost;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.ArrayList;

public class CreateUserClient {

    private GotoMeetingConnection connection;

    public CreateUserClient(GotoMeetingConnection input) {
        connection = input;
    }

    public String execute(User user) {
        UserCreationRequest requestData = new UserCreationRequest();
        requestData.setUsers(new ArrayList<>());
        requestData.getUsers().add(user);

        HttpPost request = connection.createPostRequest("/users?allOrNothing=false", requestData);
        User[] newUsers = connection.executeRequest(request, User[].class);

        if (newUsers == null || newUsers.length == 0) {
            throw new ConnectorException("Response from user creation was invalid");
        }
        return newUsers[0].getKey();
    }
}
