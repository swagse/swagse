package com.app.swagse.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class TrendingRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<SwagtubedataItem> mediaObjects;
    private RequestManager requestManager;

    public TrendingRecyclerViewAdapter(List<SwagtubedataItem> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TrendingViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.treanding_layout_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((TrendingViewHolder) viewHolder).onBind(mediaObjects, requestManager, TrendingRecyclerViewAdapter.this);
    }


    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public void removeItem(int position) {
        mediaObjects.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mediaObjects.size());
        notifyDataSetChanged();
    }

}
