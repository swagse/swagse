package com.app.swagse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.listener.OnItemClickListener;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class TrendingViewHolder extends RecyclerView.ViewHolder {

    public FrameLayout mediaContainer;
    public ImageView mediaCoverImage, volumeControl;
    public ProgressBar progressBar;
    public RequestManager requestManager;
    private View parent;
    List<SwagtubedataItem> mediaObjects;
    public MediaRecyclerAdapter adapter;
    private CircleImageView swagTubePic;
    private AppCompatTextView swagTubeOptions, trendingTimeago, trendingViews, trendingChanelName, swagTubeName;
    private Api apiInterface;

    public TrendingViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        swagTubeName = itemView.findViewById(R.id.swagTubeName);
        trendingChanelName = itemView.findViewById(R.id.trendingChanelName);
        trendingTimeago = itemView.findViewById(R.id.trendingTimeago);
        trendingViews = itemView.findViewById(R.id.trendingViews);
        swagTubeOptions = itemView.findViewById(R.id.swagTubeOptions);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.ivVolumeControl);


    }

    public void onBind(List<SwagtubedataItem> mediaObjects, RequestManager requestManager, TrendingRecyclerViewAdapter trendingRecyclerViewAdapter) {
        this.requestManager = requestManager;
        this.mediaObjects = mediaObjects;
        parent.setTag(this);
        this.requestManager.load(mediaObjects.get(getAdapterPosition()).getThmubnal()).into(mediaCoverImage);
        swagTubeName.setText(mediaObjects.get(getAdapterPosition()).getTitle());
        trendingTimeago.setText(mediaObjects.get(getAdapterPosition()).getTimeago());
        trendingViews.setText(mediaObjects.get(getAdapterPosition()).getViewscount() + " views");
        trendingChanelName.setText(mediaObjects.get(getAdapterPosition()).getName());

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getContext().startActivity(new Intent(parent.getContext(), SwagTubeDetailsActivity.class).putExtra(TrendingViewHolder.class.getSimpleName(), mediaObjects.get(getAdapterPosition()).getId()));
            }
        });

        swagTubeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(parent.getContext(), swagTubeOptions);
                //inflating menu from xml resource
                //adding click listener
                popup.inflate(R.menu.trending_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.save_watch_later: {
                                if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
                                    parent.getContext().startActivity(new Intent(parent.getContext(), LoginActivity.class));
                                } else {
                                    swagTubeWatchLater(mediaObjects.get(getAdapterPosition()).getId());
                                }
                                break;
                            }
                            case R.id.copy_video_url: {
                                int sdk = android.os.Build.VERSION.SDK_INT;
                                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setText("text to clip");
                                } else {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("copy", mediaObjects.get(getAdapterPosition()).getVideourl());
                                    clipboard.setPrimaryClip(clip);
                                }
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

    private void swagTubeWatchLater(String postId) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.addWatchLater(postId, PrefConnect.readString(parent.getContext(), Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            OwnerGlobal.toast((Activity) parent.getContext(), response.body().getMessage());
                        } else {
                            OwnerGlobal.toast((Activity) parent.getContext(), response.body().getMessage());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast((Activity) parent.getContext(), jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect((Activity) parent.getContext());
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                }
            });
        }

    }
}
