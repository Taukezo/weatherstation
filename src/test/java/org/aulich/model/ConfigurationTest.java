package org.aulich.model;

import org.aulich.utilities.DataMapperUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigurationTest {
    @Test
    public void test() {
        Configuration.deleteConfigurationFile();
        Configuration cfg = Configuration.getConfiguration();
        ConfigurationModel cfgM = cfg.getConfigurationModel();
        cfgM.setMapsApiKey("1234");
        cfgM.setHostAddress("http://localhost:8080/weatherstation");
        cfgM.setBufferPath("C:\\temp\\BufferedData");
        cfgM.setBufferPerformWaitTime(30000);
        cfgM.setPerformedPath("C:\\temp\\PerformedData");
        cfgM.setErrorPath("C:\\temp\\ErrorData");
        DataSourceModel dataSourceModel = new DataSourceModel();
        dataSourceModel.setUrl("jdbc:sqlserver://localhost;database=weatherstation;encrypt=false;");
        dataSourceModel.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceModel.setUserName("TECUSER");
        dataSourceModel.setPassword("PWD4711");
        cfgM.setDataSourceModel(dataSourceModel);
        cfgM.getDataMapperModels().add(new DataMapperModel("PASSKEY",
                DataMapperUtil.STATIONID, DataMapperUtil.TYPE_STRING));
        cfgM.getDataMapperModels().add(new DataMapperModel("tempf",
                DataMapperUtil.MEASURE_TEMPOUTF,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("tempinf",
                DataMapperUtil.MEASURE_TEMPINF,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("windspeedmph",
                DataMapperUtil.MEASURE_WINDSPEEDMPH,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("winddir",
                DataMapperUtil.MEASURE_WINDDIR,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("hourlyrainin",
                DataMapperUtil.MEASURE_HOURLYRAININCH,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("humidityin",
                DataMapperUtil.MEASURE_HUMIDITYIN,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("humidity",
                DataMapperUtil.MEASURE_HUMIDITYOUT,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("baromabsin",
                DataMapperUtil.MEASURE_AIRPRESSUREINHG,
                DataMapperUtil.TYPE_BIGDECIMAL));
        cfgM.getDataMapperModels().add(new DataMapperModel("dateutc",
                DataMapperUtil.MEASUREDATE, DataMapperUtil.TYPE_DATE));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_TEMPOUTF,
                "Außentemperatur [F]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_TEMPOUTC,
                "Außentemperatur [C]", DataMapperUtil.MEASURE_TEMPOUTF, DataMapperUtil.DERIVE_FAHRENHEITINCELSIUS));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_TEMPINF,
                "Innentemperatur [F]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_TEMPINC,
                "Innentemperatur [C]", DataMapperUtil.MEASURE_TEMPINF,
                DataMapperUtil.DERIVE_FAHRENHEITINCELSIUS));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_WINDSPEEDMPH,
                "Windgeschwindigkeit [Miles/h]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_WINDSPEEDKMH,
                "Windgeschwindigkeit [Km/h]", DataMapperUtil.MEASURE_WINDSPEEDMPH,
                DataMapperUtil.DERIVE_MILESINKM));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_WINDDIR,
                "Windrichtung", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_HOURLYRAININCH,
                "Regen pro Stunde [inch]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_HOURLYRAINMM,
                "Regen pro Stunde [inch]",
                DataMapperUtil.MEASURE_HOURLYRAININCH, DataMapperUtil.DERIVE_INCHINMM));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_HUMIDITYOUT,
                "Luftfeuchte außen [%]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_HUMIDITYIN,
                "Luftfeuchte innen [%]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_AIRPRESSUREINHG,
                "Luftdruck [Hg]", "", ""));
        cfgM.getMeasurementTypes().add(new MeasurementTypeModel(DataMapperUtil.MEASURE_AIRPRESSUREINHPA,
                "Luftdruck [hpa]", DataMapperUtil.MEASURE_AIRPRESSUREINHG,
                DataMapperUtil.DERIVE_HGINHPA));
        if (cfg.save("resources")) {
            assertTrue(true);
        } else {
            fail();
        }
    }
}
