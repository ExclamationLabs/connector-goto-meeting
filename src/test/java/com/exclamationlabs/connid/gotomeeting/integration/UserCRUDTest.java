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

package com.exclamationlabs.connid.gotomeeting.integration;

import com.exclamationlabs.connid.gotomeeting.GotoMeetingConnector;
import com.exclamationlabs.connid.gotomeeting.client.*;
import com.exclamationlabs.connid.gotomeeting.model.User;
import com.exclamationlabs.connid.gotomeeting.util.TestGotoMeetingConfiguration;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserCRUDTest {

    private static final String FIXED_GROUP_ID = "2493304650723090190"; // Defenders

    private GotoMeetingConnector connector;

    private static String generatedUserId;

    @Before
    public void setup() {
        connector = new GotoMeetingConnector();
        connector.init(new TestGotoMeetingConfiguration());
    }

    @Test
    public void test10CrudCreateUser() {
        CreateUserClient client = new CreateUserClient(connector.getConnection());
        User user = new User();
        user.setEmail("dennis@eckersly.com");
        user.setFirstName("Dennis");
        user.setLastName("Eckersly");
        generatedUserId = client.execute(user);
        assertNotNull(generatedUserId);
    }

    @Test
    public void test20CrudReadSingleUser() {

        GetUserClient client = new GetUserClient(connector.getConnection());
        User result = client.execute("5348866090447493637"); // John lee Hooker

        assertNotNull(result);
        assertNotNull(result.getEmail());
        assertNotNull(result.getLastName());
    }

    @Test
    public void test21CrudReadUsers() {
        ListUsersClient client = new ListUsersClient(connector.getConnection());
        List<User> userList = client.execute();

        assertNotNull(userList);
        assertFalse(userList.isEmpty());
    }


    @Test
    public void test30CrudUpdate() {
        UpdateUserClient client = new UpdateUserClient(connector.getConnection());

        User updateUser = new User();
        updateUser.setKey(generatedUserId);
        updateUser.setLastName("Eck");

        String uidResponse = client.execute(updateUser);
        assertNotNull(uidResponse);
        assertEquals(generatedUserId, uidResponse);
    }

     @Test
     public void test31AddGroupToUser() {
         AddGroupToUserClient client = new AddGroupToUserClient(connector.getConnection());
         client.execute(FIXED_GROUP_ID, generatedUserId);
     }

     @Test
     public void test32RemoveGroupFromUser() {
         RemoveGroupFromUserClient client = new RemoveGroupFromUserClient(connector.getConnection());
         client.execute(FIXED_GROUP_ID, generatedUserId);
     }

    @Test
    public void test41CrudDelete() {
        DeleteUserClient client = new DeleteUserClient(connector.getConnection());
        client.execute(generatedUserId);
    }

}
