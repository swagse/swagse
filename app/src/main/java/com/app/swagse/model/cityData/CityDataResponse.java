package com.app.swagse.model.cityData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CityDataResponse{

	@SerializedName("citydata")
	private List<CitydataItem> citydata;

	@SerializedName("status")
	private String status;

	public void setCitydata(List<CitydataItem> citydata){
		this.citydata = citydata;
	}

	public List<CitydataItem> getCitydata(){
		return citydata;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CityDataResponse{" + 
			"citydata = '" + citydata + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}