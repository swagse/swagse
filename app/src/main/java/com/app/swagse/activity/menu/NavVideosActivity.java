package com.app.swagse.activity.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.UploadSwagTubeVideoActivity;
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.RemoveDataResponse;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class NavVideosActivity extends AppCompatActivity {

    RecyclerView videoRecyclerView;
    RelativeLayout nothing_main_layout, navVideoLayout;
    SwipeRefreshLayout navVideoSwipeLayout;
    ProgressBar progressBar;
    private Api apiInterface;
    AppCompatButton addVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_videos);

        getSupportActionBar().setTitle("Upload Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterface = RetrofitClient.getInstance().getApi();
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        navVideoLayout = findViewById(R.id.navVideoLayout);
        navVideoSwipeLayout = findViewById(R.id.navVideoSwipeLayout);
        progressBar = findViewById(R.id.progress_bar);
        addVideo = findViewById(R.id.addVideo);

        navVideoSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                navVideoSwipeLayout.setRefreshing(false);
                getSwagTubeData();
            }
        });

        getSwagTubeData();

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefConnect.readBoolean(NavVideosActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(NavVideosActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(NavVideosActivity.this, UploadSwagTubeVideoActivity.class));
                }
            }
        });

    }

    public void getSwagTubeData() {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getYourVideo(PrefConnect.readString(NavVideosActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                NavWatchAdapter swagTubeAdapter = new NavWatchAdapter(NavVideosActivity.this, swagTubeDataList, "myvideo");
                                videoRecyclerView.setAdapter(swagTubeAdapter);
                                navVideoLayout.setVisibility(View.VISIBLE);
                                nothing_main_layout.setVisibility(View.GONE);
                            } else {
                                nothing_main_layout.setVisibility(View.VISIBLE);
                                navVideoLayout.setVisibility(View.GONE);
                            }
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            navVideoLayout.setVisibility(View.GONE);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(NavVideosActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(NavVideosActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clear_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.clearAll) {
            removeVideoFromList();
//            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeVideoFromList() {
        ProgressDialog progressDialog = new ProgressDialog(NavVideosActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Api apiInterface = RetrofitClient.getInstance().getApi();
        Call<RemoveDataResponse> removeDataResponseCall = apiInterface.deleteMyPost("", PrefConnect.readString(NavVideosActivity.this, Constants.USERID, ""));
        removeDataResponseCall.enqueue(new Callback<RemoveDataResponse>() {
            @Override
            public void onResponse(Call<RemoveDataResponse> call, Response<RemoveDataResponse> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        OwnerGlobal.toast(NavVideosActivity.this, response.body().getMessage());
                        nothing_main_layout.setVisibility(View.VISIBLE);
                        navVideoLayout.setVisibility(View.GONE);
                    } else {
                        OwnerGlobal.toast(NavVideosActivity.this, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveDataResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t);
            }
        });
    }
}