package com.younho.hazelcast;

import java.io.Serializable;
import java.util.Date;

public class DCOLDataHist implements Serializable {
    private Long id;
    private String eqpId;
    private String workId;
    private String controlJobId;
    private String processJobId;
    private Date dcolDate;
    private String dcolName;
    private Long dcolOrder;
    private String dcolValue;

    public DCOLDataHist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getControlJobId() {
        return controlJobId;
    }

    public void setControlJobId(String controlJobId) {
        this.controlJobId = controlJobId;
    }

    public String getProcessJobId() {
        return processJobId;
    }

    public void setProcessJobId(String processJobId) {
        this.processJobId = processJobId;
    }

    public Date getDcolDate() {
        return dcolDate;
    }

    public void setDcolDate(Date dcolDate) {
        this.dcolDate = dcolDate;
    }

    public String getDcolName() {
        return dcolName;
    }

    public void setDcolName(String dcolName) {
        this.dcolName = dcolName;
    }

    public Long getDcolOrder() {
        return dcolOrder;
    }

    public void setDcolOrder(Long dcolOrder) {
        this.dcolOrder = dcolOrder;
    }

    public String getDcolValue() {
        return dcolValue;
    }

    public void setDcolValue(String dcolValue) {
        this.dcolValue = dcolValue;
    }
}
