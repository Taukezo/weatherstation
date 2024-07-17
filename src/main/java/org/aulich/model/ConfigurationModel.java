package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("configuration")
public class ConfigurationModel {
    @XStreamAlias("datasource")
    private DataSourceModel dataSourceModel = new DataSourceModel();
    @XStreamAlias("bufferpath")
    private String bufferPath = "";

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
}
