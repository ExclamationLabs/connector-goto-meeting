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

import com.exclamationlabs.connid.gotomeeting.client.UpdateUserClient;
import com.exclamationlabs.connid.gotomeeting.model.User;
import com.exclamationlabs.connid.gotomeeting.util.GotoMeetingAbstractClientTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUserClientTest extends GotoMeetingAbstractClientTest {

    @Test
    public void execute() throws IOException {
        final String ITEM_KEY = "797979";
        prepareMockResponseEmpty();

        UpdateUserClient client = new UpdateUserClient(connection);
        User modifyUser = new User();
        modifyUser.setKey(ITEM_KEY);
        modifyUser.setFirstName("Fido");
        String newId = client.execute(modifyUser);

        assertNotNull(newId);
        assertEquals(ITEM_KEY, newId);

    }
}
