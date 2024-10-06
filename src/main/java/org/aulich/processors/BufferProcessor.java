package org.aulich.processors;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.*;
import org.aulich.utilities.AppDataBaseConnectionPool;
import org.aulich.utilities.DataMapperUtil;
import org.aulich.utilities.MessageParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;


/**
 * The Bufferprocessor ist reading the filesystem and performing
 * buffered WiFiMessages. If message is valid, it moves it from the
 * monitored directory <code>bufferpath</code>to the directory of performed
 * messages <code>performedpath</code> and transfers all values in the
 * weatherstation-database.
 */
public class BufferProcessor implements Runnable {
    private static final Logger LOG = LogManager.getLogger(BufferProcessor.class);
    private static final int waitTime = Configuration.getConfiguration().getConfigurationModel().getBufferPerformWaitTime();

    @Override
    public void run() {
        LOG.debug("Thread starts to run.");
        while (true) {
            try {
                doWork();
                LOG.debug("One cycle performed, now waiting for " + waitTime + " millis.");

            } catch (SQLException | NullPointerException | IOException e) {
                LOG.error("Error while performing one cycle.", e);
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                LOG.error("Error while thread sleeping.", e);
            }
        }
    }

    private void doWork() throws SQLException, IOException {
        // Check configured paths
        File srcDir = new File(Configuration.getConfiguration().getConfigurationModel().getBufferPath());
        File dstDir = new File(Configuration.getConfiguration().getConfigurationModel().getPerformedPath());
        File errDir = new File(Configuration.getConfiguration().getConfigurationModel().getErrorPath());
        if (!srcDir.exists() || !srcDir.isDirectory() || !dstDir.exists() || !dstDir.isDirectory() || !errDir.exists() || !errDir.isDirectory()) {
            LOG.error("Configuration: <bufferpath>, <performedpath> or <errorpath> does not exist!");
            return;
        }
        // Connect to database
        LOG.debug("Getting database-connection.");
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        pool.connectToDataBase();
        Connection con = AppDataBaseConnectionPool.getConnection();
        boolean anyError = false;
        // Loop around buffered messages
        for (File msgFile : srcDir.listFiles()) {
            if (msgFile.isFile()) {
                LOG.debug("Messagefile to process: " + msgFile.getName());
                WiFiMessageModel msg = new WiFiMessageModel();
                anyError = false;
                // Parse message
                try {
                    msg = getMessage(msgFile, WiFiMessageModel.class);
                } catch (ParserConfigurationException | SAXException e) {
                    LOG.debug("Could not parse file " + msgFile.getName(), e);
                    anyError = true;
                }
                // Process message
                if (!anyError) {
                    LOG.debug("Processing message " + msgFile.getName());
                    if (!msg.getMessageType().equals(WiFiMessage.MESSAGETYPE_ECOWITT)) {
                        LOG.error("Unsupported MessageType in " + msgFile.getName());
                        anyError = true;
                    } else {
                        // Check, if station is valid
                        PreparedStatement stm = con.prepareStatement("SELECT COUNT(*) FROM Stations WHERE StationId = ?");
                        String stationId = MessageParser.getValueString(msg, "PASSKEY");
                        stm.setString(1, stationId);
                        ResultSet rst = stm.executeQuery();
                        if (!rst.next() || rst.getInt(1) == 0) {
                            LOG.error("Unregistered Station-ID:" + stationId);
                            anyError = true;
                        } else {
                            // If here, register all values of the message in database
                            anyError = !performMeasurements(con, msg);
                        }
                        rst.close();
                        stm.close();
                    }
                }
                // Move performed message
                if (anyError) {
                    FileUtils.copyFile(msgFile, new File(errDir.getAbsolutePath(), msgFile.getName()));
                } else {
                    FileUtils.copyFile(msgFile, new File(dstDir.getAbsolutePath(), msgFile.getName()));
                }
                if (!msgFile.delete()) {
                    throw new IOException("Could not delete file " + msgFile.getAbsolutePath());
                }
            }
        }
        con.close();
    }

    private WiFiMessageModel getMessage(File file, Class<?> clazz) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.parse(file);
        // Now try to parse ...
        XStream xStream = new XStream();
        xStream.ignoreUnknownElements();
        xStream.addPermission(NoTypePermission.NONE);
        xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xStream.allowTypeHierarchy(Collection.class);
        xStream.allowTypesByWildcard(new String[]{"org.aulich.model.**"});
        xStream.processAnnotations(clazz);
        return (WiFiMessageModel) xStream.fromXML(file);
    }

    private boolean performMeasurements(Connection con,
                                         WiFiMessageModel msg) throws SQLException {
        DataMapperUtil util = new DataMapperUtil();
        // At first get Key-Values for the records
        String stationId = MessageParser.getValueString(msg, "PASSKEY");
        Date measureDate = new Date();
        try {
            measureDate = MessageParser.getValueDate(msg, "dateutc");
        } catch (ParseException e) {
            LOG.error("Invalid date (a)", e);
            return false;
        }
        if (measureDate == null) {
            LOG.error("Invalid date (b)");
            return false;
        }
        Timestamp measureDateTS = DataMapperUtil.getSqlTimestamp(measureDate);
        // Remove possibly existing records
        PreparedStatement delStm1 = con.prepareStatement("DELETE FROM " +
                "MeasurementString WHERE StationId = ? AND MeasureDate=?");
        delStm1.setString(1, stationId);
        delStm1.setTimestamp(2, measureDateTS);
        delStm1.execute();
        delStm1.close();
        PreparedStatement delStm2 = con.prepareStatement("DELETE FROM " +
                "MeasurementDate WHERE StationId = ? AND MeasureDate=?");
        delStm2.setString(1, stationId);
        delStm2.setTimestamp(2, measureDateTS);
        delStm2.execute();
        delStm2.close();
        PreparedStatement delStm3 = con.prepareStatement("DELETE FROM " +
                "MeasurementDecimal WHERE StationId = ? AND MeasureDate=?");
        delStm3.setString(1, stationId);
        delStm3.setTimestamp(2, measureDateTS);
        delStm3.execute();
        delStm3.close();
        PreparedStatement delStm4 = con.prepareStatement("DELETE FROM " +
                "Measurement WHERE StationId = ? AND MeasureDate=?");
        delStm4.setString(1, stationId);
        delStm4.setTimestamp(2, measureDateTS);
        delStm4.execute();
        delStm4.close();
        // Create entry for this measurement
        PreparedStatement insStm0 = con.prepareStatement("INSERT INTO  " +
                "Measurement(StationId, MeasureDate, MeasureDateLocal) " +
                "VALUES(?, ?, ?)");
        insStm0.setString(1, stationId);
        insStm0.setTimestamp(2, measureDateTS);
        insStm0.setTimestamp(3, measureDateTS);
        insStm0.executeUpdate();
        insStm0.close();
        // Looparound all message-entries
        for (MessageEntryModel model : msg.getMessageEntryModels()) {
            DataMapperModel dataModel = DataMapperUtil.getDataMapperByMSGField(model.getKey());
            if (dataModel != null) {
                switch (dataModel.getDataType()) {
                    case DataMapperUtil.TYPE_STRING:
                        PreparedStatement insStm1 = con.prepareStatement("INSERT INTO  MeasurementString(StationId,  " + "MeasureDate, MeasureType,  Value) " + "VALUES(?, ?, ?, ?)");
                        insStm1.setString(1, stationId);
                        insStm1.setTimestamp(2, measureDateTS);
                        insStm1.setString(3, dataModel.getDataBaseField());
                        insStm1.setString(4, model.getValue());
                        insStm1.executeUpdate();
                        insStm1.close();
                        break;
                    case DataMapperUtil.TYPE_BIGDECIMAL:
                        PreparedStatement insStm2 = con.prepareStatement("INSERT INTO MeasurementDecimal(StationId,  " + "MeasureDate,  MeasureType, Value) " + "VALUES(?, ?, ?, ?)");
                        insStm2.setString(1, stationId);
                        insStm2.setTimestamp(2, measureDateTS);
                        insStm2.setString(3, dataModel.getDataBaseField());
                        insStm2.setBigDecimal(4, MessageParser.getValueBigDecimal(msg, model.getKey()));
                        insStm2.executeUpdate();
                        // Check, if there is a derived measurementtype declared
                        MeasurementTypeModel typeModel = util.getDerivedType(dataModel.getDataBaseField());
                        if (typeModel != null) {
                            insStm2.setString(3, typeModel.getMeasurementId());
                            insStm2.setBigDecimal(4, util.getDerivedValue(typeModel.getDerivingMethod(), MessageParser.getValueBigDecimal(msg, model.getKey())));
                            insStm2.executeUpdate();
                        }
                        insStm2.close();
                        break;
                    case DataMapperUtil.TYPE_DATE:
                        PreparedStatement insStm3 = con.prepareStatement("INSERT INTO MeasurementDate(StationId, " + "MeasureDate, MeasureType,  Value) " + "VALUES(?, ?, ?, ?)");
                        insStm3.setString(1, stationId);
                        insStm3.setTimestamp(2, measureDateTS);
                        insStm3.setString(3, dataModel.getDataBaseField());
                        insStm3.setTimestamp(4, measureDateTS);
                        insStm3.executeUpdate();
                        break;
                }
            }
        }
        return true;
    }
}
