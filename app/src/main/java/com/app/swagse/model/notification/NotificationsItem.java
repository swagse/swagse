package com.app.swagse.model.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationsItem{

	@SerializedName("date")
	private String date;

	@SerializedName("user_pic")
	private String userPic;

	@SerializedName("action")
	private String action;

	@SerializedName("id")
	private String id;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setUserPic(String userPic){
		this.userPic = userPic;
	}

	public String getUserPic(){
		return userPic;
	}

	public void setAction(String action){
		this.action = action;
	}

	public String getAction(){
		return action;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
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
			"NotificationsItem{" + 
			"date = '" + date + '\'' + 
			",user_pic = '" + userPic + '\'' + 
			",action = '" + action + '\'' + 
			",id = '" + id + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}