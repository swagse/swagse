package com.app.swagse.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.MainActivity;
import com.app.swagse.activity.SignUpActivity;
import com.app.swagse.activity.UploadSwagTubeVideoActivity;
import com.app.swagse.adapter.ExoPlayerRecyclerView;
import com.app.swagse.adapter.MediaRecyclerAdapter;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.adapter.VideoItem;
import com.app.swagse.adapter.VideosAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.MovableFloatingActionButton;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.userDetails.UserDetailResponse;
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

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class SwagTubeFragment extends Fragment {

    public SwagTubeFragment() {
    }

    private Activity mActivity;
    private RecyclerView swagTubeRecyclerView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private Api apiInterface;
    private boolean isViewShown = false;
    private MovableFloatingActionButton swagTubeUploadVideo;
    private SwipeRefreshLayout swagTubeRefresh;

    ExoPlayerRecyclerView mRecyclerView;
    private ArrayList<SwagtubedataItem> mediaObjectList = new ArrayList<>();
    private MediaRecyclerAdapter mAdapter;
    private boolean firstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swag_tube, container, false);
        apiInterface = RetrofitClient.getInstance().getApi();

        swagTubeRefresh = view.findViewById(R.id.swagTubeRefresh);
        swagTubeUploadVideo = view.findViewById(R.id.swagTubeUploadVideo);
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
//        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getSwagTubeData();

        if (PrefConnect.readBoolean(getContext(), Constants.GUEST_USER, false)) {
            swagTubeUploadVideo.setVisibility(View.GONE);
        } else {
            swagTubeUploadVideo.setVisibility(View.VISIBLE);
        }

        swagTubeUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, UploadSwagTubeVideoActivity.class));
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        swagTubeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwagTubeData();
                swagTubeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    public void getSwagTubeData() {
//        if (mActivity == null) {
//            return;
//        }
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getSwagTubeData(PrefConnect.readString(mActivity, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                //set data object
                                mRecyclerView.setMediaObjects(swagTubeDataList);
                                mAdapter = new MediaRecyclerAdapter(swagTubeDataList, initGlide());
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
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private RequestManager initGlide() {
        if (mActivity == null) {
            return null;
        }
        RequestOptions options = new RequestOptions();
        return Glide.with(mActivity)
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
    public void onPause() {
        Log.d("TAG", "onPause: ");
        if (mRecyclerView != null) {
            mRecyclerView.onPausePlayer();
        }
        super.onPause();
    }
}