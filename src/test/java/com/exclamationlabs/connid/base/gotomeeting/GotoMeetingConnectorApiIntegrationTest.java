/*
    Copyright 2022 Exclamation Labs
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

package com.exclamationlabs.connid.base.gotomeeting;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.gotomeeting.attribute.GotoMeetingGroupAttribute;
import com.exclamationlabs.connid.base.gotomeeting.attribute.GotoMeetingUserAttribute;
import com.exclamationlabs.connid.base.gotomeeting.configuration.GotoMeetingConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GotoMeetingConnectorApiIntegrationTest extends
        ApiIntegrationTest<GotoMeetingConfiguration, GotoMeetingConnector> {

    private static String generatedUserId;
    private static String generatedGroupId;

    @Override
    protected GotoMeetingConfiguration getConfiguration() {
        return new GotoMeetingConfiguration(
                new ConfigurationNameBuilder().withConnector("GOTOMEETING").build()
        );
    }

    @Override
    protected Class<GotoMeetingConnector> getConnectorClass() {
        return GotoMeetingConnector.class;
    }

    @Override
    protected void readConfiguration(GotoMeetingConfiguration webExConfiguration) {
        ConfigurationReader.setupTestConfiguration(webExConfiguration);
    }

    @Before
    public void testSetup() {
        super.setup();
    }

    @Test
    public void test050Test() {
        getConnectorFacade().test();
    }

    @Test
    public void test110UserCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(GotoMeetingUserAttribute.FIRST_NAME.name()).addValue("Fred").build());
        attributes.add(new AttributeBuilder().setName(GotoMeetingUserAttribute.LAST_NAME.name()).addValue("Rubble").build());
        attributes.add(new AttributeBuilder().setName(GotoMeetingUserAttribute.EMAIL.name()).addValue("fred@rubble.com").build());


        Uid newId = getConnectorFacade().create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        generatedUserId = newId.getUidValue();
    }

    @Test
    public void test120UserModify() {
        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.LAST_NAME.name()).addValueToReplace("Flinstone").build());
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.USER_KEY.name()).addValueToReplace(generatedUserId).build());

        Set<AttributeDelta> response = getConnectorFacade().updateDelta(ObjectClass.ACCOUNT, new Uid(generatedUserId),
                attributes, new OperationOptionsBuilder().build());

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test130UsersGet() {
        results = new ArrayList<>();
        getConnectorFacade().search( ObjectClass.ACCOUNT, null, getHandler(), new OperationOptionsBuilder().build());
        assertTrue(getResults().size() >= 1);
    }

    @Test
    public void test132UsersGetPageOne() {
        results = new ArrayList<>();
        getConnectorFacade().search( ObjectClass.ACCOUNT, null, getHandler(),
                new OperationOptionsBuilder().setPageSize(5).setPagedResultsOffset(1).build());
        assertEquals(5, getResults().size());
    }

    @Test
    public void test134UsersGetPageTwo() {
        results = new ArrayList<>();
        getConnectorFacade().search( ObjectClass.ACCOUNT, null, getHandler(),
                new OperationOptionsBuilder().setPageSize(3).setPagedResultsOffset(4).build());
        assertEquals(3, getResults().size());
    }

    @Test
    public void test140UserGet() {
        results = new ArrayList<>();
        Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue(generatedUserId).build();
        getConnectorFacade().search( ObjectClass.ACCOUNT, new EqualsFilter(idAttribute),
                getHandler(), new OperationOptionsBuilder().build());
        assertEquals(1, getResults().size());
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getUid().getUidValue()));
    }

    @Test
    public void test142UserGetUsingEmailFilter() {
        results = new ArrayList<>();
        Attribute idAttribute = new AttributeBuilder().setName(GotoMeetingUserAttribute.EMAIL.name()).
                addValue("david@gilmour.com").build();
        getConnectorFacade().search( ObjectClass.ACCOUNT, new EqualsFilter(idAttribute),
                getHandler(), new OperationOptionsBuilder().build());
        assertEquals(1, getResults().size());
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getUid().getUidValue()));
    }

    @Test
    public void test210GroupCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(GotoMeetingGroupAttribute.GROUP_NAME.name()).
                addValue("Flinstones").build());

        Uid newId = getConnectorFacade().create(ObjectClass.GROUP,
                attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        generatedGroupId = newId.getUidValue();
    }

    @Test
    public void test220GroupModify() {
        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingGroupAttribute.GROUP_NAME.name()).addValueToReplace("Flinstones2").build());
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingGroupAttribute.GROUP_KEY.name()).addValueToReplace(generatedGroupId).build());

        Set<AttributeDelta> response = getConnectorFacade().updateDelta(
                ObjectClass.GROUP, new Uid(generatedGroupId), attributes, new OperationOptionsBuilder().build());
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test230GroupsGet() {
        results = new ArrayList<>();
        getConnectorFacade().search(
                ObjectClass.GROUP, null, getHandler(), new OperationOptionsBuilder().build());
        assertTrue(results.size() >= 1);
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getUid().getUidValue()));
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getName().getNameValue()));
    }

    @Test
    public void test232GroupsGetPageOne() {
        results = new ArrayList<>();
        getConnectorFacade().search( ObjectClass.GROUP, null, getHandler(),
                new OperationOptionsBuilder().setPageSize(3).setPagedResultsOffset(1).build());
        assertEquals(3, getResults().size());
    }

    @Test
    public void test234GroupsGetPageTwo() {
        results = new ArrayList<>();
        getConnectorFacade().search( ObjectClass.GROUP, null, getHandler(),
                new OperationOptionsBuilder().setPageSize(2).setPagedResultsOffset(3).build());
        assertEquals(2, getResults().size());
    }

    @Test
    public void test240GroupGet() {
        Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue(generatedGroupId).build();
        getConnectorFacade().search(
                ObjectClass.GROUP, new EqualsFilter(idAttribute), getHandler(),
                new OperationOptionsBuilder().build());
        assertEquals(1, getResults().size());
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getUid().getUidValue()));
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getName().getNameValue()));
    }

    @Test
    public void test242GroupGetUsingNameFilter() {
        results = new ArrayList<>();
        Attribute idAttribute = new AttributeBuilder().setName(GotoMeetingGroupAttribute.GROUP_NAME.name()).
                addValue("Defenders").build();
        getConnectorFacade().search( ObjectClass.GROUP, new EqualsFilter(idAttribute),
                getHandler(), new OperationOptionsBuilder().build());
        assertEquals(1, getResults().size());
        assertTrue(StringUtils.isNotBlank(getResults().get(0).getUid().getUidValue()));
    }

    @Test
    public void test260AddGroupToUser() {
        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.USER_KEY.name()).addValueToReplace(generatedUserId).build());
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.GROUP_IDS.name()).addValueToReplace(
                Collections.singletonList(generatedGroupId)).build());
        Set<AttributeDelta> response = getConnectorFacade().updateDelta(ObjectClass.ACCOUNT,
                new Uid(generatedUserId), attributes, new OperationOptionsBuilder().build());
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test270RemoveGroupFromUser() {
        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.USER_KEY.name()).addValueToReplace(generatedUserId).build());
        attributes.add(new AttributeDeltaBuilder().setName(
                GotoMeetingUserAttribute.GROUP_IDS.name()).addValueToReplace(
                Collections.EMPTY_LIST).build());
        Set<AttributeDelta> response = getConnectorFacade().updateDelta(ObjectClass.ACCOUNT,
                new Uid(generatedUserId), attributes, new OperationOptionsBuilder().build());
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test290GroupDelete() {
        getConnectorFacade().delete(ObjectClass.GROUP, new Uid(generatedGroupId), new OperationOptionsBuilder().build());
    }

    @Test
    public void test390UserDelete() {
        getConnectorFacade().delete(ObjectClass.ACCOUNT, new Uid(generatedUserId), new OperationOptionsBuilder().build());
    }
}