package org.aulich.utilities;

import org.aulich.model.DataMapperModel;
import org.aulich.model.MessageEntryModel;
import org.aulich.model.WiFiMessageModel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MessageParser {
    public static String getString(WiFiMessageModel msg, String key) {
        String value = MessageParser.getValueString(msg, key);
        return Objects.requireNonNullElse(value, "");
    }

    public static BigDecimal getValueBigDecimal(WiFiMessageModel msg, String key) {
        return new BigDecimal(Objects.requireNonNullElse(MessageParser.getValueString(msg, key), "0.0")).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    public static String getValueString(WiFiMessageModel msg, String key) {
        for (MessageEntryModel entry : msg.getMessageEntryModels()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Date getValueDate(WiFiMessageModel msg, String key) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (MessageEntryModel entry : msg.getMessageEntryModels()) {
            if (entry.getKey().equals(key)) {
                return formatter.parse(entry.getValue());
            }
        }
        return null;
    }

    public static Object getValue(WiFiMessageModel msg, String key) {
        DataMapperModel model = DataMapperUtil.getDataMapperByMSGField(key);
        if (model != null) {
            switch (model.getDataType()) {
                case DataMapperUtil.TYPE_STRING:
                    return getValueString(msg, model.getMessageField());
                default:
                    return null;
            }
        }
        return null;
    }
}
