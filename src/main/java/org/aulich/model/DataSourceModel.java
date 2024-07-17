package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("datasource")
public class DataSourceModel {
    @XStreamAlias("driverclassname")
    private String driverClassName ="";
    @XStreamAlias("url")
    private String url = "";
    @XStreamAlias("username")
    private String userName = "";
    @XStreamAlias("password")
    private String password = "";
    /**
     * @return the driverClassName
     */
    public String getDriverClassName() {
        return driverClassName;
    }
    /**
     * @param driverClassName the driverClassName to set
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
