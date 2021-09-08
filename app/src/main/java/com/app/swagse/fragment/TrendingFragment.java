package com.app.swagse.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.activity.CategoryListActivity;
import com.app.swagse.activity.TrendingActivity;
import com.app.swagse.activity.menu.NavLikeVideoActivity;
import com.app.swagse.activity.menu.NavVideosActivity;
import com.app.swagse.activity.menu.NavWatchLaterActivity;
import com.app.swagse.activity.trending.FashionBeautyActivity;
import com.app.swagse.activity.trending.FilmsActivity;
import com.app.swagse.activity.trending.GamingActivity;
import com.app.swagse.activity.trending.LearningActivity;
import com.app.swagse.activity.trending.LiveActivity;
import com.app.swagse.activity.trending.MusicActivity;
import com.app.swagse.activity.trending.NewsActivity;
import com.app.swagse.adapter.ExoPlayerRecyclerView1;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.adapter.TrendingRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.fragment.trend.FilmsFragment;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.CountDataResponse;
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

public class TrendingFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = TrendingFragment.class.getSimpleName();
    private Api apiInterface;
    AppCompatButton trendingButton, trendingLive, trendingLearning, trendingBeauty, trendingFilms, trendingNews, trendingGame, trendingMusic;
    LinearLayout yourVideos, downloadVideo, likeVideo, categoryVideo, watchLaterVideo;
    private Activity mActivity;
    AppCompatTextView likeVideoCount, downloadVideoCount, watchLaterCount;
    ExoPlayerRecyclerView1 mRecyclerView;
    private ArrayList<SwagtubedataItem> mediaObjectList = new ArrayList<>();
    private TrendingRecyclerViewAdapter mAdapter;
    private boolean firstTime = true;
    private SwipeRefreshLayout swipeRefersh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        swipeRefersh = view.findViewById(R.id.swipeRefersh);
        trendingButton = view.findViewById(R.id.trendingButton);
        trendingLive = view.findViewById(R.id.trendingLive);
        trendingLearning = view.findViewById(R.id.trendingLearning);
        trendingBeauty = view.findViewById(R.id.trendingBeauty);
        trendingFilms = view.findViewById(R.id.trendingFilms);
        trendingNews = view.findViewById(R.id.trendingNews);
        trendingGame = view.findViewById(R.id.trendingGame);
        trendingMusic = view.findViewById(R.id.trendingMusic);

        yourVideos = view.findViewById(R.id.yourVideos);
        likeVideo = view.findViewById(R.id.likeVideo);
        downloadVideo = view.findViewById(R.id.downloadVideo);
        categoryVideo = view.findViewById(R.id.categoryVideo);
        watchLaterVideo = view.findViewById(R.id.watchLaterVideo);

        likeVideoCount = view.findViewById(R.id.likeVideoCount);
        downloadVideoCount = view.findViewById(R.id.downloadVideoCount);
        watchLaterCount = view.findViewById(R.id.watchLaterCount);

        apiInterface = RetrofitClient.getInstance().getApi();

        mRecyclerView = view.findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        getSwagTubeData();


       /* trendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, TrendingActivity.class));
            }
        });*/

        trendingButton.setOnClickListener(this);
        trendingLive.setOnClickListener(this);
        trendingLearning.setOnClickListener(this);
        trendingBeauty.setOnClickListener(this);
        trendingFilms.setOnClickListener(this);
        trendingNews.setOnClickListener(this);
        trendingGame.setOnClickListener(this);
        trendingMusic.setOnClickListener(this);
        likeVideo.setOnClickListener(this);
        downloadVideo.setOnClickListener(this);
        yourVideos.setOnClickListener(this);
        categoryVideo.setOnClickListener(this);
        watchLaterVideo.setOnClickListener(this);

        swipeRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCountsData();
                swipeRefersh.setRefreshing(false);
            }
        });
        return view;
    }


    public void getSwagTubeData() {
        Log.d("TAG", "getSwagTubeData: " + "Hello");
        if (App.isOnline()) {
            // progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getTrendingVideos(PrefConnect.readString(mActivity, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    //   progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                mRecyclerView.setMediaObjects(swagTubeDataList);
                                mAdapter = new TrendingRecyclerViewAdapter(swagTubeDataList, initGlide());
                                //Set Adapter
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(mActivity, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(mActivity);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                }
            });
        }
    }

    private RequestManager initGlide() {
        if (mActivity == null){
            return null;
        }
        RequestOptions options = new RequestOptions();
        return Glide.with(mActivity)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trendingButton: {
                startActivity(new Intent(mActivity, TrendingActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingLive: {
                startActivity(new Intent(mActivity, LiveActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingLearning: {
                startActivity(new Intent(mActivity, LearningActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingBeauty: {
                startActivity(new Intent(mActivity, FashionBeautyActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingFilms: {
                startActivity(new Intent(mActivity, FilmsActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingGame: {
                startActivity(new Intent(mActivity, GamingActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingNews: {
                startActivity(new Intent(mActivity, NewsActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.trendingMusic: {
                startActivity(new Intent(mActivity, MusicActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.likeVideo: {
                startActivity(new Intent(mActivity, NavLikeVideoActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.downloadVideo: {
//                startActivity(new Intent(mActivity, MusicActivity.class));
//                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.yourVideos: {
                startActivity(new Intent(mActivity, NavVideosActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.categoryVideo: {
                startActivity(new Intent(mActivity, CategoryListActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.watchLaterVideo: {
                startActivity(new Intent(mActivity, NavWatchLaterActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.d("TAG", "onPause: ");
        if (mRecyclerView != null) {
            mRecyclerView.onPausePlayer();
        }
        super.onPause();
    }

    public void getCountsData() {
        if (App.isOnline()) {
            // progressBar.setVisibility(View.VISIBLE);
            Call<CountDataResponse> userResponseCall = apiInterface.getCounts(PrefConnect.readString(mActivity, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<CountDataResponse>() {
                @Override
                public void onResponse(Call<CountDataResponse> call, Response<CountDataResponse> response) {
                    //   progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "getCounts: " + response.body().toString());
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            Log.d(TAG, "getCounts123: " + response.body().toString());
                            likeVideoCount.setText("" + response.body().getCountdata().getLikedCount() + " videos");
                            watchLaterCount.setText("" + response.body().getCountdata().getLaterCount() + " videos");
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(mActivity, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(mActivity);
                    }
                }

                @Override
                public void onFailure(Call<CountDataResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCountsData();
    }
}