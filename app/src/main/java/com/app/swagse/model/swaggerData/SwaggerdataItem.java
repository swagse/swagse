package com.app.swagse.model.swaggerData;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SwaggerdataItem implements Serializable {

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

	@SerializedName("commentcount")
	private int commentcount;

	@SerializedName("timeago")
	private String timeago;

	@SerializedName("videourl")
	private String videourl;

	@SerializedName("name")
	private String name;

	@SerializedName("userfollowstatus")
	private int userfollowstatus;

	@SerializedName("id")
	private String id;

	@SerializedName("views_count")
	private int viewsCount;

	@SerializedName("category")
	private String category;

	@SerializedName("thmubnal")
	private String thmubnal;

	public void setLikedata(List<LikedataItem> likedata){
		this.likedata = likedata;
	}

	public List<LikedataItem> getLikedata(){
		return likedata;
	}

	public void setCommentdata(List<CommentdataItem> commentdata){
		this.commentdata = commentdata;
	}

	public List<CommentdataItem> getCommentdata(){
		return commentdata;
	}

	public void setUserlikestatus(int userlikestatus){
		this.userlikestatus = userlikestatus;
	}

	public int getUserlikestatus(){
		return userlikestatus;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setUserid(String userid){
		this.userid = userid;
	}

	public String getUserid(){
		return userid;
	}

	public void setLikecount(int likecount){
		this.likecount = likecount;
	}

	public int getLikecount(){
		return likecount;
	}

	public void setUsercommentstatus(int usercommentstatus){
		this.usercommentstatus = usercommentstatus;
	}

	public int getUsercommentstatus(){
		return usercommentstatus;
	}

	public void setCommentcount(int commentcount){
		this.commentcount = commentcount;
	}

	public int getCommentcount(){
		return commentcount;
	}

	public void setTimeago(String timeago){
		this.timeago = timeago;
	}

	public String getTimeago(){
		return timeago;
	}

	public void setVideourl(String videourl){
		this.videourl = videourl;
	}

	public String getVideourl(){
		return videourl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUserfollowstatus(int userfollowstatus){
		this.userfollowstatus = userfollowstatus;
	}

	public int getUserfollowstatus(){
		return userfollowstatus;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setViewsCount(int viewsCount){
		this.viewsCount = viewsCount;
	}

	public int getViewsCount(){
		return viewsCount;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setThmubnal(String thmubnal){
		this.thmubnal = thmubnal;
	}

	public String getThmubnal(){
		return thmubnal;
	}

	@Override
 	public String toString(){
		return 
			"SwaggerdataItem{" + 
			"likedata = '" + likedata + '\'' + 
			",commentdata = '" + commentdata + '\'' + 
			",userlikestatus = '" + userlikestatus + '\'' + 
			",title = '" + title + '\'' + 
			",userid = '" + userid + '\'' + 
			",likecount = '" + likecount + '\'' + 
			",usercommentstatus = '" + usercommentstatus + '\'' + 
			",commentcount = '" + commentcount + '\'' + 
			",timeago = '" + timeago + '\'' + 
			",videourl = '" + videourl + '\'' + 
			",name = '" + name + '\'' + 
			",userfollowstatus = '" + userfollowstatus + '\'' + 
			",id = '" + id + '\'' + 
			",views_count = '" + viewsCount + '\'' + 
			",category = '" + category + '\'' + 
			",thmubnal = '" + thmubnal + '\'' + 
			"}";
		}
}