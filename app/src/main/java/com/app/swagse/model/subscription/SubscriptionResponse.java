package com.app.swagse.model.subscription;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SubscriptionResponse{

	@SerializedName("subscriptionlist")
	private List<SubscriptionlistItem> subscriptionlist;

	@SerializedName("status")
	private String status;

	public void setSubscriptionlist(List<SubscriptionlistItem> subscriptionlist){
		this.subscriptionlist = subscriptionlist;
	}

	public List<SubscriptionlistItem> getSubscriptionlist(){
		return subscriptionlist;
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
			"SubscriptionResponse{" + 
			"subscriptionlist = '" + subscriptionlist + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}