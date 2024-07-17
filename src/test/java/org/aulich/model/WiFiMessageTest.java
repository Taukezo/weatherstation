package org.aulich.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WiFiMessageTest {
    WiFiMessage wiFiMessage = new WiFiMessage();
    WiFiMessageModel wiFiMessageModel = new WiFiMessageModel();

    @Test
    public void test() {
        wiFiMessageModel.setMessageType(WiFiMessage.MESSAGETYPE_ECOWITT);
        MessageEntryModel messageEntryModel = new MessageEntryModel();
        messageEntryModel.setKey("Key1");
        messageEntryModel.setValue("Value1");
        wiFiMessageModel.getMessageEntryModels().add(messageEntryModel);
        wiFiMessageModel.getMessageEntryModels().add(new MessageEntryModel("Key2", "Value2"));
        WiFiMessage wiFiMessage = new WiFiMessage();
        wiFiMessage.setWiFiMessageModel(wiFiMessageModel);
        if (wiFiMessage.save("Test.xml")) {
            assertTrue(true);
        } else {
            fail();
        }
    }

}
