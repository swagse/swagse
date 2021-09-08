package com.app.swagse.model.commentData;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CommentResponse{

	@SerializedName("commentdata")
	private List<CommentdataItem> commentdata;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public List<CommentdataItem> getCommentdata(){
		return commentdata;
	}

	public String getMessage(){
		return message;
	}

	public String getStatus(){
		return status;
	}
}