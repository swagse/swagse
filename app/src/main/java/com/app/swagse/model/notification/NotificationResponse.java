package com.app.swagse.model.notification;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse{

	@SerializedName("notifications")
	private List<NotificationsItem> notifications;

	@SerializedName("status")
	private String status;

	public void setNotifications(List<NotificationsItem> notifications){
		this.notifications = notifications;
	}

	public List<NotificationsItem> getNotifications(){
		return notifications;
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
			"NotificationResponse{" + 
			"notifications = '" + notifications + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}