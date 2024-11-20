package org.aulich.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.Configuration;
import org.aulich.model.ConfigurationModel;
import org.aulich.model.MeasurementTypeModel;
import org.aulich.pojo.CellPojo;
import org.aulich.pojo.DataTablePojo;
import org.aulich.pojo.LinePojo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class delivers static values used in the applications jsp-pages.
 */
public class JspInjectorUtil {
    private static final Logger LOG =
            LogManager.getLogger(JspInjectorUtil.class);

    /**
     * Returns a Json-String with all configured Measurement-Types
     *
     * @return
     */
    public static String getMeasurementTypes() {
        ConfigurationModel cfgM =
                Configuration.getConfiguration().getConfigurationModel();
        List<MeasurementTypeModel> types = cfgM.getMeasurementTypes();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(types);
            System.out.println(jsonString);
        } catch (Exception e) {
            LOG.error("Exception while creating Json", e);
        }
        return jsonString;
    }

    /**
     * Method delivers the trend as x<0: falling, x=0: no trend, x>0:
     * rising</0:>
     *
     * @param stationId
     * @param measureType
     * @return
     */
    public static long getTrend(String stationId, String measureType) {
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        long result = 0;
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("SELECT StationId\n" +
                    "      ,DateFromParts(year(MeasureDate), month(MeasureDate), day(MeasureDate)) as MeasureDate\n" +
                    "      ,MeasureType\n" +
                    "      ,avg(Value) as Value\n" +
                    "  FROM MeasurementDecimal\n" +
                    "  where abs(datediff(day, getutcdate(),MeasureDate)) < " +
                    "3\n" +
                    "  and StationId = ?\n" +
                    "  and MeasureType = ?\n" +
                    "    group by stationId, year(MeasureDate), month(MeasureDate), day(MeasureDate), MeasureType\n" +
                    "\torder by MeasureDate desc");
            selStm.setString(1, stationId);
            selStm.setString(2, measureType);
            ResultSet rs = selStm.executeQuery();
            boolean first = true;
            BigDecimal ov = new BigDecimal("0");
            BigDecimal v = new BigDecimal("0");
            // Process the result set
            while (rs.next()) {
                if (first) {
                    ov = rs.getBigDecimal("Value");
                    first = false;
                }
                v = rs.getBigDecimal("Value");
                result += v.compareTo(ov);
                ov = v;
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result * -1;
    }

    /**
     * Gets the trend and delivers a corresponding image url.
     *
     * @param stationId
     * @param measureType
     * @return
     */
    public static String getTrendImageUrl(String stationId, String measureType) {
        long trend = getTrend(stationId, measureType);
        if (trend == 0) {
            return "images/up-and-down-arrows.png";
        } else {
            if (trend < 0) {
                return "images/decrease.png";
            } else {
                return
                        "images/increase.png";
            }
        }
    }

    /**
     * Method delivers the last value stored.
     *
     * @param stationId
     * @param measureType
     * @return
     */
    public static BigDecimal getActualValue(String stationId, String measureType) {
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        BigDecimal result = new BigDecimal("0");
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("Select StationId, MeasureDate, MeasureType, Value from MeasurementDecimal where MeasureDate = (\n" +
                    "SELECT max(MeasureDate)\n" +
                    "  FROM Measurement\n" +
                    "  where StationId =? and MeasureType = ?)");
            selStm.setString(1, stationId);
            selStm.setString(2, measureType);
            ResultSet rs = selStm.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("Value");
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Delivers the name of the station.
     *
     * @param stationId
     * @return
     */
    public static String getStationName(String stationId) {
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        String result = "";
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("SELECT StationName\n" +
                    "  FROM Stations WHERE StationId=?");
            selStm.setString(1, stationId);
            ResultSet rs = selStm.executeQuery();
            if (rs.next()) {
                result = rs.getString("StationName");
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Method delivers the last measurement date.
     *
     * @param stationId
     * @return
     */
    public static Date getLastMeasureDate(String stationId) {
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        Date result = new Date();
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement(
                    "SELECT max(MeasureDate) AS MeasureDate\n" +
                            "  FROM Measurement\n" +
                            "  where StationId =?");
            selStm.setString(1, stationId);
            ResultSet rs = selStm.executeQuery();
            if (rs.next()) {
                result = rs.getTimestamp("MeasureDate");
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static String getLastMeasureDateFormatted(String stationId) {
        Date date = getLastMeasureDate(stationId);
        String pattern = "EEE, d. MMM yyyy HH:mm";
        String result = new SimpleDateFormat(pattern).format(date) + " Uhr";
        return result;
    }
}
