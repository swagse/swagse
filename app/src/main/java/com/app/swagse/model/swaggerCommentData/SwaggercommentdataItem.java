package com.app.swagse.model.swaggerCommentData;

import com.google.gson.annotations.SerializedName;

public class SwaggercommentdataItem{

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

	public SwaggercommentdataItem(String timeago, String userpic, String commentUserId, String comment, String commentId, String username) {
		this.timeago = timeago;
		this.userpic = userpic;
		this.commentUserId = commentUserId;
		this.comment = comment;
		this.commentId = commentId;
		this.username = username;
	}

	public void setTimeago(String timeago){
		this.timeago = timeago;
	}

	public String getTimeago(){
		return timeago;
	}

	public void setUserpic(String userpic){
		this.userpic = userpic;
	}

	public String getUserpic(){
		return userpic;
	}

	public void setCommentUserId(String commentUserId){
		this.commentUserId = commentUserId;
	}

	public String getCommentUserId(){
		return commentUserId;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	public void setCommentId(String commentId){
		this.commentId = commentId;
	}

	public String getCommentId(){
		return commentId;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"SwaggercommentdataItem{" + 
			"timeago = '" + timeago + '\'' + 
			",userpic = '" + userpic + '\'' + 
			",comment_user_id = '" + commentUserId + '\'' + 
			",comment = '" + comment + '\'' + 
			",comment_id = '" + commentId + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}