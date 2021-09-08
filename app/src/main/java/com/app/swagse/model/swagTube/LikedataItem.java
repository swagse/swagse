package com.app.swagse.model.swagTube;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LikedataItem  implements Serializable {

	@SerializedName("like_id")
	private String likeId;

	@SerializedName("like_timeago")
	private String likeTimeago;

	@SerializedName("like_pic")
	private String likePic;

	@SerializedName("like_name")
	private String likeName;

	public String getLikeId(){
		return likeId;
	}

	public String getLikeTimeago(){
		return likeTimeago;
	}

	public String getLikePic(){
		return likePic;
	}

	public String getLikeName(){
		return likeName;
	}
}