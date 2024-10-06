package org.aulich.model;

import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class WiFiMessage {
    public final static String MESSAGETYPE_ECOWITT = "ECOWITT";
    private static final String XML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final Logger LOG = LogManager.getLogger(WiFiMessage.class);
    private WiFiMessageModel wiFiMessageModel = new WiFiMessageModel();

    public WiFiMessageModel getWiFiMessageModel() {
        return wiFiMessageModel;
    }

    public void setWiFiMessageModel(WiFiMessageModel wiFiMessageModel) {
        this.wiFiMessageModel = wiFiMessageModel;
    }

    public boolean save(String fileName) {
        Configuration configuration = Configuration.getConfiguration();
        String fileNameConfirmed = configuration.getConfigurationModel().getBufferPath() + File.separator + fileName.replaceAll(":","-") + ".xml";
        File messageFile = new File(fileNameConfirmed);
        XStream xStream = new XStream();
        xStream.processAnnotations(WiFiMessageModel.class);
        OutputStream outputStream = null;
        Writer writer = null;
        try {
            outputStream = new FileOutputStream(messageFile);
            writer = new OutputStreamWriter(outputStream,
                    StandardCharsets.UTF_8);
            outputStream.write(XML_HEADER.getBytes(StandardCharsets.UTF_8));
            xStream.toXML(wiFiMessageModel, writer);
            writer.close();
            outputStream.close();
            LOG.debug("Message " + fileNameConfirmed + " saved.");
        } catch (Exception exp) {
            return false;
        }
        writer = null;
        outputStream = null;
        return true;
    }
}
