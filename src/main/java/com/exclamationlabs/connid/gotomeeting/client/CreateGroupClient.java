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
import com.exclamationlabs.connid.gotomeeting.model.Group;
import org.apache.http.client.methods.HttpPost;

public class CreateGroupClient {

    private GotoMeetingConnection connection;

    public CreateGroupClient(GotoMeetingConnection input) {
        connection = input;
    }

    public String execute(Group group) {

        HttpPost request = connection.createPostRequest("/groups", group);
        return connection.executeRequest(request, String.class);
    }
}
