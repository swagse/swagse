package com.app.swagse.model.commentData;

import com.google.gson.annotations.SerializedName;

public class CommentdataItem{

	@SerializedName("timeago")
	private String timeago;

	@SerializedName("userpic")
	private String userpic;

	@SerializedName("comment_user_id")
	private String commentUserId;

	@SerializedName("comment")
	private String comment;

	@SerializedName("comment_id")
	private String commentId;

	@SerializedName("username")
	private String username;

	public String getTimeago(){
		return timeago;
	}

	public String getUserpic(){
		return userpic;
	}

	public String getCommentUserId(){
		return commentUserId;
	}

	public String getComment(){
		return comment;
	}

	public String getCommentId(){
		return commentId;
	}

	public String getUsername(){
		return username;
	}
}