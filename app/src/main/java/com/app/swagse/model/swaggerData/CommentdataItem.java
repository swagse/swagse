package com.app.swagse.model.swaggerData;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentdataItem implements Serializable {

	@SerializedName("comment_name")
	private String commentName;

	@SerializedName("comment_timeago")
	private String commentTimeago;

	@SerializedName("comment_user_id")
	private String commentUserId;

	@SerializedName("comment")
	private String comment;

	@SerializedName("comment_id")
	private String commentId;

	@SerializedName("comment_pic")
	private String commentPic;

	public CommentdataItem(String commentName, String commentTimeago, String commentUserId, String comment, String commentId, String commentPic) {
		this.commentName = commentName;
		this.commentTimeago = commentTimeago;
		this.commentUserId = commentUserId;
		this.comment = comment;
		this.commentId = commentId;
		this.commentPic = commentPic;
	}

	public void setCommentName(String commentName){
		this.commentName = commentName;
	}

	public String getCommentName(){
		return commentName;
	}

	public void setCommentTimeago(String commentTimeago){
		this.commentTimeago = commentTimeago;
	}

	public String getCommentTimeago(){
		return commentTimeago;
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

	public void setCommentPic(String commentPic){
		this.commentPic = commentPic;
	}

	public String getCommentPic(){
		return commentPic;
	}

	@Override
 	public String toString(){
		return 
			"CommentdataItem{" + 
			"comment_name = '" + commentName + '\'' + 
			",comment_timeago = '" + commentTimeago + '\'' + 
			",comment_user_id = '" + commentUserId + '\'' + 
			",comment = '" + comment + '\'' + 
			",comment_id = '" + commentId + '\'' + 
			",comment_pic = '" + commentPic + '\'' + 
			"}";
		}
}