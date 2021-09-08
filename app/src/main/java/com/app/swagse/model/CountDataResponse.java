package com.app.swagse.model;

import com.google.gson.annotations.SerializedName;

public class CountDataResponse{

	@SerializedName("countdata")
	private Countdata countdata;

	@SerializedName("status")
	private String status;

	public void setCountdata(Countdata countdata){
		this.countdata = countdata;
	}

	public Countdata getCountdata(){
		return countdata;
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
			"CountDataResponse{" + 
			"countdata = '" + countdata + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}