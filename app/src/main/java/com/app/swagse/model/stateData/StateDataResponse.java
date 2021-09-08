package com.app.swagse.model.stateData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StateDataResponse{

	@SerializedName("statedata")
	private List<StatedataItem> statedata;

	@SerializedName("status")
	private String status;

	public void setStatedata(List<StatedataItem> statedata){
		this.statedata = statedata;
	}

	public List<StatedataItem> getStatedata(){
		return statedata;
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
			"StateDataResponse{" + 
			"statedata = '" + statedata + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}