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

import com.exclamationlabs.connid.gotomeeting.client.CreateGroupClient;
import com.exclamationlabs.connid.gotomeeting.model.Group;
import com.exclamationlabs.connid.gotomeeting.util.GotoMeetingAbstractClientTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CreateGroupClientTest extends GotoMeetingAbstractClientTest {

    @Test
    public void execute() throws IOException {
        final String responseData = "4654957705246364934";
        prepareMockResponse(responseData);

        CreateGroupClient client = new CreateGroupClient(connection);
        Group newGroup = new Group();
        newGroup.setName("Hello");
        String newId = client.execute(newGroup);

        assertNotNull(newId);
        assertEquals(responseData, newId);

    }
}
