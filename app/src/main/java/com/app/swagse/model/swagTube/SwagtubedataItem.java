package com.app.swagse.model.swagTube;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SwagtubedataItem implements Serializable {

    @SerializedName("likedata")
    private List<LikedataItem> likedata;

    @SerializedName("commentdata")
    private List<CommentdataItem> commentdata;

    @SerializedName("userlikestatus")
    private int userlikestatus;

    @SerializedName("title")
    private String title;

    @SerializedName("userid")
    private String userid;

    @SerializedName("likecount")
    private int likecount;

    @SerializedName("usercommentstatus")
    private int usercommentstatus;

    @SerializedName("userfollowstatus")
    private int userfollowstatus;

    @SerializedName("commentcount")
    private int commentcount;

    @SerializedName("views_count")
    private int viewscount;

    @SerializedName("timeago")
    private String timeago;

    @SerializedName("videourl")
    private String videourl;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    @SerializedName("category")
    private String category;

    @SerializedName("thmubnal")
    private String thmubnal;

    public List<LikedataItem> getLikedata() {
        return likedata;
    }

    public List<CommentdataItem> getCommentdata() {
        return commentdata;
    }

    public int getUserlikestatus() {
        return userlikestatus;
    }

    public String getTitle() {
        return title;
    }

    public String getUserid() {
        return userid;
    }

    public int getLikecount() {
        return likecount;
    }

    public int getUsercommentstatus() {
        return usercommentstatus;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public String getTimeago() {
        return timeago;
    }

    public String getVideourl() {
        return videourl;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getThmubnal() {
        return thmubnal;
    }

    public void setLikedata(List<LikedataItem> likedata) {
        this.likedata = likedata;
    }

    public void setCommentdata(List<CommentdataItem> commentdata) {
        this.commentdata = commentdata;
    }

    public void setUserlikestatus(int userlikestatus) {
        this.userlikestatus = userlikestatus;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public void setUsercommentstatus(int usercommentstatus) {
        this.usercommentstatus = usercommentstatus;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public void setTimeago(String timeago) {
        this.timeago = timeago;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setThmubnal(String thmubnal) {
        this.thmubnal = thmubnal;
    }

    public int getUserfollowstatus() {
        return userfollowstatus;
    }

    public void setUserfollowstatus(int userfollowstatus) {
        this.userfollowstatus = userfollowstatus;
    }

    public int getViewscount() {
        return viewscount;
    }

    public void setViewscount(int viewscount) {
        this.viewscount = viewscount;
    }
}