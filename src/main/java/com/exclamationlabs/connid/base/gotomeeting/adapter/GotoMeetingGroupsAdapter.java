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

package com.exclamationlabs.connid.base.gotomeeting.adapter;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingGroup;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.INTEGER;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.gotomeeting.attribute.GotoMeetingGroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;

public class GotoMeetingGroupsAdapter extends BaseAdapter<GotoMeetingGroup> {

    @Override
    public ObjectClass getType() {
        return ObjectClass.GROUP;
    }

    @Override
    public Class<GotoMeetingGroup> getIdentityModelClass() {
        return GotoMeetingGroup.class;
    }

    @Override
    public List<ConnectorAttribute> getConnectorAttributes() {
        List<ConnectorAttribute> result = new ArrayList<>();
        result.add(new ConnectorAttribute(GROUP_KEY.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(GROUP_NAME.name(), STRING, REQUIRED));
        result.add(new ConnectorAttribute(KEY_PATH.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(NAME_PATH.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(USER_KEYS.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(TOTAL_MEMBER_COUNT.name(), INTEGER, NOT_UPDATEABLE));
        return result;
    }

    @Override
    protected List<Attribute> constructAttributes(GotoMeetingGroup group) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(AttributeBuilder.build(GROUP_KEY.name(), group.getKey()));
        attributes.add(AttributeBuilder.build(GROUP_NAME.name(), group.getName()));
        attributes.add(AttributeBuilder.build(KEY_PATH.name(), group.getKeyPath()));
        attributes.add(AttributeBuilder.build(NAME_PATH.name(), group.getNamePath()));
        attributes.add(AttributeBuilder.build(USER_KEYS.name(), group.getUserKeys()));
        attributes.add(AttributeBuilder.build(TOTAL_MEMBER_COUNT.name(), group.getTotalMemberCount()));
        return attributes;
    }

    @Override
    protected GotoMeetingGroup constructModel(Set<Attribute> attributes, boolean isCreation) {
        GotoMeetingGroup group = new GotoMeetingGroup();
        group.setKey(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));

        if (group.getKey()==null) {
            group.setKey(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, GROUP_KEY));
        }

        group.setName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, GROUP_NAME));
        return group;
    }
}
