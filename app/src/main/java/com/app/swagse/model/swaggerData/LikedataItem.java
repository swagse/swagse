package com.app.swagse.model.swaggerData;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LikedataItem implements Serializable {

	@SerializedName("like_id")
	private String likeId;

	@SerializedName("like_timeago")
	private String likeTimeago;

	@SerializedName("like_pic")
	private String likePic;

	@SerializedName("like_name")
	private String likeName;

	public void setLikeId(String likeId){
		this.likeId = likeId;
	}

	public String getLikeId(){
		return likeId;
	}

	public void setLikeTimeago(String likeTimeago){
		this.likeTimeago = likeTimeago;
	}

	public String getLikeTimeago(){
		return likeTimeago;
	}

	public void setLikePic(String likePic){
		this.likePic = likePic;
	}

	public String getLikePic(){
		return likePic;
	}

	public void setLikeName(String likeName){
		this.likeName = likeName;
	}

	public String getLikeName(){
		return likeName;
	}

	@Override
 	public String toString(){
		return 
			"LikedataItem{" + 
			"like_id = '" + likeId + '\'' + 
			",like_timeago = '" + likeTimeago + '\'' + 
			",like_pic = '" + likePic + '\'' + 
			",like_name = '" + likeName + '\'' + 
			"}";
		}
}