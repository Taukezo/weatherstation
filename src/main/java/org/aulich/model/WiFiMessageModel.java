package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("wifimessage")
public class WiFiMessageModel {
    @XStreamAlias("messagetype")
    private String messageType = "";

    @XStreamAlias("messageentrymodels")
    private List<MessageEntryModel> messageEntryModels =
            new ArrayList<MessageEntryModel>();

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<MessageEntryModel> getMessageEntryModels() {
        return messageEntryModels;
    }

    public void setMessageEntryModels(List<MessageEntryModel> messageEntryModels) {
        this.messageEntryModels = messageEntryModels;
    }
}
