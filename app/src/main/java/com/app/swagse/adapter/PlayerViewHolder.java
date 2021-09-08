package com.app.swagse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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

import com.app.swagse.Chat.Chat_Fragment;
import com.app.swagse.Chat.Inbox.Inbox_F;
import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.ChatActivity;
import com.app.swagse.activity.MainActivity;
import com.app.swagse.activity.SwagTubeCommentActivity;
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
import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

/**
 * Created by Morris on 03,June,2019
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    public FrameLayout mediaContainer;
    public ImageView mediaCoverImage, volumeControl;
    public ProgressBar progressBar;
    public RequestManager requestManager;
    private TextView title, userHandle;
    private View parent;
    private AppCompatImageView postImage;
    private CircleImageView swagTubePic;
    private AppCompatTextView swagTubeWatchlater, swagTubeViewsCount, swagTube_follow, swagTubeName, swagTubeDays, swagTubeCommentCount, swagTubeOptions, swagTube_share, swagTube_comment, swagTube_like, swagTubeLikeCount;
    private LinearLayout main_social_layout_linear;
    private RelativeLayout main_count_layout;
    private RelativeLayout main_social_layout;
    private Api apiInterface;
    List<SwagtubedataItem> mediaObjectsData;
    OnItemClickListener mItemClickListener;
    public MediaRecyclerAdapter adapter;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;
        mediaContainer = itemView.findViewById(R.id.mediaContainer);
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
        title = itemView.findViewById(R.id.swagTubeName);
        userHandle = itemView.findViewById(R.id.swagTubeDays);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.ivVolumeControl);

        swagTubeLikeCount = itemView.findViewById(R.id.swagTubeLikeCount);
        swagTubeCommentCount = itemView.findViewById(R.id.swagTubeCommentCount);
        swagTube_share = itemView.findViewById(R.id.swagTube_share);
        swagTube_comment = itemView.findViewById(R.id.swagTube_comment);
        swagTube_like = itemView.findViewById(R.id.swagTube_like);
        swagTubeOptions = itemView.findViewById(R.id.swagTubeOptions);
        swagTube_follow = itemView.findViewById(R.id.swagTube_follow);
        main_social_layout_linear = itemView.findViewById(R.id.main_social_layout_linear);
        main_count_layout = itemView.findViewById(R.id.main_count_layout);
        main_social_layout = itemView.findViewById(R.id.main_social_layout);
        swagTubeViewsCount = itemView.findViewById(R.id.swagTubeViewsCount);
        swagTubeWatchlater = itemView.findViewById(R.id.swagTubeWatchlater);
    }

    void onBind(List<SwagtubedataItem> mediaObjectsData, RequestManager requestManager, MediaRecyclerAdapter adapter) {
        this.requestManager = requestManager;
        this.mediaObjectsData = mediaObjectsData;
        parent.setTag(this);
        title.setText(mediaObjectsData.get(getAdapterPosition()).getTitle());
        userHandle.setText(mediaObjectsData.get(getAdapterPosition()).getTimeago());
        this.adapter = adapter;

        swagTubeLikeCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getLikecount());
        swagTubeCommentCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getCommentcount() + " Comment");
        swagTubeViewsCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getViewscount());
        if (mediaObjectsData.get(getAdapterPosition()).getUserlikestatus() == 1) {
            swagTube_like.setTextColor(Color.parseColor("#025FA4"));
            swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unlike, 0, 0, 0);
        } else {
            swagTube_like.setTextColor(Color.parseColor("#000000"));
            swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
        }

//        if (mediaObjectsData.get(getAdapterPosition()).getCommentcount() != 0 || mediaObjectsData.get(getAdapterPosition()).getLikecount() != 0) {
//            swagTubeCommentCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getCommentcount() + " Comment");
//            swagTubeLikeCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getLikecount());
//            main_count_layout.setVisibility(View.VISIBLE);
//        } else {
//            main_count_layout.setVisibility(View.GONE);
//        }

        if (mediaObjectsData.get(getAdapterPosition()).getUserfollowstatus() == 1) {
            swagTube_follow.setTextColor(Color.parseColor("#025FA4"));
            swagTube_follow.setText("Follow");
            swagTube_follow.setVisibility(View.VISIBLE);
        } else {
            swagTube_follow.setVisibility(View.GONE);
        }
        this.requestManager.load(mediaObjectsData.get(getAdapterPosition()).getThmubnal()).into(mediaCoverImage);

        if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
            swagTubeOptions.setVisibility(View.GONE);
        } else {
            swagTubeOptions.setVisibility(View.VISIBLE);
        }


        swagTube_like.setOnClickListener(this);
        swagTube_comment.setOnClickListener(this);
        swagTube_share.setOnClickListener(this);
        swagTubeOptions.setOnClickListener(this);
        main_social_layout.setOnClickListener(this);
        swagTubeWatchlater.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swagTube_like: {
                if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
                    parent.getContext().startActivity(new Intent(parent.getContext(), LoginActivity.class));
                } else {
                    if (mediaObjectsData.get(getAdapterPosition()).getUserlikestatus() == 1) {
                        mediaObjectsData.get(getAdapterPosition()).setUserlikestatus(0);
                        mediaObjectsData.get(getAdapterPosition()).setLikecount(mediaObjectsData.get(getAdapterPosition()).getLikecount() - 1);
                        swagTubeLikeCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getLikecount());
                        swagTube_like.setTextColor(Color.parseColor("#000000"));
                        swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                        swagTubeLike(mediaObjectsData.get(getAdapterPosition()).getId());
                    } else if (mediaObjectsData.get(getAdapterPosition()).getUserlikestatus() == 0) {
                        mediaObjectsData.get(getAdapterPosition()).setUserlikestatus(1);
                        mediaObjectsData.get(getAdapterPosition()).setLikecount(mediaObjectsData.get(getAdapterPosition()).getLikecount() + 1);
                        swagTube_like.setTextColor(Color.parseColor("#025FA4"));
                        swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unlike, 0, 0, 0);
                        swagTubeLikeCount.setText("" + mediaObjectsData.get(getAdapterPosition()).getLikecount());
                        swagTubeLike(mediaObjectsData.get(getAdapterPosition()).getId());
                    }
                }
                break;
            }
            case R.id.swagTube_comment: {
                if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
                    parent.getContext().startActivity(new Intent(parent.getContext(), LoginActivity.class));
                } else {
                    parent.getContext().startActivity(new Intent(parent.getContext(), SwagTubeCommentActivity.class).putExtra(PlayerViewHolder.class.getSimpleName(), (Serializable) mediaObjectsData.get(getAdapterPosition()).getCommentdata()).putExtra("id", mediaObjectsData.get(getAdapterPosition()).getId()));
                }
                break;
            }
            case R.id.main_social_layout: {
                parent.getContext().startActivity(new Intent(parent.getContext(), SwagTubeDetailsActivity.class).putExtra(PlayerViewHolder.class.getSimpleName(), mediaObjectsData.get(getAdapterPosition()).getId()));
                break;
            }
            case R.id.swagTube_share: {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hii");
                    String shareMessage = "\nHi! I found this Post on SwagSe App - check it out now \n\n " + Constants.BASE_URL + "watch/" + OwnerGlobal.getMd5(mediaObjectsData.get(getAdapterPosition()).getId());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    parent.getContext().startActivity(Intent.createChooser(shareIntent, "Send To"));
                } catch (Exception e) {
                    e.toString();
                }
                break;
            }
            case R.id.swagTubeWatchlater: {
                if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
                    parent.getContext().startActivity(new Intent(parent.getContext(), LoginActivity.class));
                } else {
                    swagTubeWatchLater(mediaObjectsData.get(getAdapterPosition()).getId());
                }
                break;
            }
            case R.id.swagTubeOptions: {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(parent.getContext(), swagTubeOptions);
                //inflating menu from xml resource
                //adding click listener
                popup.inflate(R.menu.swagtube_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.report_post: {
                                //handle menu1 click
                                // setup the alert builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                                builder.setTitle("Choose a reason for reporting this post:");
                                // add a list
                                String[] animals = {"Not Relevant to SwagSe", "Suspicious or Spam", "Abusive or Obscene", "Posted Multiple Times", "Fake news", "Not Interested"};
                                builder.setItems(animals, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0: // horse
                                            {
                                                removePost(mediaObjectsData.get(getAdapterPosition()).getId(), "Not Relevant to SwagSe", getAdapterPosition());
                                                break;
                                            }
                                            case 1: // cow
                                            {
                                                removePost(mediaObjectsData.get(getAdapterPosition()).getId(), "Suspicious or Spam", getAdapterPosition());
                                                break;
                                            }
                                            case 2: // camel
                                            {
                                                removePost(mediaObjectsData.get(getAdapterPosition()).getId(), "Abusive or Obscene", getAdapterPosition());
                                                break;
                                            }
                                            case 3: // sheep
                                            {
                                                removePost(mediaObjectsData.get(getAdapterPosition()).getId(), "Posted Multiple Times", getAdapterPosition());
                                                break;
                                            }
                                            case 4: // sheep
                                            {
                                                removePost(mediaObjectsData.get(getAdapterPosition()).getId(), "Fake news", getAdapterPosition());
                                                break;
                                            }
                                        }
                                    }
                                });
                                // create and show the alert dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                            }
                            case R.id.copy_video_url: {
                                int sdk = android.os.Build.VERSION.SDK_INT;
                                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setText("text to clip");
                                } else {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("copy", mediaObjectsData.get(getAdapterPosition()).getVideourl());
                                    clipboard.setPrimaryClip(clip);
                                }
                                break;
                            }
                            case R.id.hide_post: {
                                adapter.removeItem(getAdapterPosition());
                                break;
                            }
                            case R.id.chat_post:
                                if (PrefConnect.readBoolean(parent.getContext(), Constants.GUEST_USER, false)) {
                                    parent.getContext().startActivity(new Intent(parent.getContext(), LoginActivity.class));
//                                    parent.getContext()overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else {
                                    Intent i = new Intent(parent.getContext(), ChatActivity.class);
                                    i.putExtra("user_id", mediaObjectsData.get(getAdapterPosition()).getUserid());
                                    i.putExtra("user_name", mediaObjectsData.get(getAdapterPosition()).getName());
                                    i.putExtra("user_pic", mediaObjectsData.get(getAdapterPosition()).getThmubnal());
                                    parent.getContext().startActivity(i);
//                                    parent.getContext().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
            break;
        }
    }

    private void swagTubeLike(String postId) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeLike(postId, PrefConnect.readString(parent.getContext(), Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {

                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
//                            toast(context, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
//                        OwnerGlobal.LoginRedirect(getActivity());
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t);
                }
            });
        }
    }

    private void removePost(String id, String reason, int adapterPosition) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeReportPost(id, PrefConnect.readString(parent.getContext(), Constants.USERID, ""), reason);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            adapter.removeItem(getAdapterPosition());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
//                            toast(context, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
//                        OwnerGlobal.LoginRedirect(getActivity());
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
//                    progressDialog.dismiss();
                }
            });
        }
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