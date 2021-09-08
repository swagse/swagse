package com.app.swagse.model.swagTube;

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

    public String getCommentName() {
        return commentName;
    }

    public String getCommentTimeago() {
        return commentTimeago;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentPic() {
        return commentPic;
    }
}