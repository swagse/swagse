package com.app.swagse.fragment.trend;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.activity.UploadSwagTubeVideoActivity;
import com.app.swagse.adapter.ExoPlayerRecyclerView;
import com.app.swagse.adapter.ExoPlayerRecyclerView1;
import com.app.swagse.adapter.MediaRecyclerAdapter;
import com.app.swagse.adapter.SwagTubeAdapter;
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


public class NewsFragment extends Fragment {

    ProgressBar progressBar;
    RelativeLayout nothing_main_layout;
    Api apiInterface;
    private SwipeRefreshLayout swagTubeRefresh;

    ExoPlayerRecyclerView1 mRecyclerView;
    private ArrayList<SwagtubedataItem> mediaObjectList = new ArrayList<>();
    private TrendingRecyclerViewAdapter mAdapter;
    private boolean firstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        apiInterface = RetrofitClient.getInstance().getApi();

        swagTubeRefresh = view.findViewById(R.id.swagTubeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
//        swagTubeRecyclerView = view.findViewById(R.id.swagTubeRecyclerView);
//        swagTubeRecyclerView.setHasFixedSize(true);
//        swagTubeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mRecyclerView = view.findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        getSwagTubeData();

//        swagTubeUploadVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), UploadSwagTubeVideoActivity.class));
//                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });

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
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getTrendingCategoryData(PrefConnect.readString(getActivity(), Constants.USERID, ""), "News");
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
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(getActivity(), jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(getActivity());
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
        return Glide.with(getActivity())
                .setDefaultRequestOptions(options);
    }

/*    @Override
    public void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
    }*/
}