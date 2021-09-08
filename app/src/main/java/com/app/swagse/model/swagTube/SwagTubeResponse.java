package com.app.swagse.model.swagTube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SwagTubeResponse implements Serializable {

    @SerializedName("swagtubedata")
    private List<SwagtubedataItem> swagtubedata;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public List<SwagtubedataItem> getSwagtubedata() {
        return swagtubedata;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}