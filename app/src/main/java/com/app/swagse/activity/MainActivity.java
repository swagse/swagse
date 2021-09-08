package com.app.swagse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.swagse.Chat.Inbox.Inbox_F;
import com.app.swagse.LiveStreaming.Fragment.Live_Users_F;
import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.menu.NavHistoryActivity;
import com.app.swagse.activity.menu.NavLikeVideoActivity;
import com.app.swagse.activity.menu.NavReportHistoryActivity;
import com.app.swagse.activity.menu.NavSettingActivity;
import com.app.swagse.activity.menu.NavSubscriptionActivity;
import com.app.swagse.activity.menu.NavVideosActivity;
import com.app.swagse.activity.menu.NavWatchLaterActivity;
import com.app.swagse.adapter.ExoPlayerRecyclerView;
import com.app.swagse.constants.Constants;
import com.app.swagse.constants.Variables;
import com.app.swagse.controller.App;
import com.app.swagse.fragment.SwagTubeFragment;
import com.app.swagse.fragment.SwaggerNewFragment;
import com.app.swagse.fragment.TrendingFragment;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.CountDataResponse;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.model.userDetails.Userdata;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private AppCompatTextView mainuserName;
    private CircleImageView header_imageView;
    private ExoPlayerRecyclerView mRecyclerView;
    private AppCompatImageView search, chat;
    private TextView textCartItemCount, notification_count_batch;
    FrameLayout notificationLayout;
    Api apiInterface;

    //live
    TextView go_liveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = RetrofitClient.getInstance().getApi();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("SwagSe");

        go_liveText = findViewById(R.id.go_liveText);
        // get the reference of FrameLayout and TabLayout
        mainuserName = (AppCompatTextView) findViewById(R.id.mainuserName);
        simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        search = (AppCompatImageView) findViewById(R.id.search);
        chat = (AppCompatImageView) findViewById(R.id.chat);

        mRecyclerView = new ExoPlayerRecyclerView(MainActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        mainuserName = (AppCompatTextView) header.findViewById(R.id.mainuserName);
        header_imageView = (CircleImageView) header.findViewById(R.id.header_imageView);
        notification_count_batch = (TextView) header.findViewById(R.id.notification_count_batch);
        notificationLayout = (FrameLayout) header.findViewById(R.id.notificationLayout);

        navigationView.setNavigationItemSelectedListener(this);

        Variables.user_id = PrefConnect.readString(MainActivity.this, Constants.USERID, "");
        Variables.user_name = PrefConnect.readString(MainActivity.this, Constants.USERNAME, "");
        Variables.user_pic = PrefConnect.readString(MainActivity.this, Constants.USERPIC, "");

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onCreate: " + token);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Swag Tube"));
        tabLayout.addTab(tabLayout.newTab().setText("Swagger"));
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));

        SwagTubeFragment swagTubeFragment = new SwagTubeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.simpleFrameLayout, swagTubeFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        // perform setOnTabSelectedListener event on TabLayout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new SwagTubeFragment();
                        break;
                    case 1:
                        fragment = new SwaggerNewFragment();
                        break;
                    case 2:
                        fragment = new TrendingFragment();
                        break;
                }

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefConnect.readBoolean(MainActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    startActivity(new Intent(MainActivity.this, Inbox_F.class));
                }
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });

        go_liveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefConnect.readBoolean(MainActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    startActivity(new Intent(MainActivity.this, Live_Users_F.class));
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        textCartItemCount = (TextView) navigationView.getMenu().findItem(R.id.nav_later).getActionView();
        String userID = PrefConnect.readString(MainActivity.this, Constants.USERID, "");
        if (!userID.equals("")) {
            getUserProfile(userID);
        }
//        getCountsData();
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                PrefConnect.clearAllPrefs(MainActivity.this);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finishAffinity();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_subscription: {
                startActivity(new Intent(MainActivity.this, NavSubscriptionActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_package: {
                startActivity(new Intent(MainActivity.this, SubcriptionPackageActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_like: {
                startActivity(new Intent(MainActivity.this, NavLikeVideoActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_later: {
                startActivity(new Intent(MainActivity.this, NavWatchLaterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_reportHistory: {
                startActivity(new Intent(MainActivity.this, NavReportHistoryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_history: {
                startActivity(new Intent(MainActivity.this, NavHistoryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_videos: {
                startActivity(new Intent(MainActivity.this, NavVideosActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_profile: {
                if (PrefConnect.readBoolean(MainActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    startActivity(new Intent(MainActivity.this, FollowerUserActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            }
            case R.id.nav_settings: {
                startActivity(new Intent(MainActivity.this, NavSettingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_help: {
                startActivity(new Intent(MainActivity.this, NavHelpActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.nav_feedback: {
                startActivity(new Intent(MainActivity.this, NavFeedbackActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }

        }
        mRecyclerView.onPausePlayer();
        mRecyclerView.releasePlayer();
        Log.d(TAG, "onNavigationItemSelected: ");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getUserProfile(String userId) {
        if (App.getInstance().isOnline()) {
            Call<UserDetailResponse> userResponseCall = apiInterface.getUserProfile(userId);
            userResponseCall.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
//                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            Userdata userdata = response.body().getUserdata();
                            if (userdata != null) {
                                PrefConnect.writeString(MainActivity.this, Constants.USERNAME, userdata.getUserName());
                                PrefConnect.writeString(MainActivity.this, Constants.USERPIC, userdata.getImg());
                                PrefConnect.writeString(MainActivity.this, Constants.CHANNEL_NAME, userdata.getChannelName());
                                PrefConnect.writeString(MainActivity.this, Constants.EMAIL, userdata.getEmail());
                                PrefConnect.writeBoolean(MainActivity.this, Constants.GUEST_USER, false);
                                PrefConnect.writeString(MainActivity.this, Constants.VIDEO_DURATION, String.valueOf(response.body().getVideoLength()));
                                PrefConnect.writeString(MainActivity.this, Constants.VIDEO_SIZE, String.valueOf(response.body().getVideoSize()));
                                mainuserName.setText(PrefConnect.readString(MainActivity.this, Constants.USERNAME, ""));
                                String userpic = PrefConnect.readString(MainActivity.this, Constants.USERPIC, "");
                                if (userpic != null && !userpic.equals("")) {
                                    Glide.with(MainActivity.this).load(userpic).placeholder(R.drawable.ic_user).into(header_imageView);
                                }
                                notification_count_batch.setText("" + response.body().getNotificaiton_count());

                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(MainActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(MainActivity.this);
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
        String userID = PrefConnect.readString(MainActivity.this, Constants.USERID, "");
        if (!userID.equals("")) {
            getUserProfile(userID);
        }
//        getCountsData();
    }

    public void getCountsData() {
        if (App.isOnline()) {
            // progressBar.setVisibility(View.VISIBLE);
            Call<CountDataResponse> userResponseCall = apiInterface.getCounts(PrefConnect.readString(MainActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<CountDataResponse>() {
                @Override
                public void onResponse(Call<CountDataResponse> call, Response<CountDataResponse> response) {
                    //   progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "getCounts: " + response.body().toString());
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            Log.d(TAG, "getCounts123: " + response.body().getCountdata().getLaterCount());
                            if (response.body().getCountdata().getLaterCount() == 0) {
                                textCartItemCount.setVisibility(View.GONE);
                            } else {
                                textCartItemCount.setVisibility(View.VISIBLE);
                                textCartItemCount.setText(response.body().getCountdata().getLaterCount());
                            }
//                            likeVideoCount.setText("" + response.body().getCountdata().getLikedCount() + " videos");
//                            watchLaterCount.setText("" + response.body().getCountdata().getLaterCount() + " videos");
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(MainActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(MainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<CountDataResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }
}