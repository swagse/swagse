package com.app.swagse.model;

import com.google.gson.annotations.SerializedName;

public class GetOTPResponse{

	@SerializedName("otp")
	private int otp;

	@SerializedName("mobileno")
	private String mobileno;

	@SerializedName("userstatus")
	private String userStatus;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public int getOtp(){
		return otp;
	}

	public String getMobileno(){
		return mobileno;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}

	public String getUserStatus() {
		return userStatus;
	}
}