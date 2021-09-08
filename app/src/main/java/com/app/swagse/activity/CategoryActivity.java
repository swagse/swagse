package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.adapter.ExoPlayerRecyclerView1;
import com.app.swagse.adapter.TrendingRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class CategoryActivity extends AppCompatActivity {

    ExoPlayerRecyclerView1 mRecyclerView;
    private ArrayList<SwagtubedataItem> mediaObjectList = new ArrayList<>();
    private TrendingRecyclerViewAdapter mAdapter;
    ProgressBar progressBar;
    RelativeLayout nothing_main_layout, categoryLayout;
    Api apiInterface;

    private SwipeRefreshLayout swagTubeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiInterface = RetrofitClient.getInstance().getApi();

        swagTubeRefresh = findViewById(R.id.swagTubeRefresh);
        progressBar = findViewById(R.id.progressBar);
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        categoryLayout = findViewById(R.id.categoryLayout);
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent intent = getIntent();
        if (intent.hasExtra(CategoryListActivity.class.getSimpleName())) {
            String categoryName = intent.getExtras().getString(CategoryListActivity.class.getSimpleName());
            if (!categoryName.isEmpty()) {
                getSupportActionBar().setTitle(categoryName);
                getSwagTubeData(categoryName);
            }
        }
    }

    public void getSwagTubeData(String categoryName) {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getTrendingCategoryData(PrefConnect.readString(CategoryActivity.this, Constants.USERID, ""), categoryName);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
//                                SwagTubeAdapter swagTubeAdapter = new SwagTubeAdapter(getContext(), swagTubeDataList);
//                                swagTubeRecyclerView.setAdapter(swagTubeAdapter);
                                //set data object
                                mRecyclerView.setMediaObjects(swagTubeDataList);
                                mAdapter = new TrendingRecyclerViewAdapter(swagTubeDataList, initGlide());
                                //Set Adapter
                                mRecyclerView.setAdapter(mAdapter);
                                categoryLayout.setVisibility(View.VISIBLE);
                                nothing_main_layout.setVisibility(View.GONE);
                            } else {
                                categoryLayout.setVisibility(View.GONE);
                                nothing_main_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(CategoryActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(CategoryActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(CategoryActivity.this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
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
}