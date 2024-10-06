package org.aulich.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.Configuration;
import org.aulich.model.ConfigurationModel;
import org.aulich.model.DataMapperModel;
import org.aulich.model.MeasurementTypeModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;

/**
 * Helps mapping between measurementtypes and calculating of derived
 * measurements.
 *
 * @author Thomas Aulich
 */
public class DataMapperUtil {
    private static final Logger LOG =
            LogManager.getLogger(DataMapperUtil.class);
    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_BIGDECIMAL = "BIGDECIMAL";
    public static final String TYPE_DATE = "DATE";
    public static final String DERIVE_FAHRENHEITINCELSIUS = "DERIVE_FAHRENHEITINCELSIUS";
    public static final String DERIVE_MILESINKM = "DERIVE_MILESINKM";
    public static final String DERIVE_INCHINMM = "DERIVE_INCHINMM";
    public static final String DERIVE_HGINHPA = "DERIVE_HGINHPA";
    public static final String MEASURE_TEMPINF = "TempInF";
    public static final String MEASURE_TEMPINC = "TempInC";
    public static final String MEASURE_TEMPOUTF = "TempOutF";
    public static final String MEASURE_TEMPOUTC = "TempOutC";
    public static final String MEASURE_WINDSPEEDMPH = "WindSpeedMph";
    public static final String MEASURE_WINDSPEEDKMH = "WindSpeedKmh";
    public static final String MEASURE_WINDDIR = "WindDir";
    public static final String MEASURE_HOURLYRAININCH = "HourlyRainInch";
    public static final String MEASURE_HOURLYRAINMM = "HourlyRainMm";
    public static final String MEASURE_HUMIDITYIN = "HumidityIn";
    public static final String MEASURE_HUMIDITYOUT = "HumidityOut";
    public static final String MEASURE_AIRPRESSUREINHG = "AirPressureInHg";
    public static final String MEASURE_AIRPRESSUREINHPA = "AirPressureInHpa";
    public static final String MEASURE_DATELOCAL = "MeasureDateLocal";
    public static final String STATIONID = "StationId";
    public static final String MEASUREDATE = "MeasureDate";
    private HashMap<String, MeasurementTypeModel> deriverMap = new HashMap<String, MeasurementTypeModel>();

    /**
     * Constructor to get access to non-static methods.
     */
    public DataMapperUtil() {
        deriverMap = new HashMap<String, MeasurementTypeModel>();
        ConfigurationModel cfgM = Configuration.getConfiguration().getConfigurationModel();
        for (MeasurementTypeModel model : cfgM.getMeasurementTypes()) {
            if (model.getDerivedFrom() != null && !"".equals(model.getDerivedFrom()) && deriverMap.get(model.getDerivedFrom()) == null) {
                deriverMap.put(model.getDerivedFrom(), model);
            }
        }
    }

    /**
     * Searches for a derived measurementtype by a given measurementtype.
     *
     * @param typeId
     * @return
     */
    public MeasurementTypeModel getDerivedType(String typeId) {
        return deriverMap.get(typeId);
    }

    /**
     * Calculates a derived value from it's original value by using a
     * specific deriving method.
     *
     * @param deriveMethod
     * @param originalValue
     * @return
     */
    public BigDecimal getDerivedValue(String deriveMethod,
                                      BigDecimal originalValue) {
        if (originalValue == null) {
            LOG.warn("Null-Value not possible to convert!");
            return null;
        }
        switch (deriveMethod) {
            case DERIVE_FAHRENHEITINCELSIUS:
                return convertToCelsius(originalValue);
            case DERIVE_INCHINMM:
                return originalValue.multiply(BigDecimal.valueOf(25.4)).setScale(1, RoundingMode.HALF_UP);
            case DERIVE_MILESINKM:
                return originalValue.multiply(BigDecimal.valueOf(1.60934)).setScale(3, RoundingMode.HALF_UP);
            case DERIVE_HGINHPA:
                return originalValue.multiply(BigDecimal.valueOf(33.8639)).setScale(1, RoundingMode.HALF_UP);
        }
        return null;
    }

    private BigDecimal convertToCelsius(BigDecimal valueFahrenheit) {
        return valueFahrenheit.subtract(BigDecimal.valueOf(32))
                .multiply(BigDecimal.valueOf(5))
                .divide(BigDecimal.valueOf(9), RoundingMode.HALF_UP).setScale(1, RoundingMode.HALF_UP);
    }

    public static DataMapperModel getDataMapperByDBField(String value) {
        ConfigurationModel cfgM = Configuration.getConfiguration().getConfigurationModel();
        for (DataMapperModel model : cfgM.getDataMapperModels()) {
            if (model.getDataBaseField().equals(value)) {
                return model;
            }
        }
        return null;
    }

    public static DataMapperModel getDataMapperByMSGField(String value) {
        ConfigurationModel cfgM = Configuration.getConfiguration().getConfigurationModel();
        for (DataMapperModel model : cfgM.getDataMapperModels()) {
            if (model.getMessageField().equals(value)) {
                return model;
            }
        }
        return null;
    }

    public static java.sql.Date getSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp getSqlTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }
}
