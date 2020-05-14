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

package com.exclamationlabs.connid.gotomeeting.adapter;

import com.exclamationlabs.connid.gotomeeting.GotoMeetingConnection;
import com.exclamationlabs.connid.gotomeeting.client.*;
import com.exclamationlabs.connid.gotomeeting.field.UserField;
import com.exclamationlabs.connid.gotomeeting.model.User;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class UsersAdapter implements AccessManagementAdapter {

    private static final Log LOG = Log.getLog(UsersAdapter.class);

    private GotoMeetingConnection connection;


    public UsersAdapter(GotoMeetingConnection in) {
        connection = in;
    }

    @Override
    public Uid create(Set<Attribute> attributes) {
        LOG.info("Enter Create GotoMeeting user with supplied attributes {0}", attributes);
        CreateUserClient client = new CreateUserClient(connection);
        String uidValue;

        User user = new User();
        attributes.forEach(item -> map(user, item));
        uidValue = client.execute(user);

        LOG.info("UID for new user is {0}", uidValue);

        performGroupUpdate(uidValue, attributes, false);
        return new Uid(uidValue);
    }

    @Override
    public Uid update(Uid uid, Set<Attribute> attributes) {
        LOG.info("Enter Update GotoMeeting user with supplied attributes {0} for uid {1}",
                attributes, uid.getUidValue());
        User user = new User();
        user.setKey(uid.getUidValue());
        attributes.forEach(item -> map(user, item));
        UpdateUserClient client = new UpdateUserClient(connection);
        String updatedUserId = client.execute(user);
        LOG.info("Updated user id {0}", updatedUserId);

        performGroupUpdate(uid.getUidValue(), attributes, true);
        return uid;
    }

    @Override
    public void delete(Uid uid) {
        LOG.info("Delete GotoMeeting User {0}", uid.getUidValue());
        DeleteUserClient client = new DeleteUserClient(connection);
        client.execute(uid.getUidValue());
        LOG.info("Delete GotoMeeting User {0} completed.", uid.getUidValue());
    }

    @Override
    public void get(String query, ResultsHandler resultsHandler) {
        if (queryAllRecords(query)) {
            LOG.info("Get All GotoMeeting Users");
            ListUsersClient client = new ListUsersClient(connection);
            List<User> userList = client.execute();

            for (User current : userList) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(current.getKey())
                                .setName(current.getEmail())
                                .setObjectClass(ObjectClass.ACCOUNT)
                                .build());
            }
        } else {
            // Query for single user
            LOG.info("Get user information for uid {0}", query);
            GetUserClient client = new GetUserClient(connection);
            User lookup = client.execute(query);
            if (lookup != null) {
                resultsHandler.handle(userToConnectorObject(lookup));
            }
        }
    }

    private void map(User user, Attribute attribute) {

        if (Name.NAME.equalsIgnoreCase(attribute.getName())) {
            user.setEmail(readAttributeValue(attribute).toString());
        } else if (Uid.NAME.equalsIgnoreCase(attribute.getName())) {
            user.setKey(readAttributeValue(attribute).toString());
        } else {
            UserField field = UserField.valueOf(attribute.getName());

            switch (field) {
                case FIRST_NAME:
                    user.setFirstName(readAttributeValue(attribute).toString());
                    break;
                case LAST_NAME:
                    user.setLastName(readAttributeValue(attribute).toString());
                    break;
                case EMAIL:
                    user.setEmail(readAttributeValue(attribute).toString());
                    break;
                case USER_KEY:
                    user.setKey(readAttributeValue(attribute).toString());
                    break;
                case TIME_ZONE:
                    user.setTimeZone(readAttributeValue(attribute).toString());
                    break;
                case LOCALE:
                    user.setLocale(readAttributeValue(attribute).toString());
                    break;

                default:
                    LOG.info("Attribute " + attribute.getName() + " not recognized.");
                    break;
            }
        }
    }

    private static ConnectorObject userToConnectorObject(final User user) {

        final ConnectorObjectBuilder builder = new ConnectorObjectBuilder()
                .setObjectClass(ObjectClass.ACCOUNT)
                .setUid(user.getKey())
                .setName(user.getEmail())
                .addAttribute(UserField.USER_KEY.name(), user.getKey())
                .addAttribute(UserField.FIRST_NAME.name(), user.getFirstName())
                .addAttribute(UserField.LAST_NAME.name(), user.getLastName())
                .addAttribute(UserField.EMAIL.name(), user.getEmail())
                .addAttribute(UserField.LOCALE.name(), user.getLocale())
                .addAttribute(UserField.TIME_ZONE.name(), user.getTimeZone())
                .addAttribute(UserField.STATUS.name(), user.getStatus())
                .addAttribute(UserField.NAME_LOCKED.name(), user.getNameLocked())
                .addAttribute(UserField.EMAIL_LOCKED.name(), user.getEmailLocked())
                .addAttribute(UserField.IDENTITY_READ_ONLY.name(), user.getIdentityReadOnly())
                .addAttribute(UserField.CAN_AUTHENTICATE.name(), user.getCanAuthenticate())
                .addAttribute(UserField.INVITE_COUNT.name(), user.getInviteCount())
                .addAttribute(UserField.ADMIN_ROLES.name(), user.getAdminRoles())
                .addAttribute(UserField.PRODUCTS.name(), user.getProducts())
                .addAttribute(UserField.LICENSE_KEYS.name(), user.getLicenseKeys())
                .addAttribute(UserField.GROUP_IDS.name(), user.getGroupKey());

        return builder.build();
    }

    private void performGroupUpdate(String uidValue, Set<Attribute> attributes,
                                    boolean isUpdate) {
        LOG.info("Check for updated group ID's for user, is update {0}", isUpdate);

        Optional<Attribute> hasUpdatedGroupKeys = attributes.stream().filter(current -> current.getName().equals(UserField.GROUP_IDS.name())).findFirst();
        String newGroupKey = null;
        if (hasUpdatedGroupKeys.isPresent()) {
            if (hasUpdatedGroupKeys.get().getValue() != null) {
                Optional<Object> updatedGroupKey =
                        hasUpdatedGroupKeys.get().getValue().stream().filter(Objects::nonNull).findFirst();
                if (updatedGroupKey.isPresent()) {
                    newGroupKey = updatedGroupKey.get().toString();
                }
            }

            if (isUpdate) { // remove existing group from user
                RemoveGroupFromUserClient removeClient = new RemoveGroupFromUserClient(connection);
                removeClient.execute("NA", uidValue);
            }

            // Assign new group to user
            if (newGroupKey != null) {
                AddGroupToUserClient addClient = new AddGroupToUserClient(connection);
                addClient.execute(newGroupKey, uidValue);
            }

        }
    }

}
