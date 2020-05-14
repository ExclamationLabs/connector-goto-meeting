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

import com.exclamationlabs.connid.gotomeeting.client.ListGroupsClient;
import com.exclamationlabs.connid.gotomeeting.model.Group;
import com.exclamationlabs.connid.gotomeeting.util.GotoMeetingAbstractClientTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ListGroupsClientTest extends GotoMeetingAbstractClientTest {

    @Test
    public void execute() throws IOException {
        String responseData = "{\"results\":[{\"key\":\"8450026542029090574\",\"name\":\"Alpha Flight\",\"keyPath\":[\"8450026542029090574\"],\"namePath\":[\"Alpha Flight\"],\"userKeys\":[\"8768345053946106373\",\"192683222403936526\"],\"memberCount\":2,\"totalMemberCount\":2},{\"key\":\"1406284574726299398\",\"name\":\"Avengers\",\"keyPath\":[\"1406284574726299398\"],\"namePath\":[\"Avengers\"],\"memberCount\":0,\"totalMemberCount\":0},{\"key\":\"2493304650723090190\",\"name\":\"Defenders\",\"keyPath\":[\"2493304650723090190\"],\"namePath\":[\"Defenders\"],\"userKeys\":[\"5348866090447493637\"],\"memberCount\":1,\"totalMemberCount\":1},{\"key\":\"7232518624985123596\",\"name\":\"Teen Titans\",\"keyPath\":[\"7232518624985123596\"],\"namePath\":[\"Teen Titans\"],\"memberCount\":0,\"totalMemberCount\":0},{\"key\":\"8102581967098349838\",\"name\":\"XMen\",\"keyPath\":[\"8102581967098349838\"],\"namePath\":[\"XMen\"],\"memberCount\":0,\"totalMemberCount\":0}],\"fromIndex\":0,\"toIndex\":5,\"total\":5}\n";
        prepareMockResponse(responseData);

        ListGroupsClient client = new ListGroupsClient(connection);
        List<Group> groups = client.execute();

        assertNotNull(groups);
        assertTrue(groups.size() > 1);
        for (Group current : groups) {
            assertNotNull(current.getKey());
            assertNotNull(current.getName());
        }

    }
}
