package com.tranxit.enterprise.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 07-06-2018.
 */
public class EstimateFare implements Serializable {
    @SerializedName("estimated_fare")
    @Expose
    private Double estimatedFare;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("surge")
    @Expose
    private Integer surge;
    @SerializedName("surge_value")
    @Expose
    private String surgeValue;
    @SerializedName("tax_price")
    @Expose
    private Double taxPrice;
    @SerializedName("base_price")
    @Expose
    private Double basePrice;
    @SerializedName("wallet_balance")
    @Expose
    private Double walletBalance;


    @SerializedName("city_limits")
    @Expose
    private Integer cityLimits;
    @SerializedName("limit_message")
    @Expose
    private String limitMessage;
    @SerializedName("time_package")
    @Expose
    private List<TimePackage> timePackage = new ArrayList<>();

    @SerializedName("non_geo_price")
    @Expose
    private Double non_geo_price;

    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getSurge() {
        return surge;
    }

    public void setSurge(Integer surge) {
        this.surge = surge;
    }

    public String getSurgeValue() {
        return surgeValue;
    }

    public void setSurgeValue(String surgeValue) {
        this.surgeValue = surgeValue;
    }

    public Double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(Double taxPrice) {
        this.taxPrice = taxPrice;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }


    public Integer getCityLimits() {
        return cityLimits;
    }

    public void setCityLimits(Integer cityLimits) {
        this.cityLimits = cityLimits;
    }

    public String getLimitMessage() {
        return limitMessage;
    }

    public void setLimitMessage(String limitMessage) {
        this.limitMessage = limitMessage;
    }


    public Double getNon_geo_price() {
        return non_geo_price;
    }

    public void setNon_geo_price(Double non_geo_price) {
        this.non_geo_price = non_geo_price;
    }

    public List<TimePackage> getTimePackage() {
        return timePackage;
    }

    public void setTimePackage(List<TimePackage> timePackage) {
        this.timePackage = timePackage;
    }
}
