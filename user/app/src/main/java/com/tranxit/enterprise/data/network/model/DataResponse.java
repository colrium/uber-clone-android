package com.tranxit.enterprise.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 07-06-2018.
 */
public class DataResponse {

    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<>();

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }
}
