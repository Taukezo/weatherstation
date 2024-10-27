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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        LOG.debug("Querying for station=" + stationId + ", measureType=" +
                measureType + ", starttime=" + startTime + ", endtime=" + endTime);
        Date measureDateFrom = new Date();
        List<ColumnPojo> columns = new ArrayList<>();
        columns.add(new ColumnPojo("1", "Date", "", "date"));
        columns.add(new ColumnPojo("2", "Value", "", "number"));
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

    private Timestamp getTimeStampFromParameter(String dateParameter) {
        dateParameter = dateParameter.replace('T', ' ');
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM" +
                "-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateParameter,
                formatter);
        return Timestamp.valueOf(localDateTime);
    }
}
