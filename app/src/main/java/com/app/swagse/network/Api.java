package com.app.swagse.network;

import com.app.swagse.model.CountDataResponse;
import com.app.swagse.model.GetOTPResponse;
import com.app.swagse.model.RemoveDataResponse;
import com.app.swagse.model.SubPackageResponse;
import com.app.swagse.model.category.CategoryResponse;
import com.app.swagse.model.cityData.CityDataResponse;
import com.app.swagse.model.commentData.CommentResponse;
import com.app.swagse.model.countryData.CountryResponse;
import com.app.swagse.model.notification.NotificationResponse;
import com.app.swagse.model.stateData.StateDataResponse;
import com.app.swagse.model.subPackage.PackageResponse;
import com.app.swagse.model.subscription.SubscriptionResponse;
import com.app.swagse.model.swagTube.ChatNotification;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swaggerCommentData.SwaggerCommentResponse;
import com.app.swagse.model.swaggerData.SwaggerResponse;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Api {

    //GET OTP
    @FormUrlEncoded
    @POST("Apiutil/getotp")
    Call<GetOTPResponse> getOTP(@Field("mobile_no") String mobileNumber);

    //Verify OTP
    @FormUrlEncoded
    @POST("Apiutil/verifyotp")
    Call<UserDetailResponse> verifyOTP(@Field("mobile_no") String mobileNumber, @Field("otp") String otp, @Field("device_id") String device_id, @Field("fcm_token") String fcmToken);

    //Social Login
    @FormUrlEncoded
    @POST("Apiutil/sociallogin")
    Call<UserDetailResponse> socialLogin(@Field("user_name") String name, @Field("email") String emailID, @Field("picurl") String pic, @Field("logintype") String loginType,@Field("device_id") String device_id, @Field("fcm_token") String fcmToken);

    //SignUp
    @Multipart
    @POST("Apiutil/reguser")
    Call<UserDetailResponse> signupProfile(@PartMap Map<String, RequestBody> stringRequestBodyMap, @Part MultipartBody.Part img);

    //GET State
    @GET("Apiutil/getcountrydata")
    Call<CountryResponse> getCountry();
    //GET State
    @FormUrlEncoded
    @POST("Apiutil/getstatedata")
    Call<StateDataResponse> getState(@Field("ciso") String state);

    //GET OTP
    @FormUrlEncoded
    @POST("Apiutil/getcitydata")
    Call<CityDataResponse> getCity(@Field("ciso") String countryId, @Field("siso") String stateId);

    //GET OTP
    @FormUrlEncoded
    @POST("Swagtubeapi/getnotifications")
    Call<NotificationResponse> getnotifications(@Field("user_id") String userId);

     //GET OTP
    @FormUrlEncoded
    @POST("Swagtubeapi/changenotistatus")
    Call<NotificationResponse> changeNotificationStatus(@Field("noti_id") String notificationID);

    //GET OTP
    @FormUrlEncoded
    @POST("Swagtubeapi/search_video")
    Call<SwagTubeResponse> searchVideo(@Field("user_id") String countryId,@Field("keyword") String stateId);

    //UploadSwagTube Video
    @Multipart
    @POST("Swagtubeapi/postswagtubevideo")
    Call<UserDetailResponse> uploadSwagTubeVideo(@PartMap Map<String, RequestBody> stringRequestBodyMap, @Part MultipartBody.Part img);

    //Get SwagTube Data
    @FormUrlEncoded
    @POST("Swagtubeapi/getpostvideo")
    Call<SwagTubeResponse> getSwagTubeData(@Field("user_id") String userId);

    //Like SwagTube Data
    @FormUrlEncoded
    @POST("Swagtubeapi/postlikeaction")
    Call<SwagTubeResponse> swagTubeLike(@Field("post_id") String postId, @Field("user_id") String userId);

    //Comment SwagTube Data
    @FormUrlEncoded
    @POST("Swagtubeapi/postcommentaction")
    Call<CommentResponse> swagTubeComment(@Field("post_id") String postId, @Field("user_id") String userId, @Field("comment") String comment);

    //Follow SwagTube Channel
    @FormUrlEncoded
    @POST("Swagtubeapi/followchannel")
    Call<SwagTubeResponse> swagTubeFollow(@Field("channeluser") String channelUser, @Field("user_id") String userId, @Field("followflag") String followFlag);

    //Add Watch Later
    @FormUrlEncoded
    @POST("Swagtubeapi/watchlaterpostaction")
    Call<SwagTubeResponse> addWatchLater(@Field("post_id") String postId, @Field("user_id") String userId);

    //Save Video
    @FormUrlEncoded
    @POST("Swagtubeapi/savepostaction")
    Call<SwagTubeResponse> swagTubeSaveVideo(@Field("user_id") String userId, @Field("user_id") String postId);

    //SawagSe Video Details
    @FormUrlEncoded
    @POST("Swagtubeapi/getpostvideodetails")
    Call<SwagTubeResponse> swagTubeVideoDetails(@Field("user_id") String userId, @Field("post_id") String postId);

    //Download Video
    Call<ResponseBody> downloadImage(String s);

    //Report SwagTube Video
    @FormUrlEncoded
    @POST("Swagtubeapi/reportpost")
    Call<SwagTubeResponse> swagTubeReportPost(@Field("post_id") String id, @Field("user_id") String user_id, @Field("reason") String reason);

    //Get Category
    @GET("Swagtubeapi/getswagtubecategory")
    Call<CategoryResponse> getswagtubecategory();

    //Get Trending Category
    @FormUrlEncoded
    @POST("Swagtubeapi/gettrendingcatgorydata")
    Call<SwagTubeResponse> getTrendingCategoryData(@Field("user_id") String userId, @Field("category") String category);

    //Watch History
    @FormUrlEncoded
    @POST("Swagtubeapi/getwatchhistory")
    Call<SwagTubeResponse> getWatchHistory(@Field("user_id") String userId);

    //GET WATCH LATER
    @FormUrlEncoded
    @POST("Swagtubeapi/getwatchlater")
    Call<SwagTubeResponse> getWatchLater(@Field("user_id") String userId);

    //GET Report History
    @FormUrlEncoded
    @POST("Swagtubeapi/getreporthistory")
    Call<SwagTubeResponse> getReportHistory(@Field("user_id") String userId);

    //GET Like Video
    @FormUrlEncoded
    @POST("Swagtubeapi/getlikedvideo")
    Call<SwagTubeResponse> getLikedVideo(@Field("user_id") String userId);

    //GET Save Video
    @FormUrlEncoded
    @POST("Swagtubeapi/getsavedvideo")
    Call<SwagTubeResponse> getSaveVideo(@Field("user_id") String userId);

    //Get Subscription List
    @FormUrlEncoded
    @POST("Swagtubeapi/getsubscriptionlist")
    Call<SubscriptionResponse> getSubscriptionList(@Field("user_id") String userId);

    //Get My Videos
    @FormUrlEncoded
    @POST("Swagtubeapi/getmyvideos")
    Call<SwagTubeResponse> getYourVideo(@Field("user_id") String userId);

    //Get Trending Video
    @FormUrlEncoded
    @POST("Swagtubeapi/gettrendingvideos")
    Call<SwagTubeResponse> getTrendingVideos(@Field("user_id") String userId);

    //Get User Profile
    @FormUrlEncoded
    @POST("Swagtubeapi/getuserprofile")
    Call<UserDetailResponse> getUserProfile(@Field("user_id") String userId);

    //Update Profile
    @Multipart
    @POST("Swagtubeapi/updateprofile")
    Call<UserDetailResponse> updateProfile(@PartMap Map<String, RequestBody> stringRequestBodyMap, @Part MultipartBody.Part img);

    //Get Counts in Trending
    @FormUrlEncoded
    @POST("Swagtubeapi/getcounts")
    Call<CountDataResponse> getCounts(@Field("user_id") String userId);

    //Get Swagger Data
    @FormUrlEncoded
    @POST("Swaggerapi/getpostvideo")
    Call<SwaggerResponse> getSwaggerData(@Field("user_id") String userId);

    //Swagger Like Data
    @FormUrlEncoded
    @POST("Swaggerapi/postlikeaction")
    Call<SwagTubeResponse> swaggerLike(@Field("post_id") String postId, @Field("user_id") String userId);

    //Swagger Report Data
    @FormUrlEncoded
    @POST("Swaggerapi/reportpost")
    Call<SwagTubeResponse> swaggerReport(@Field("post_id") String id, @Field("user_id") String user_id, @Field("reason") String reason);

    //Swagger Comment
    @FormUrlEncoded
    @POST("Swaggerapi/postcommentaction")
    Call<SwaggerCommentResponse> swaggerComment(@Field("post_id") String postId, @Field("user_id") String userId, @Field("comment") String comment);

    //Delete My Post
    @FormUrlEncoded
    @POST("swagtubeapi/deletemypost")
    Call<RemoveDataResponse> deleteMyPost(@Field("post_id") String postId, @Field("user_id") String userId);

    //Delete My Post
    @FormUrlEncoded
    @POST("swagtubeapi/clearnotification")
    Call<RemoveDataResponse> clearNotification(@Field("user_id") String userId);

    //Delete History
    @FormUrlEncoded
    @POST("swagtubeapi/deletefromhistroy")
    Call<RemoveDataResponse> deleteHistory(@Field("post_id") String postId, @Field("user_id") String userId);

    //Delete LikeVideo
    @FormUrlEncoded
    @POST("swagtubeapi/deletefromliked")
    Call<RemoveDataResponse> deleteLikeVideo(@Field("post_id") String postId, @Field("user_id") String userId);

    //Delete LikeVideo
    @FormUrlEncoded
    @POST("swagtubeapi/deletefromwatchlater")
    Call<RemoveDataResponse> deleteWatchLater(@Field("post_id") String postId, @Field("user_id") String userId);

    //Delete LikeVideo
    @FormUrlEncoded
    @POST("swagtubeapi/deletefromwatcreport")
    Call<RemoveDataResponse> deleteReportVideo(@Field("post_id") String postId, @Field("user_id") String userId);

    //GET SUBSCRIPTION PACKAGES
    @GET("swagtubeapi/getsubscription")
    Call<PackageResponse> getSubscriptionPackage();

    //REQUEST FOR SUBSCRIPTION PACKAGES
    @FormUrlEncoded
    @POST("apiutil/reqforpack")
    Call<SubPackageResponse> subscribePackages(@Field("razorpayPaymentID") String razorpayPaymentID,@Field("pack_id") String postId, @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("apiutil/submit_feedback")
    Call<JsonElement> sendFeedback(@Field("pack_id") String postId, @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("apiutil/reqforpack")
    Call<JsonElement> contactUs(@Field("pack_id") String postId, @Field("user_id") String userId);

    //Send Chat Notification
    @FormUrlEncoded
    @POST("Swagtubeapi/sendchatnotification")
    Call<ChatNotification> swaggerChatNotification(@Field("receiver_id") String receiver_id, @Field("message") String message);

}
