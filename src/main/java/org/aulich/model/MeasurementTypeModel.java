package org.aulich.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.math.BigDecimal;

/**
 * Declares a measurementtype, supported by application. Also declares types,
 * that are calculated and not originaly sent by weatherstations.
 *
 * @author Thomas Aulich
 */
@XStreamAlias("measurementtype")
public class MeasurementTypeModel {
    @XStreamAlias("measurementid")
    @XStreamAsAttribute
    private String measurementId = "";
    @XStreamAlias("measurementname")
    @XStreamAsAttribute
    private String measurementName = "";
    @XStreamAlias("derivedfrom")
    @XStreamAsAttribute
    private String derivedFrom = "";

    @XStreamAlias("derivingmethod")
    @XStreamAsAttribute
    private String derivingMethod = "";

    /**
     * Constructor for mor convenience when building instances.
     *
     * @param measurementId
     * @param measurementName
     * @param derivedFrom
     * @param derivingMethod
     */
    public MeasurementTypeModel(String measurementId, String measurementName,
                                String derivedFrom, String derivingMethod) {
        this.measurementId = measurementId;
        this.measurementName = measurementName;
        this.derivedFrom = derivedFrom;
        this.derivingMethod = derivingMethod;
    }

    public String getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(String measurementId) {
        this.measurementId = measurementId;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public String getDerivingMethod() {
        return derivingMethod;
    }

    public void setDerivingMethod(String derivingMethod) {
        this.derivingMethod = derivingMethod;
    }
}
