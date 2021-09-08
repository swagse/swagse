package com.app.swagse.model.swaggerCommentData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SwaggerCommentResponse{

	@SerializedName("swaggercommentdata")
	private List<SwaggercommentdataItem> swaggercommentdata;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setSwaggercommentdata(List<SwaggercommentdataItem> swaggercommentdata){
		this.swaggercommentdata = swaggercommentdata;
	}

	public List<SwaggercommentdataItem> getSwaggercommentdata(){
		return swaggercommentdata;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
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
			"SwaggerCommentResponse{" + 
			"swaggercommentdata = '" + swaggercommentdata + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}