package org.aulich.weatherstation.rest.retrievedata;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.pojo.CellPojo;
import org.aulich.pojo.ColumnPojo;
import org.aulich.pojo.DataTablePojo;
import org.aulich.pojo.LinePojo;
import org.aulich.utilities.AppDataBaseConnectionPool;
import org.aulich.utilities.JsonTypeConverter;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/measurementdata")
public class MeasurementData {
    private static final Logger LOG = LogManager.getLogger(MeasurementData.class);

    @GET
    @Path("/series")
    @Produces(MediaType.APPLICATION_JSON)
    public DataTablePojo getSeries(@QueryParam("stationid") String stationId,
                                   @QueryParam("measuretype") String measureType,
                                   @QueryParam("starttime") String startTime,
                                   @QueryParam("endtime") String endTime
    ) {
        // Decide, if detailed or consolidated by day difference
        LocalDate d1 = LocalDate.parse(startTime.substring(0,10));
        LocalDate d2 = LocalDate.parse(endTime.substring(0, 10));
        if (ChronoUnit.DAYS.between(d1, d2) > 7) {
            return getValuesConsolidated(stationId, measureType, startTime, endTime);
        } else {
            return getValues(stationId, measureType, startTime, endTime);
        }
    }

    private DataTablePojo getValues(String stationId,
                                    String measureType,
                                    String startTime, String endTime) {
        LOG.debug("Querying for station=" + stationId + ", measureType=" +
                measureType + ", starttime=" + startTime + ", endtime=" + endTime);
        Date measureDateFrom = new Date();
        List<ColumnPojo> columns = new ArrayList<>();
        columns.add(new ColumnPojo("1", "Datum", "", "date"));
        columns.add(new ColumnPojo("2", measureType, "", "number"));
        List<LinePojo> lines = new ArrayList<>();
        // Get records from table Stations
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("SELECT [StationId]\n" +
                    "      ,[MeasureDate]\n" +
                    "      ,[MeasureType]\n" +
                    "      ,[Value]\n" +
                    "  FROM [weatherstation].[dbo].[MeasurementDecimal]\n" +
                    "  where StationId =? and MeasureType =?" +
                    " and MeasureDate >= ? And MeasureDate <= ? order by StationId, MeasureDate " +
                    "\n");
            selStm.setString(1, stationId);
            selStm.setString(2, measureType);
            selStm.setTimestamp(3, getTimeStampFromParameter(startTime));
            selStm.setTimestamp(4, getTimeStampFromParameter(endTime));
            ResultSet rs = selStm.executeQuery();
            // Process the result set
            while (rs.next()) {
                List<CellPojo> cells = new ArrayList<>();
                cells.add(new CellPojo(JsonTypeConverter.getDateTime(rs.getTimestamp(
                        "MeasureDate"))));
                cells.add(new CellPojo(rs.getBigDecimal("Value")));
                lines.add(new LinePojo(cells));
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new DataTablePojo(columns, lines);
    }

    private DataTablePojo getValuesConsolidated(String stationId,
                                                String measureType,
                                                String startTime, String endTime) {
        LOG.debug("Querying for station=" + stationId + ", measureType=" +
                measureType + ", starttime=" + startTime + ", endtime=" + endTime);
        Date measureDateFrom = new Date();
        List<ColumnPojo> columns = new ArrayList<>();
        columns.add(new ColumnPojo("1", "Datum", "", "date"));
        columns.add(new ColumnPojo("2", "Durchschnitt " + measureType, "", "number"));
        columns.add(new ColumnPojo("3", "Minimal " + measureType, "", "number"));
        columns.add(new ColumnPojo("4", "Maximal " + measureType, "", "number"));
        List<LinePojo> lines = new ArrayList<>();
        // Get records from table Stations
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("SELECT StationId,\n" +
                    "DateFromParts(year(MeasureDate), month(MeasureDate), day(MeasureDate)) as MeasureDate\n" +
                    ",Avg(Value) as AvgValue\n" +
                    "      ,Min(Value) as MinValue\n" +
                    "\t  ,Max(Value) as MaxValue\n" +
                    "  FROM MeasurementDecimal where StationId = " +
                    "? and MeasureType = ? and MeasureDate >= ? And MeasureDate <= ?\n" +
                    "  Group By StationId, year(MeasureDate), month(MeasureDate), day(MeasureDate)\n" +
                    "  Order by MeasureDate");
            selStm.setString(1, stationId);
            selStm.setString(2, measureType);
            selStm.setTimestamp(3, getTimeStampFromParameter(startTime));
            selStm.setTimestamp(4, getTimeStampFromParameter(endTime));
            ResultSet rs = selStm.executeQuery();
            // Process the result set
            while (rs.next()) {
                List<CellPojo> cells = new ArrayList<>();
                cells.add(new CellPojo(JsonTypeConverter.getDateTime(rs.getTimestamp(
                        "MeasureDate"))));
                cells.add(new CellPojo(rs.getBigDecimal("AvgValue")));
                cells.add(new CellPojo(rs.getBigDecimal("MinValue")));
                cells.add(new CellPojo(rs.getBigDecimal("MaxValue")));
                lines.add(new LinePojo(cells));
            }
            rs.close();
            selStm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new DataTablePojo(columns, lines);
    }

    private Timestamp getTimeStampFromParameter(String dateParameter) {
        dateParameter = dateParameter.replace('T', ' ');
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM" +
                "-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateParameter,
                formatter);
        return Timestamp.valueOf(localDateTime);
    }
}
