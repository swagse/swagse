package com.app.swagse.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.activity.menu.NavHistoryActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.RemoveDataResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavWatchAdapter extends RecyclerView.Adapter<NavWatchAdapter.NavWatchViewHolder> {

    private static final String TAG = NavWatchAdapter.class.getSimpleName();
    Context context;
    List<SwagtubedataItem> swagTubeDataList;
    private ProgressDialog progressDialog;
    String from;
    Call<RemoveDataResponse> removeDataResponseCall;

    public NavWatchAdapter(Context context, List<SwagtubedataItem> swagTubeDataList, String froms) {
        this.context = context;
        this.swagTubeDataList = swagTubeDataList;
        this.from = froms;
    }

    @NonNull
    @Override
    public NavWatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NavWatchViewHolder(LayoutInflater.from(context).inflate(R.layout.like_video_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NavWatchViewHolder holder, int position) {
        SwagtubedataItem swagtubedataItem = swagTubeDataList.get(position);
        Glide.with(context).load(swagtubedataItem.getThmubnal()).placeholder(R.drawable.gray_bg).into(holder.videoThumbnails);
        holder.videoTitle.setText(swagtubedataItem.getTitle());
        holder.channelName.setText(swagtubedataItem.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SwagTubeDetailsActivity.class).putExtra(NavWatchAdapter.class.getSimpleName(), swagTubeDataList.get(position).getId()));
            }
        });

        holder.swagTubeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.swagTubeOptions);
                //inflating menu from xml resource
                //adding click listener
                popup.inflate(R.menu.history_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeVideo: {
                                //handle menu1 click
                                // setup the alert builder
                                removeVideoFromList(swagtubedataItem.getId(), holder.getAdapterPosition());
                                break;
                            }
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    private void removeVideoFromList(String id, int adapterPosition) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Api apiInterface = RetrofitClient.getInstance().getApi();
        if (from.equals("watchlater")) {
            removeDataResponseCall = apiInterface.deleteWatchLater(id, PrefConnect.readString(context, Constants.USERID, ""));
        } else if (from.equals("history")) {
            removeDataResponseCall = apiInterface.deleteHistory(id, PrefConnect.readString(context, Constants.USERID, ""));
        } else if (from.equals("like")) {
            removeDataResponseCall = apiInterface.deleteLikeVideo(id, PrefConnect.readString(context, Constants.USERID, ""));
        } else if (from.equals("myvideo")) {
            removeDataResponseCall = apiInterface.deleteMyPost(id, PrefConnect.readString(context, Constants.USERID, ""));
        } else if (from.equals("report")) {
            removeDataResponseCall = apiInterface.deleteReportVideo(id, PrefConnect.readString(context, Constants.USERID, ""));
        }
        removeDataResponseCall.enqueue(new Callback<RemoveDataResponse>() {
            @Override
            public void onResponse(Call<RemoveDataResponse> call, Response<RemoveDataResponse> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        OwnerGlobal.toast((Activity) context, response.body().getMessage());
                        swagTubeDataList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        notifyItemRangeChanged(adapterPosition, swagTubeDataList.size());
                        notifyDataSetChanged();
                    } else {
                        OwnerGlobal.toast((Activity) context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveDataResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return swagTubeDataList == null ? 0 : swagTubeDataList.size();
    }

    public class NavWatchViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView swagTubeOptions, channelName, videoTitle;
        AppCompatImageView videoThumbnails;

        public NavWatchViewHolder(@NonNull View itemView) {
            super(itemView);

            swagTubeOptions = itemView.findViewById(R.id.swagTubeOptions);
            channelName = itemView.findViewById(R.id.channelName);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoThumbnails = itemView.findViewById(R.id.videoThumbnails);
        }
    }
}
