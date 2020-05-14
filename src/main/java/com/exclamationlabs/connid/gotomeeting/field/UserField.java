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

package com.exclamationlabs.connid.gotomeeting.field;

import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum UserField {
    USER_KEY(String.class, AttributeInfo.Flags.NOT_UPDATEABLE),
    FIRST_NAME,
    LAST_NAME,
    EMAIL,
    LOCALE,
    TIME_ZONE,
    ADMIN,
    ROLE,
    STATUS,
    NAME_LOCKED(Boolean.class),
    EMAIL_LOCKED(Boolean.class),
    IDENTITY_READ_ONLY(Boolean.class),
    CAN_AUTHENTICATE(Boolean.class),
    INVITE_COUNT(Integer.class),
    ADMIN_ROLES(String.class, AttributeInfo.Flags.MULTIVALUED),
    PRODUCTS(String.class, AttributeInfo.Flags.MULTIVALUED),
    LICENSE_KEYS(String.class, AttributeInfo.Flags.MULTIVALUED),
    GROUP_IDS(String.class, AttributeInfo.Flags.MULTIVALUED);

    private Class dataType;
    private Set<AttributeInfo.Flags> dataFlags;

    UserField() {
        dataType = String.class;
        dataFlags = new HashSet<>();
    }

    UserField(Class typeClass, AttributeInfo.Flags... flags) {
        dataType = typeClass;
        dataFlags = new HashSet<>();
        dataFlags.addAll(Arrays.asList(flags));
    }

    public Class getDataType() {
        return dataType;
    }

    public Set<AttributeInfo.Flags> getDataFlags() {
        return dataFlags;
    }
}
