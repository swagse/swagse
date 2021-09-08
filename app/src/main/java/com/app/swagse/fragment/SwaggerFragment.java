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
import com.app.swagse.adapter.VideoItem;
import com.app.swagse.adapter.VideosAdapter;

import java.util.ArrayList;
import java.util.List;

public class SwaggerFragment extends Fragment {

    SwipeRefreshLayout swaggerRefresh;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swagger, container, false);

        swaggerRefresh = view.findViewById(R.id.swaggerRefresh);
        final ViewPager2 videosViewPager = view.findViewById(R.id.viewPagerVideos);
        List<VideoItem> videoItems = new ArrayList<>();

        VideoItem item = new VideoItem();
        item.videoURL = "https://liciolentimo.co.ke/img/videos/facebook.mp4";
        item.videoTitle = "Women In Tech";
        item.videoDesc = "International Women's Day 2019";
        videoItems.add(item);

        VideoItem item2 = new VideoItem();
        item2.videoURL = "https://liciolentimo.co.ke/img/videos/facebook3.mp4";
        item2.videoTitle = "Sasha Solomon";
        item2.videoDesc = "How Sasha Solomon Became a Software Developer at Twitter";
        videoItems.add(item2);

        VideoItem item3 = new VideoItem();
        item3.videoURL = "https://liciolentimo.co.ke/img/videos/facebook2.mp4";
        item3.videoTitle = "Happy Hour Wednesday";
        item3.videoDesc = " Depth-First Search Algorithm";
        videoItems.add(item3);

        videosViewPager.setAdapter(new VideosAdapter(getActivity(), videoItems));

        swaggerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videosViewPager.setAdapter(new VideosAdapter(getActivity(), videoItems));
                swaggerRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = mActivity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}