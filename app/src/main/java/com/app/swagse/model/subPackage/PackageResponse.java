package com.app.swagse.model.subPackage;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PackageResponse{

	@SerializedName("subscription")
	private List<SubscriptionItem> subscription;

	@SerializedName("status")
	private String status;

	public void setSubscription(List<SubscriptionItem> subscription){
		this.subscription = subscription;
	}

	public List<SubscriptionItem> getSubscription(){
		return subscription;
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
			"PackageResponse{" + 
			"subscription = '" + subscription + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}