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
import org.apache.http.client.methods.HttpPut;

public class UpdateUserClient {

    private GotoMeetingConnection connection;

    public UpdateUserClient(GotoMeetingConnection input) {
        connection = input;
    }

    public String execute(User user) {
        String userKey = user.getKey();
        user.setKey(null); // cannot pass key value in update JSON, can only be present in URL

        HttpPut request = connection.createPutRequest("/users/" +
                userKey + "?allOrNothing=true", user);
        connection.executeRequest(request, null);

        return userKey;
    }
}
