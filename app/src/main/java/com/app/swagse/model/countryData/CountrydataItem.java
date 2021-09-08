package com.app.swagse.model.countryData;

import com.google.gson.annotations.SerializedName;

public class CountrydataItem {

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("id")
    private String id;

    @SerializedName("country_iso")
    private String countryIso;

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }

    public String getCountryIso() {
        return countryIso;
    }

    @Override
    public String toString() {
        return countryName;
    }
}