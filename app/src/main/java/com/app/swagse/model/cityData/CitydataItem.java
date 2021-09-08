package com.app.swagse.model.cityData;

import com.google.gson.annotations.SerializedName;

public class CitydataItem {

    @SerializedName("city")
    private String city;

    @SerializedName("id")
    private String id;

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return city;
    }
}