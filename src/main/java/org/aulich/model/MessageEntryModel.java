package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("messageentry")
public class MessageEntryModel {
    @XStreamAlias("key")
    private String key = "";
    @XStreamAlias("value")
    private String value = "";
    public MessageEntryModel() {
        this.key = key;
        this.value = value;
    }
    public MessageEntryModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
