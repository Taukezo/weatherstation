package org.aulich.weatherstation.rest.retrievedata;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.pojo.*;
import org.aulich.utilities.AppDataBaseConnectionPool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/mapsdata")
public class MapsData {
    private static final Logger LOG = LogManager.getLogger(MapsData.class);

    @GET
    @Path("/stations")
    @Produces(MediaType.APPLICATION_JSON)
    public DataTablePojo getStations() {
        List<ColumnPojo> columns = new ArrayList<>();
        columns.add(new ColumnPojo("1", "Lat", "", "number"));
        columns.add(new ColumnPojo("2", "Long", "", "number"));
        columns.add(new ColumnPojo("3", "Name", "", "string"));
        columns.add(new ColumnPojo("4", "StationId", "", "string"));
        List<LinePojo> lines = new ArrayList<>();
        // Get records from table Stations
        AppDataBaseConnectionPool pool = AppDataBaseConnectionPool.getInstance();
        try {
            pool.connectToDataBase();
            Connection con = AppDataBaseConnectionPool.getConnection();
            PreparedStatement selStm = con.prepareStatement("SELECT " +
                    "StationId, StationName, Latitude, Longitude, " +
                    "TimeZone FROM Stations");
            ResultSet rs = selStm.executeQuery();
            // Process the result set
            while (rs.next()) {
                List<CellPojo> cells = new ArrayList<>();
                cells.add(new CellPojo(rs.getBigDecimal("Latitude")));
                cells.add(new CellPojo(rs.getBigDecimal("Longitude")));
                cells.add(new CellPojo(rs.getString("StationName")));
                cells.add(new CellPojo(rs.getString("StationId")));
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
}
