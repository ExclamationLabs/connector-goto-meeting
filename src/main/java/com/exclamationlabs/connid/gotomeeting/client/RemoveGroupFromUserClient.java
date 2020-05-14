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
import org.apache.http.client.methods.HttpDelete;

public class RemoveGroupFromUserClient {

    private GotoMeetingConnection connection;

    public RemoveGroupFromUserClient(GotoMeetingConnection input) {
        connection = input;
    }

    public void execute(String groupKey, String userKey) {
        // Note: GotoMeeting only seems to support removing a user from ALL groups it
        // currently belongs to.  So that is the logic that is invoked here.

        HttpDelete request = connection.createDeleteRequest("/users/" +
                userKey + "/groups");
        connection.executeRequest(request, null);
    }
}
