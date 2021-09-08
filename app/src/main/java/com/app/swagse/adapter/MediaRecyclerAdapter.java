package com.app.swagse.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.listener.OnItemClickListener;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morris on 03,June,2019
 */
public class MediaRecyclerAdapter extends RecyclerView.Adapter {

    private List<SwagtubedataItem> mediaObjects;
    private RequestManager requestManager;

    public MediaRecyclerAdapter(List<SwagtubedataItem> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.swagtube_layout_design, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((PlayerViewHolder) viewHolder).onBind(mediaObjects, requestManager, MediaRecyclerAdapter.this);
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
