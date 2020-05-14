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

import com.exclamationlabs.connid.gotomeeting.client.GetUserClient;
import com.exclamationlabs.connid.gotomeeting.model.User;
import com.exclamationlabs.connid.gotomeeting.util.GotoMeetingAbstractClientTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GetUserClientTest extends GotoMeetingAbstractClientTest {

    @Test
    public void execute() throws IOException {
        String responseData = "{\"key\":\"5348866090447493637\",\"email\":\"jlh@yahoo.com\",\"firstName\":\"John Lee\",\"lastName\":\"Hooker\",\"locale\":\"en_US\",\"timeZone\":\"America/Los_Angeles\",\"licenseKeys\":[\"1355534416108530190\"],\"products\":[\"G2MFREE\"],\"groupKey\":\"2493304650723090190\",\"groupName\":\"Defenders\",\"groupKeyPath\":[\"2493304650723090190\"],\"groupNamePath\":[\"Defenders\"],\"settings\":{\"G2MFREE\":{\"attendeeListEnabled\":true,\"chatEnabled\":true},\"G2M\":{\"tollAccessible\":true,\"tollEnabled\":true,\"voipAccessible\":true,\"voipEnabled\":true,\"privateConfCallAccessible\":true,\"privateConfCallEnabled\":false,\"tollFreeAccessible\":true,\"tollFreeEnabled\":true,\"dialOutAccessible\":true,\"dialOutEnabled\":true,\"tollCountries\":[\"US\"],\"allowedTollCountries\":[\"AT\",\"AU\",\"BE\",\"BG\",\"BR\",\"CA\",\"CH\",\"CL\",\"CO\",\"CZ\",\"DE\",\"DK\",\"ES\",\"FI\",\"FR\",\"GB\",\"GR\",\"HU\",\"IE\",\"IL\",\"IT\",\"LU\",\"MX\",\"MY\",\"NL\",\"NO\",\"NZ\",\"PA\",\"PE\",\"RO\",\"SE\",\"TR\",\"US\",\"ZA\"],\"tollFreeCountries\":[\"US\"],\"allowedTollFreeCountries\":[\"AE\",\"AR\",\"AT\",\"AU\",\"BE\",\"BG\",\"BH\",\"BR\",\"BY\",\"CA\",\"CH\",\"CL\",\"CN\",\"CO\",\"CR\",\"CZ\",\"DE\",\"DK\",\"ES\",\"FI\",\"FR\",\"GB\",\"GR\",\"HK\",\"HU\",\"ID\",\"IE\",\"IL\",\"IN\",\"IS\",\"IT\",\"JP\",\"KR\",\"LU\",\"MX\",\"MY\",\"NL\",\"NO\",\"NZ\",\"PA\",\"PE\",\"PH\",\"PL\",\"PT\",\"RO\",\"RU\",\"SA\",\"SE\",\"SG\",\"SK\",\"TH\",\"TR\",\"TW\",\"UA\",\"US\",\"UY\",\"VN\",\"ZA\"],\"dialOutCountries\":[\"AE\",\"AR\",\"AT\",\"AU\",\"BE\",\"BG\",\"BH\",\"BR\",\"BY\",\"CA\",\"CH\",\"CL\",\"CN\",\"CO\",\"CR\",\"CZ\",\"DE\",\"DK\",\"EG\",\"ES\",\"FI\",\"FR\",\"GB\",\"GR\",\"HK\",\"HU\",\"ID\",\"IE\",\"IL\",\"IN\",\"IS\",\"IT\",\"JP\",\"KR\",\"KW\",\"LU\",\"MX\",\"MY\",\"NL\",\"NO\",\"NZ\",\"OM\",\"PA\",\"PE\",\"PH\",\"PK\",\"PL\",\"PT\",\"QA\",\"RO\",\"RU\",\"SA\",\"SE\",\"SG\",\"TH\",\"TR\",\"TW\",\"UA\",\"US\",\"UY\",\"ZA\"],\"allowedDialOutCountries\":[\"AE\",\"AR\",\"AT\",\"AU\",\"BE\",\"BG\",\"BH\",\"BR\",\"BY\",\"CA\",\"CH\",\"CL\",\"CN\",\"CO\",\"CR\",\"CZ\",\"DE\",\"DK\",\"EG\",\"ES\",\"FI\",\"FR\",\"GB\",\"GR\",\"HK\",\"HU\",\"ID\",\"IE\",\"IL\",\"IN\",\"IS\",\"IT\",\"JP\",\"KR\",\"KW\",\"LU\",\"MX\",\"MY\",\"NL\",\"NO\",\"NZ\",\"OM\",\"PA\",\"PE\",\"PH\",\"PK\",\"PL\",\"PT\",\"QA\",\"RO\",\"RU\",\"SA\",\"SE\",\"SG\",\"TH\",\"TR\",\"TW\",\"UA\",\"US\",\"UY\",\"ZA\"],\"attendeeListEnabled\":true,\"keyboardMouseControlEnabled\":true,\"chatEnabled\":true,\"recordingEnabled\":true,\"webcamEnabled\":true,\"webViewerAccessible\":true,\"webViewerEnabled\":false,\"onlineRecordingAccessible\":true,\"onlineRecordingEnabled\":true,\"onlineRecordingTranscriptsAccessible\":false,\"onlineRecordingTranscriptsEnabled\":false,\"standaloneAudioAccessible\":true,\"standaloneAudioEnabled\":true,\"openMeetingsAccessible\":true,\"openMeetingsEnabled\":true,\"ux2019Accessible\":true,\"ux2019Enabled\":true,\"meetingHubEnabled\":true,\"meetingHubAccessible\":true,\"npaEnabled\":true,\"npaConfirmationDialogEnabled\":false,\"npaSameAccountRestrictionEnabled\":false,\"roomSystemDualDisplayEnabled\":true}},\"passwordCreated\":false,\"invitedDate\":1588085082030,\"nameLocked\":false,\"emailLocked\":false,\"identityReadOnly\":false,\"canAuthenticate\":false,\"status\":\"INVITED\",\"inviteCount\":1}\n";
        prepareMockResponse(responseData);

        GetUserClient client = new GetUserClient(connection);
        User user = client.execute("1234");

        assertNotNull(user);
        assertNotNull(user.getKey());
        assertNotNull(user.getFirstName());
        assertNotNull(user.getLastName());
        assertNotNull(user.getEmail());
    }
}
