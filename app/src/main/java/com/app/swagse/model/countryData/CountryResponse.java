package com.app.swagse.model.countryData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CountryResponse{

	@SerializedName("countrydata")
	private List<CountrydataItem> countrydata;

	@SerializedName("status")
	private String status;

	public void setCountrydata(List<CountrydataItem> countrydata){
		this.countrydata = countrydata;
	}

	public List<CountrydataItem> getCountrydata(){
		return countrydata;
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
			"CountryResponse{" + 
			"countrydata = '" + countrydata + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}