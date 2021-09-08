package com.app.swagse.model.userDetails;

import com.google.gson.annotations.SerializedName;

public class FollowersDataItem{

	@SerializedName("img")
	private String img;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("id")
	private String id;

	public void setImg(String img){
		this.img = img;
	}

	public String getImg(){
		return img;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"FollowersDataItem{" + 
			"img = '" + img + '\'' + 
			",user_name = '" + userName + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}