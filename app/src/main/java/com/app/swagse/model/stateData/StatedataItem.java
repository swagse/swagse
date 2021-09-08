package com.app.swagse.model.stateData;

import com.google.gson.annotations.SerializedName;

public class StatedataItem{

	@SerializedName("state_name")
	private String stateName;

	@SerializedName("state_iso")
	private String stateIso;

	@SerializedName("id")
	private String id;

	@SerializedName("country_iso")
	private String countryIso;

	public void setStateName(String stateName){
		this.stateName = stateName;
	}

	public String getStateName(){
		return stateName;
	}

	public void setStateIso(String stateIso){
		this.stateIso = stateIso;
	}

	public String getStateIso(){
		return stateIso;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCountryIso(String countryIso){
		this.countryIso = countryIso;
	}

	public String getCountryIso(){
		return countryIso;
	}

	@Override
 	public String toString(){
		return stateName;
		}
}