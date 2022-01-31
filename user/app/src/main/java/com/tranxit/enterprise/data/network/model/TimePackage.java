package com.tranxit.enterprise.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TimePackage implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("time_id")
    @Expose
    private Integer timeId;
    @SerializedName("service_id")
    @Expose
    private Integer serviceId;
    @SerializedName("peak_price")
    @Expose
    private Double peakPrice;
    @SerializedName("times")
    @Expose
    private Times times;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimeId() {
        return timeId;
    }

    public void setTimeId(Integer timeId) {
        this.timeId = timeId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Double getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(Double peakPrice) {
        this.peakPrice = peakPrice;
    }

    public Times getTimes() {
        return times;
    }

    public void setTimes(Times times) {
        this.times = times;
    }
}
