package com.app.swagse.model.subscription;

import com.google.gson.annotations.SerializedName;

public class SubscriptionlistItem{

	@SerializedName("channel_name")
	private String channelName;

	@SerializedName("date")
	private String date;

	@SerializedName("country")
	private String country;

	@SerializedName("img")
	private String img;

	@SerializedName("gender")
	private String gender;

	@SerializedName("city")
	private String city;

	@SerializedName("loginType")
	private String loginType;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("tag_line")
	private String tagLine;

	@SerializedName("number")
	private String number;

	@SerializedName("dob")
	private String dob;

	@SerializedName("id")
	private String id;

	@SerializedName("state")
	private String state;

	@SerializedName("category")
	private String category;

	@SerializedName("email")
	private String email;

	public void setChannelName(String channelName){
		this.channelName = channelName;
	}

	public String getChannelName(){
		return channelName;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setImg(String img){
		this.img = img;
	}

	public String getImg(){
		return img;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setLoginType(String loginType){
		this.loginType = loginType;
	}

	public String getLoginType(){
		return loginType;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setTagLine(String tagLine){
		this.tagLine = tagLine;
	}

	public String getTagLine(){
		return tagLine;
	}

	public void setNumber(String number){
		this.number = number;
	}

	public String getNumber(){
		return number;
	}

	public void setDob(String dob){
		this.dob = dob;
	}

	public String getDob(){
		return dob;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	@Override
 	public String toString(){
		return 
			"SubscriptionlistItem{" + 
			"channel_name = '" + channelName + '\'' + 
			",date = '" + date + '\'' + 
			",country = '" + country + '\'' + 
			",img = '" + img + '\'' + 
			",gender = '" + gender + '\'' + 
			",city = '" + city + '\'' + 
			",loginType = '" + loginType + '\'' + 
			",user_name = '" + userName + '\'' + 
			",tag_line = '" + tagLine + '\'' + 
			",number = '" + number + '\'' + 
			",dob = '" + dob + '\'' + 
			",id = '" + id + '\'' + 
			",state = '" + state + '\'' + 
			",category = '" + category + '\'' + 
			",email = '" + email + '\'' + 
			"}";
		}
}