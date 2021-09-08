package com.app.swagse.model.subPackage;

import com.google.gson.annotations.SerializedName;

public class SubscriptionItem{

	@SerializedName("end_date")
	private String endDate;

	@SerializedName("duration")
	private String duration;

	@SerializedName("date")
	private String date;

	@SerializedName("video_size")
	private String videoSize;

	@SerializedName("staus")
	private String staus;

	@SerializedName("price")
	private String price;

	@SerializedName("package_details")
	private String packageDetails;

	@SerializedName("video_length")
	private String videoLength;

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("color_code")
	private String colorCode;

	@SerializedName("start_date")
	private String startDate;

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setDuration(String duration){
		this.duration = duration;
	}

	public String getDuration(){
		return duration;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setVideoSize(String videoSize){
		this.videoSize = videoSize;
	}

	public String getVideoSize(){
		return videoSize;
	}

	public void setStaus(String staus){
		this.staus = staus;
	}

	public String getStaus(){
		return staus;
	}

	public void setPrice(String price){
		this.price = price;
	}

	public String getPrice(){
		return price;
	}

	public void setPackageDetails(String packageDetails){
		this.packageDetails = packageDetails;
	}

	public String getPackageDetails(){
		return packageDetails;
	}

	public void setVideoLength(String videoLength){
		this.videoLength = videoLength;
	}

	public String getVideoLength(){
		return videoLength;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setColorCode(String colorCode){
		this.colorCode = colorCode;
	}

	public String getColorCode(){
		return colorCode;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	@Override
 	public String toString(){
		return 
			"SubscriptionItem{" + 
			"end_date = '" + endDate + '\'' + 
			",duration = '" + duration + '\'' + 
			",date = '" + date + '\'' + 
			",video_size = '" + videoSize + '\'' + 
			",staus = '" + staus + '\'' + 
			",price = '" + price + '\'' + 
			",package_details = '" + packageDetails + '\'' + 
			",video_length = '" + videoLength + '\'' + 
			",id = '" + id + '\'' + 
			",title = '" + title + '\'' + 
			",color_code = '" + colorCode + '\'' + 
			",start_date = '" + startDate + '\'' + 
			"}";
		}
}