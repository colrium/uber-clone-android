package com.tranxit.enterprise.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 13-06-2018.
 */
public class Payment {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("request_id")
    @Expose
    private Integer requestId;
    @SerializedName("promocode_id")
    @Expose
    private Object promocodeId;
    @SerializedName("payment_id")
    @Expose
    private Object paymentId;
    @SerializedName("payment_mode")
    @Expose
    private Object paymentMode;
    @SerializedName("fixed")
    @Expose
    private Double fixed;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("eta_discount")
    @Expose
    private Double etaDiscount;
    @SerializedName("commision")
    @Expose
    private Double commision;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("tax")
    @Expose
    private Double tax;
    @SerializedName("wallet")
    @Expose
    private Double wallet;
    @SerializedName("surge")
    @Expose
    private Double surge;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("payable")
    @Expose
    private Double payable;
    @SerializedName("waiting_fare")
    @Expose
    private Double waitingFare;
    @SerializedName("provider_commission")
    @Expose
    private Double providerCommission;
    @SerializedName("provider_pay")
    @Expose
    private Double providerPay;
    @SerializedName("minute")
    @Expose
    private Double minute;
    @SerializedName("night_fare")
    @Expose
    private Double nightFare;
    @SerializedName("non_geo_fencing_minute")
    @Expose
    private Integer nonGeoFencingMinute;
    @SerializedName("geo_fencing_minute")
    @Expose
    private Integer geoFencingMinute;
    @SerializedName("peak_price")
    @Expose
    private Double peakPrice;

    @SerializedName("none_geo_fencing_total")
    @Expose
    private Double noneGeoFencingTotal;
    @SerializedName("geo_fencing_total")
    @Expose
    private Double geoFencingTotal;


    @SerializedName("non_geo_price")
    @Expose
    private Double non_geo_price;


    @SerializedName("return_travel_fare")
    @Expose
    private Double return_travel_fare;

    @SerializedName("driver_beta")
    @Expose
    private double driverBeta;
    @SerializedName("rental_extra_hr_price")
    @Expose
    private double rentalExtraHrPrice;
    @SerializedName("rental_extra_km_price")
    @Expose
    private double rentalExtraKmPrice;
    @SerializedName("outstation_days")
    @Expose
    private String outstationDays;

    public double getRentalExtraHrPrice() {
        return rentalExtraHrPrice;
    }

    public void setRentalExtraHrPrice(double rentalExtraHrPrice) {
        this.rentalExtraHrPrice = rentalExtraHrPrice;
    }

    public double getRentalExtraKmPrice() {
        return rentalExtraKmPrice;
    }

    public void setRentalExtraKmPrice(double rentalExtraKmPrice) {
        this.rentalExtraKmPrice = rentalExtraKmPrice;
    }

    public String getOutstationDays() {
        return outstationDays;
    }

    public void setOutstationDays(String outstationDays) {
        this.outstationDays = outstationDays;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Object getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Object promocodeId) {
        this.promocodeId = promocodeId;
    }

    public Object getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Object paymentId) {
        this.paymentId = paymentId;
    }

    public Object getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Object paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getFixed() {
        return fixed;
    }

    public void setFixed(Double fixed) {
        this.fixed = fixed;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getEtaDiscount() {
        return etaDiscount;
    }

    public void setEtaDiscount(Double etaDiscount) {
        this.etaDiscount = etaDiscount;
    }

    public Double getCommision() {
        return commision;
    }

    public void setCommision(Double commision) {
        this.commision = commision;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public Double getSurge() {
        return surge;
    }

    public void setSurge(Double surge) {
        this.surge = surge;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }

    public Double getWaitingFare() {
        return waitingFare;
    }

    public void setWaitingFare(Double waitingFare) {
        this.waitingFare = waitingFare;
    }

    public Double getProviderCommission() {
        return providerCommission;
    }

    public void setProviderCommission(Double providerCommission) {
        this.providerCommission = providerCommission;
    }

    public Double getProviderPay() {
        return providerPay;
    }

    public void setProviderPay(Double providerPay) {
        this.providerPay = providerPay;
    }


    public Double getMinute() {
        return minute;
    }

    public void setMinute(Double minute) {
        this.minute = minute;
    }

    public Integer getNonGeoFencingMinute() {
        return nonGeoFencingMinute;
    }

    public void setNonGeoFencingMinute(Integer nonGeoFencingMinute) {
        this.nonGeoFencingMinute = nonGeoFencingMinute;
    }

    public Integer getGeoFencingMinute() {
        return geoFencingMinute;
    }

    public void setGeoFencingMinute(Integer geoFencingMinute) {
        this.geoFencingMinute = geoFencingMinute;
    }

    public Double getNoneGeoFencingTotal() {
        return noneGeoFencingTotal;
    }

    public void setNoneGeoFencingTotal(Double noneGeoFencingTotal) {
        this.noneGeoFencingTotal = noneGeoFencingTotal;
    }

    public Double getGeoFencingTotal() {
        return geoFencingTotal;
    }

    public void setGeoFencingTotal(Double geoFencingTotal) {
        this.geoFencingTotal = geoFencingTotal;
    }


    public Double getNon_geo_price() {
        return non_geo_price;
    }

    public void setNon_geo_price(Double non_geo_price) {
        this.non_geo_price = non_geo_price;
    }

    public Double getReturn_travel_fare() {
        return return_travel_fare;
    }

    public void setReturn_travel_fare(Double return_travel_fare) {
        this.return_travel_fare = return_travel_fare;
    }

    public Double getNightFare() {
        return nightFare;
    }

    public void setNightFare(Double nightFare) {
        this.nightFare = nightFare;
    }

    public Double getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(Double peakPrice) {
        this.peakPrice = peakPrice;
    }

    public double getDriverBeta() {
        return driverBeta;
    }

    public void setDriverBeta(double driverBeta) {
        this.driverBeta = driverBeta;
    }
}
