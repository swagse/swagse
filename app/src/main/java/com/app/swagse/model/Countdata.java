package com.app.swagse.model;

import com.google.gson.annotations.SerializedName;

public class Countdata{

	@SerializedName("liked_count")
	private int likedCount;

	@SerializedName("followers_count")
	private int followersCount;

	@SerializedName("watched_count")
	private int watchedCount;

	@SerializedName("later_count")
	private int laterCount;

	public void setLikedCount(int likedCount){
		this.likedCount = likedCount;
	}

	public int getLikedCount(){
		return likedCount;
	}

	public void setFollowersCount(int followersCount){
		this.followersCount = followersCount;
	}

	public int getFollowersCount(){
		return followersCount;
	}

	public void setWatchedCount(int watchedCount){
		this.watchedCount = watchedCount;
	}

	public int getWatchedCount(){
		return watchedCount;
	}

	public void setLaterCount(int laterCount){
		this.laterCount = laterCount;
	}

	public int getLaterCount(){
		return laterCount;
	}

	@Override
 	public String toString(){
		return 
			"Countdata{" + 
			"liked_count = '" + likedCount + '\'' + 
			",followers_count = '" + followersCount + '\'' + 
			",watched_count = '" + watchedCount + '\'' + 
			",later_count = '" + laterCount + '\'' + 
			"}";
		}
}