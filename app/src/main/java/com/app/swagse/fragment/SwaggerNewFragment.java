package com.app.swagse.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.swagse.R;
import com.app.swagse.adapter.MediaRecyclerAdapter;
import com.app.swagse.adapter.VideoAdapterNew;
import com.app.swagse.adapter.VideoItem;
import com.app.swagse.adapter.VideosAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.swaggerData.SwaggerResponse;
import com.app.swagse.model.swaggerData.SwaggerdataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;


public class SwaggerNewFragment extends Fragment {

    private Activity mActivity;
    SwipeRefreshLayout swaggerRefresh;
    ViewPager2 videosViewPager;
    private Api apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swagger_new, container, false);
        swaggerRefresh = view.findViewById(R.id.swaggerRefresh);
        videosViewPager = view.findViewById(R.id.viewPagerVideos);
        apiInterface = RetrofitClient.getInstance().getApi();
        getSwaggerData();

        swaggerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwaggerData();
                swaggerRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    public void getSwaggerData() {
        if (App.isOnline()) {
            Call<SwaggerResponse> userResponseCall = apiInterface.getSwaggerData(PrefConnect.readString(mActivity, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwaggerResponse>() {
                @Override
                public void onResponse(Call<SwaggerResponse> call, Response<SwaggerResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwaggerdataItem> swaggerdataItemList = response.body().getSwaggerdata();
                            if (swaggerdataItemList.size() != 0 && swaggerdataItemList != null && !swaggerdataItemList.isEmpty()) {
                                //set data object
//                                mRecyclerView.setMediaObjects(swagTubeDataList);
//                                mAdapter = new MediaRecyclerAdapter(swagTubeDataList, initGlide());
//                                //Set Adapter
//                                mRecyclerView.setAdapter(mAdapter);
                                videosViewPager.setAdapter(new VideoAdapterNew(getActivity(), swaggerdataItemList));
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
                public void onFailure(Call<SwaggerResponse> call, Throwable t) {
//                    progressBar.setVisibility(View.GONE);
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
}