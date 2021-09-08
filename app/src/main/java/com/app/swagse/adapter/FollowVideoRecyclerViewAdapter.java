package com.app.swagse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class FollowVideoRecyclerViewAdapter extends RecyclerView.Adapter<FollowVideoRecyclerViewAdapter.FollowVideoViewHolder> {

    Context context;
    List<SwagtubedataItem> swagTubeDataList;

    public FollowVideoRecyclerViewAdapter(Context context, List<SwagtubedataItem> swagTubeDataList) {
        this.context = context;
        this.swagTubeDataList = swagTubeDataList;
    }

    @NonNull
    @Override
    public FollowVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_layout_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FollowVideoViewHolder holder, int position) {
        Glide.with(context).load(swagTubeDataList.get(position).getThmubnal()).placeholder(R.drawable.gray_bg).into(holder.videoThubnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SwagTubeDetailsActivity.class).putExtra(FollowVideoRecyclerViewAdapter.class.getSimpleName(), swagTubeDataList.get(position).getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return swagTubeDataList == null ? 0 : swagTubeDataList.size();
    }

    public class FollowVideoViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView videoThubnail;

        public FollowVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThubnail = itemView.findViewById(R.id.videoThubnail);
        }
    }
}
