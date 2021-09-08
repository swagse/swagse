package com.app.swagse.model.swaggerData;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SwaggerResponse implements Serializable {

    @SerializedName("swaggerdata")
    private List<SwaggerdataItem> swaggerdata;

    @SerializedName("status")
    private String status;

    public void setSwaggerdata(List<SwaggerdataItem> swaggerdata) {
        this.swaggerdata = swaggerdata;
    }

    public List<SwaggerdataItem> getSwaggerdata() {
        return swaggerdata;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "SwaggerResponse{" +
                        "swaggerdata = '" + swaggerdata + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}