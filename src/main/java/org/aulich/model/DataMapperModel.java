package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Defines the mapping between a measurement type declaration inside a message,
 * sent from the weatherstation and the name of the measurement series
 * in the database.
 *
 * @author Thomas Aulich
 */
@XStreamAlias("datamapper")
public class DataMapperModel {
    @XStreamAlias("messagefield")
    @XStreamAsAttribute
    private String messageField = "";

    @XStreamAlias("databasefield")
    @XStreamAsAttribute
    private String dataBaseField = "";

    @XStreamAlias("datatype")
    @XStreamAsAttribute
    private String dataType = "";

    public DataMapperModel(String messageField, String dataBaseField, String dataType) {
        this.messageField = messageField;
        this.dataBaseField = dataBaseField;
        this.dataType = dataType;
    }

    public String getMessageField() {
        return messageField;
    }

    public void setMessageField(String messageField) {
        this.messageField = messageField;
    }

    public String getDataBaseField() {
        return dataBaseField;
    }

    public void setDataBaseField(String dataBaseField) {
        this.dataBaseField = dataBaseField;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
