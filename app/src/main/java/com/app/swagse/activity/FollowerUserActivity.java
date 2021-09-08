package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.activity.menu.NavSubscriptionActivity;
import com.app.swagse.activity.menu.ProfileActivity;
import com.app.swagse.adapter.FollowVideoRecyclerViewAdapter;
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.model.userDetails.Userdata;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class FollowerUserActivity extends AppCompatActivity {

    @BindView(R.id.myVideoRecyclerView)
    RecyclerView myVideoRecyclerView;
    @BindView(R.id.nothing_main_layout)
    RelativeLayout nothing_main_layout;
    @BindView(R.id.followerCount)
    AppCompatTextView followerCount;
    @BindView(R.id.postCount)
    AppCompatTextView postCount;
    @BindView(R.id.userName)
    AppCompatTextView userName;
    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.followerLayout)
    LinearLayout followerLayout;
    @BindView(R.id.profileLayout)
    LinearLayout profileLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    Api apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_user);
        ButterKnife.bind(this);
        apiInterface = RetrofitClient.getInstance().getApi();
        myVideoRecyclerView.setHasFixedSize(true);
        myVideoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSwagTubeData();
    }

    @OnClick({R.id.editProfile, R.id.followerLayout})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.editProfile: {
                startActivity(new Intent(FollowerUserActivity.this, ProfileActivity.class));
                break;
            }
            case R.id.followerLayout: {
                startActivity(new Intent(FollowerUserActivity.this, NavSubscriptionActivity.class));
                break;
            }
        }
    }

    public void getSwagTubeData() {
        if (App.isOnline()) {
            Call<SwagTubeResponse> userResponseCall = apiInterface.getYourVideo(PrefConnect.readString(FollowerUserActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                FollowVideoRecyclerViewAdapter swagTubeAdapter = new FollowVideoRecyclerViewAdapter(FollowerUserActivity.this, swagTubeDataList);
                                myVideoRecyclerView.setAdapter(swagTubeAdapter);
                                myVideoRecyclerView.setVisibility(View.VISIBLE);
                                nothing_main_layout.setVisibility(View.GONE);
                            } else {
                                nothing_main_layout.setVisibility(View.VISIBLE);
                                myVideoRecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            myVideoRecyclerView.setVisibility(View.GONE);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(FollowerUserActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(FollowerUserActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getuserprofile(String userId) {
        if (App.getInstance().isOnline()) {
            Call<UserDetailResponse> userResponseCall = apiInterface.getUserProfile(userId);
            userResponseCall.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
//                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            profileLayout.setVisibility(View.VISIBLE);
                            postCount.setText(String.valueOf(response.body().getMyvideoCount()));
                            followerCount.setText(String.valueOf(response.body().getFollowersCount()));
                            Userdata userdata = response.body().getUserdata();
                            if (userdata != null) {
                                if (!userdata.getImg().equals("")) {
                                    Glide.with(FollowerUserActivity.this).load(userdata.getImg()).placeholder(R.drawable.ic_user).into(userImage);
                                }
                                userName.setText(userdata.getUserName());
                                getSupportActionBar().setTitle(userdata.getUserName());
                            } else if (response.code() == Constants.FAILED) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    toast(FollowerUserActivity.this, jObjError.getString("response_msg"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else if (response.code() == Constants.UNAUTHORIZED) {
                                OwnerGlobal.LoginRedirect(FollowerUserActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                    //progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getuserprofile(PrefConnect.readString(FollowerUserActivity.this, Constants.USERID, ""));
    }
}