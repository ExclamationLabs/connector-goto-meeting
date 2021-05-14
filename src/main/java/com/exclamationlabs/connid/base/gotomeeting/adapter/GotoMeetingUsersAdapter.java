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
import com.exclamationlabs.connid.base.gotomeeting.model.GotoMeetingUser;
import org.identityconnectors.framework.common.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.gotomeeting.attribute.GotoMeetingUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class GotoMeetingUsersAdapter extends BaseAdapter<GotoMeetingUser> {

    @Override
    public ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    @Override
    public Class<GotoMeetingUser> getIdentityModelClass() {
        return GotoMeetingUser.class;
    }

    @Override
    public List<ConnectorAttribute> getConnectorAttributes() {
        List<ConnectorAttribute> result = new ArrayList<>();

        result.add(new ConnectorAttribute(USER_KEY.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(FIRST_NAME.name(), STRING));
        result.add(new ConnectorAttribute(LAST_NAME.name(), STRING));
        result.add(new ConnectorAttribute(EMAIL.name(), STRING));
        result.add(new ConnectorAttribute(LOCALE.name(), STRING));
        result.add(new ConnectorAttribute(ADMIN.name(), STRING));
        result.add(new ConnectorAttribute(ROLE.name(), STRING));
        result.add(new ConnectorAttribute(TIME_ZONE.name(), STRING));
        result.add(new ConnectorAttribute(STATUS.name(), STRING));
        result.add(new ConnectorAttribute(NAME_LOCKED.name(), BOOLEAN, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(EMAIL_LOCKED.name(), BOOLEAN, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(IDENTITY_READ_ONLY.name(), BOOLEAN, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(CAN_AUTHENTICATE.name(), BOOLEAN, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(INVITE_COUNT.name(), INTEGER, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(ADMIN_ROLES.name(), STRING, NOT_UPDATEABLE, MULTIVALUED));
        result.add(new ConnectorAttribute(PRODUCTS.name(), STRING, NOT_UPDATEABLE, MULTIVALUED));
        result.add(new ConnectorAttribute(LICENSE_KEYS.name(), STRING, NOT_UPDATEABLE, MULTIVALUED));
        result.add(new ConnectorAttribute(ADMIN_ROLES.name(), STRING, NOT_UPDATEABLE, MULTIVALUED));
        result.add(new ConnectorAttribute(GROUP_IDS.name(), STRING, NOT_UPDATEABLE, MULTIVALUED));

        return result;
    }

    @Override
    protected List<Attribute> constructAttributes(GotoMeetingUser user) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(AttributeBuilder.build(USER_KEY.name(), user.getKey()));
        attributes.add(AttributeBuilder.build(EMAIL.name(), user.getEmail()));
        attributes.add(AttributeBuilder.build(FIRST_NAME.name(), user.getFirstName()));
        attributes.add(AttributeBuilder.build(LAST_NAME.name(), user.getLastName()));
        attributes.add(AttributeBuilder.build(TIME_ZONE.name(), user.getTimeZone()));
        attributes.add(AttributeBuilder.build(STATUS.name(), user.getStatus()));
        attributes.add(AttributeBuilder.build(GROUP_IDS.name(), user.getGroupKey()));
        attributes.add(AttributeBuilder.build(LOCALE.name(), user.getLocale()));
        attributes.add(AttributeBuilder.build(NAME_LOCKED.name(), user.getNameLocked()));
        attributes.add(AttributeBuilder.build(EMAIL_LOCKED.name(), user.getEmailLocked()));
        attributes.add(AttributeBuilder.build(IDENTITY_READ_ONLY.name(), user.getIdentityReadOnly()));
        attributes.add(AttributeBuilder.build(CAN_AUTHENTICATE.name(), user.getCanAuthenticate()));
        attributes.add(AttributeBuilder.build(INVITE_COUNT.name(), user.getInviteCount()));
        attributes.add(AttributeBuilder.build(ADMIN_ROLES.name(), user.getAdminRoles()));
        attributes.add(AttributeBuilder.build(PRODUCTS.name(), user.getProducts()));
        attributes.add(AttributeBuilder.build(LICENSE_KEYS.name(), user.getLicenseKeys()));

        return attributes;
    }

    @Override
    protected GotoMeetingUser constructModel(Set<Attribute> attributes, boolean isCreation) {
        GotoMeetingUser user = new GotoMeetingUser();
        user.setKey(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));

        if (user.getKey()==null) {
            user.setKey(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, USER_KEY));
        }

        user.setFirstName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, FIRST_NAME));
        user.setLastName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, LAST_NAME));
        user.setEmail(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EMAIL));
        user.setTimeZone(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, TIME_ZONE));
        user.setLocale(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, LOCALE));
        return user;
    }
}
