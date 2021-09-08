package com.app.swagse.model.userDetails;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UserDetailResponse {

    @SerializedName("userdata")
    private Userdata userdata;

    @SerializedName("followers_data")
    private List<FollowersDataItem> followersData;

    @SerializedName("video_size")
    private int videoSize;

    @SerializedName("followers_count")
    private int followersCount;

    @SerializedName("video_length")
    private int videoLength;

    @SerializedName("myvideo_count")
    private int myvideoCount;

    @SerializedName("notificaiton_count")
    private int notificaiton_count;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("userstatus")
    private String userstatus;

    public int getNotificaiton_count() {
        return notificaiton_count;
    }

    public void setNotificaiton_count(int notificaiton_count) {
        this.notificaiton_count = notificaiton_count;
    }

    public void setUserdata(Userdata userdata) {
        this.userdata = userdata;
    }

    public Userdata getUserdata() {
        return userdata;
    }

    public void setFollowersData(List<FollowersDataItem> followersData) {
        this.followersData = followersData;
    }

    public List<FollowersDataItem> getFollowersData() {
        return followersData;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setMyvideoCount(int myvideoCount) {
        this.myvideoCount = myvideoCount;
    }

    public int getMyvideoCount() {
        return myvideoCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    @Override
    public String toString() {
        return
                "UserDetailResponse{" +
                        "userdata = '" + userdata + '\'' +
                        ",followers_data = '" + followersData + '\'' +
                        ",video_size = '" + videoSize + '\'' +
                        ",followers_count = '" + followersCount + '\'' +
                        ",video_length = '" + videoLength + '\'' +
                        ",myvideo_count = '" + myvideoCount + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}