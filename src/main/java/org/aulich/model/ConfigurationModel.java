package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("configuration")
public class ConfigurationModel {
    @XStreamAlias("datasource")
    private DataSourceModel dataSourceModel = new DataSourceModel();
    @XStreamAlias("bufferpath")
    private String bufferPath = "";

    @XStreamAlias("performedpath")
    private String performedPath = "";
    @XStreamAlias("errorpath")
    private String errorPath = "";

    @XStreamAlias("bufferperformwaittime")
    private int bufferPerformWaitTime = 60000;

    @XStreamAlias("datamappers")
    private List<DataMapperModel> DataMapperModels = new ArrayList<DataMapperModel>();
    @XStreamAlias("measurementtypes")
    private List<MeasurementTypeModel> MeasurementTypes = new ArrayList<MeasurementTypeModel>();

    public DataSourceModel getDataSourceModel() {
        return dataSourceModel;
    }

    public void setDataSourceModel(DataSourceModel dataSourceModel) {
        this.dataSourceModel = dataSourceModel;
    }

    public String getBufferPath() {
        return bufferPath;
    }

    public void setBufferPath(String bufferPath) {
        this.bufferPath = bufferPath;
    }

    public String getPerformedPath() {
        return performedPath;
    }

    public void setPerformedPath(String performedPath) {
        this.performedPath = performedPath;
    }

    public int getBufferPerformWaitTime() {
        return bufferPerformWaitTime;
    }

    public void setBufferPerformWaitTime(int bufferPerformWaitTime) {
        this.bufferPerformWaitTime = bufferPerformWaitTime;
    }

    public String getErrorPath() {
        return errorPath;
    }

    public void setErrorPath(String errorPath) {
        this.errorPath = errorPath;
    }

    public List<DataMapperModel> getDataMapperModels() {
        return DataMapperModels;
    }

    public void setDataMapperModels(List<DataMapperModel> dataMapperModels) {
        DataMapperModels = dataMapperModels;
    }

    public List<MeasurementTypeModel> getMeasurementTypes() {
        return MeasurementTypes;
    }

    public void setMeasurementTypes(List<MeasurementTypeModel> measurementTypes) {
        MeasurementTypes = measurementTypes;
    }
}
