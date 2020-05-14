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
import com.exclamationlabs.connid.gotomeeting.field.GroupField;
import com.exclamationlabs.connid.gotomeeting.model.Group;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

public class GroupsAdapter implements AccessManagementAdapter {

    private static final Log LOG = Log.getLog(GroupsAdapter.class);

    private GotoMeetingConnection connection;

    public GroupsAdapter(GotoMeetingConnection input) {
        connection = input;
    }

    @Override
    public Uid create(Set<Attribute> attributes) {
        LOG.info("Create new group, attributes {0}", attributes);
        CreateGroupClient client = new CreateGroupClient(connection);
        Group group = new Group();
        attributes.forEach(item -> map(group, item));
        return new Uid(client.execute(group));
    }

    @Override
    public Uid update(Uid uid, Set<Attribute> attributes) {
        LOG.info("Update group name for uid {0}, attributes {1}", uid.getUidValue(), attributes);
        Group group = new Group();
        group.setKey(uid.getUidValue());
        UpdateGroupClient client = new UpdateGroupClient(connection);
        attributes.forEach(item -> map(group, item));
        return new Uid(client.execute(group));
    }

    @Override
    public void delete(Uid uid) {
        LOG.info("Delete group, uid {0}", uid.getUidValue());

        DeleteGroupClient client = new DeleteGroupClient(connection);
        client.execute(uid.getUidValue());
        LOG.info("Successfully deleted group id {1}", uid.getUidValue());

    }

    @Override
    public void get(String query, ResultsHandler resultsHandler) {

        if (queryAllRecords(query)) {
            LOG.info("Get All groups");
            // Query for all groups
            ListGroupsClient client = new ListGroupsClient(connection);
            List<Group> groupList = client.execute();

            for (Group current : groupList) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(current.getKey())
                                .setName(current.getName())
                                .setObjectClass(ObjectClass.GROUP)
                                .build());
            }
        } else {
            // Query for single group
            LOG.info("Get group information for uid {0}", query);
            GetGroupClient client = new GetGroupClient(connection);
            Group lookup = client.execute(query);
            if (lookup != null) {
                resultsHandler.handle(groupToConnectorObject(lookup));
            }
        }
    }

    private void map(Group group, Attribute attribute) {

        if (Name.NAME.equalsIgnoreCase(attribute.getName())) {
            group.setName(readAttributeValue(attribute).toString());
        } else if (Uid.NAME.equalsIgnoreCase(attribute.getName())) {
            group.setKey(readAttributeValue(attribute).toString());
        } else {
            GroupField field = GroupField.valueOf(attribute.getName());

            if (field == GroupField.GROUP_NAME) {
                group.setName(readAttributeValue(attribute).toString());
            }
        }
    }

    private static ConnectorObject groupToConnectorObject(final Group group) {
        final ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setObjectClass(ObjectClass.GROUP);
        builder.setUid(group.getKey());
        builder.setName(group.getName());
        builder.addAttribute(OperationalAttributes.ENABLE_NAME, Boolean.TRUE);
        builder.addAttribute(GroupField.GROUP_KEY.name(), group.getKey());
        builder.addAttribute(GroupField.GROUP_NAME.name(), group.getName());
        builder.addAttribute(GroupField.KEY_PATH.name(), group.getKeyPath());
        builder.addAttribute(GroupField.NAME_PATH.name(), group.getNamePath());
        builder.addAttribute(GroupField.USER_KEYS.name(), group.getUserKeys());
        builder.addAttribute(GroupField.TOTAL_MEMBER_COUNT.name(), group.getTotalMemberCount());
        return builder.build();
    }
}
