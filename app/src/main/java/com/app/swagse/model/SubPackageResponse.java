package com.app.swagse.model;

import com.google.gson.annotations.SerializedName;

public class SubPackageResponse{

	@SerializedName("video_size")
	private String videoSize;

	@SerializedName("video_length")
	private String videoLength;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setVideoSize(String videoSize){
		this.videoSize = videoSize;
	}

	public String getVideoSize(){
		return videoSize;
	}

	public void setVideoLength(String videoLength){
		this.videoLength = videoLength;
	}

	public String getVideoLength(){
		return videoLength;
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
			"SubPackageResponse{" + 
			"video_size = '" + videoSize + '\'' + 
			",video_length = '" + videoLength + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}