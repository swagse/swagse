package com.app.swagse.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.SwagTubeCommentActivity;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.GetOTPResponse;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;
//import com.potyvideo.library.AndExoPlayerView;

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

public class SwagTubeAdapter extends RecyclerView.Adapter<SwagTubeAdapter.SwagTubeViewHolder> {

    private static final String TAG = SwagTubeAdapter.class.getSimpleName();
    Context context;
    List<SwagtubedataItem> swagTubeDataList;
    Api apiInterface;

    public SwagTubeAdapter(Context context, List<SwagtubedataItem> swagTubeDataList) {
        this.context = context;
        this.swagTubeDataList = swagTubeDataList;
    }

    @NonNull
    @Override
    public SwagTubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SwagTubeViewHolder(LayoutInflater.from(context).inflate(R.layout.swagtube_layout_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SwagTubeViewHolder holder, int position) {
        SwagtubedataItem swagtubedataItem = swagTubeDataList.get(position);
//        Glide.with(context).load(swagtubedataItem.getThmubnal()).centerCrop().into(holder.postImage);
        Glide.with(context).load(swagtubedataItem.getThmubnal()).centerCrop().into(holder.swagTubePic);
        holder.swagTubeName.setText(swagtubedataItem.getName());
        holder.swagTubeDays.setText(swagtubedataItem.getTimeago());
        holder.swagTubeLikeCount.setText("" + swagtubedataItem.getLikecount());
        holder.swagTubeCommentCount.setText("" + swagtubedataItem.getCommentcount() + " Comment");

        if (swagTubeDataList.get(position).getUserlikestatus() == 1) {
            holder.swagTube_like.setTextColor(Color.parseColor("#025FA4"));
            holder.swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unlike, 0, 0, 0);
        } else {
            holder.swagTube_like.setTextColor(Color.parseColor("#000000"));
            holder.swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
        }

        if (swagTubeDataList.get(position).getCommentcount() != 0 || swagTubeDataList.get(position).getLikecount() != 0) {
            holder.swagTubeCommentCount.setText("" + swagTubeDataList.get(position).getCommentcount() + " Comment");
            holder.swagTubeLikeCount.setText("" + swagTubeDataList.get(position).getLikecount());
            holder.main_count_layout.setVisibility(View.VISIBLE);
        } else {
            holder.main_count_layout.setVisibility(View.GONE);
        }

        if (swagTubeDataList.get(position).getUserfollowstatus() == 1) {
            holder.swagTube_follow.setTextColor(Color.parseColor("#025FA4"));
            holder.swagTube_follow.setText("Follow");
            holder.swagTube_follow.setVisibility(View.VISIBLE);
        } else {
            holder.swagTube_follow.setVisibility(View.GONE);
        }

        holder.main_social_layout_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SwagTubeDetailsActivity.class).putExtra(SwagTubeAdapter.class.getSimpleName(), swagTubeDataList.get(position).getId()));
            }
        });

        if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
            holder.swagTubeOptions.setVisibility(View.GONE);
        } else {
            holder.swagTubeOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.swagTubeOptions);
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose a reason for reporting this post:");
                                    // add a list
                                    String[] animals = {"Not Relevant to SwagSe", "Suspicious or Spam", "Abusive or Obscene", "Posted Multiple Times", "Fake news", "Not Interested"};
                                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0: // horse
                                                {
                                                    removePost(swagTubeDataList.get(position).getId(), "Not Relevant to SwagSe", holder.getAdapterPosition());
                                                    break;
                                                }
                                                case 1: // cow
                                                {
                                                    removePost(swagTubeDataList.get(position).getId(), "Suspicious or Spam", holder.getAdapterPosition());
                                                    break;
                                                }
                                                case 2: // camel
                                                {
                                                    removePost(swagTubeDataList.get(position).getId(), "Abusive or Obscene", holder.getAdapterPosition());
                                                    break;
                                                }
                                                case 3: // sheep
                                                {
                                                    removePost(swagTubeDataList.get(position).getId(), "Posted Multiple Times", holder.getAdapterPosition());
                                                    break;
                                                }
                                                case 4: // sheep
                                                {
                                                    removePost(swagTubeDataList.get(position).getId(), "Fake news", holder.getAdapterPosition());
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
//                                OwnerGlobal.toast((Activity) context, "Not Interested");
                                    int sdk = android.os.Build.VERSION.SDK_INT;
                                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        clipboard.setText("text to clip");
                                    } else {
                                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                        android.content.ClipData clip = android.content.ClipData.newPlainText("copy", swagTubeDataList.get(position).getVideourl());
                                        clipboard.setPrimaryClip(clip);
                                    }
                                    break;
                                }
                                case R.id.hide_post: {
//                                OwnerGlobal.toast((Activity) context, "Not Interested");
                                    hidePost(swagTubeDataList.get(position).getId(), "Hide Post", holder.getAdapterPosition());
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

        holder.swagTube_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    if (swagTubeDataList.get(position).getUserlikestatus() == 1) {
                        swagTubeDataList.get(position).setUserlikestatus(0);
                        swagTubeDataList.get(position).setLikecount(swagTubeDataList.get(position).getLikecount() - 1);
                        holder.swagTubeLikeCount.setText("" + swagTubeDataList.get(position).getLikecount());
                        holder.swagTube_like.setTextColor(Color.parseColor("#000000"));
                        holder.swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                        swagTubeLike(swagTubeDataList.get(position).getId());
                    } else if (swagTubeDataList.get(position).getUserlikestatus() == 0) {
                        swagTubeDataList.get(position).setUserlikestatus(1);
                        swagTubeDataList.get(position).setLikecount(swagTubeDataList.get(position).getLikecount() + 1);
                        holder.swagTube_like.setTextColor(Color.parseColor("#025FA4"));
                        holder.swagTube_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unlike, 0, 0, 0);
                        holder.swagTubeLikeCount.setText("" + swagTubeDataList.get(position).getLikecount());
                        swagTubeLike(swagTubeDataList.get(position).getId());
                    }
                }
            }
        });

        holder.swagTube_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    context.startActivity(new Intent(context, SwagTubeCommentActivity.class).putExtra(SwagTubeAdapter.class.getSimpleName(), (Serializable) swagTubeDataList.get(position)));
                }
            }
        });

       /* holder.swagTube_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swagTube_follow.setText("Unfollow");
                swagTubeFollow(swagTubeDataList.get(position).getId());
            }
        });*/

    }

    private void swagTubeLike(String postId) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeLike(postId, PrefConnect.readString(context, Constants.USERID, ""));
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
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }

    private void removePost(String id, String reason, int adapterPosition) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeReportPost(id, PrefConnect.readString(context, Constants.USERID, ""), reason);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            swagTubeDataList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, swagTubeDataList.size());
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

    private void hidePost(String id, String fake_news, int adapterPosition) {
        // OwnerGlobal.toast((Activity) context, fake_news);
        swagTubeDataList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, swagTubeDataList.size());
    }

    @Override
    public int getItemCount() {
        return swagTubeDataList == null ? 0 : swagTubeDataList.size();
    }

    public class SwagTubeViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView postImage;
        CircleImageView swagTubePic;
        AppCompatTextView swagTube_follow, swagTubeName, swagTubeDays, swagTubeCommentCount, swagTubeOptions, swagTube_share, swagTube_comment, swagTube_like, swagTubeLikeCount;
        LinearLayout main_social_layout_linear;
        RelativeLayout main_count_layout;

        public SwagTubeViewHolder(@NonNull View itemView) {
            super(itemView);
//            postImage = itemView.findViewById(R.id.postImage);
            swagTubePic = itemView.findViewById(R.id.swagTubePic);
            swagTubeName = itemView.findViewById(R.id.swagTubeName);
            swagTubeDays = itemView.findViewById(R.id.swagTubeDays);
            swagTubeLikeCount = itemView.findViewById(R.id.swagTubeLikeCount);
            swagTubeCommentCount = itemView.findViewById(R.id.swagTubeCommentCount);
            swagTube_share = itemView.findViewById(R.id.swagTube_share);
            swagTube_comment = itemView.findViewById(R.id.swagTube_comment);
            swagTube_like = itemView.findViewById(R.id.swagTube_like);
            swagTubeOptions = itemView.findViewById(R.id.swagTubeOptions);
            swagTube_follow = itemView.findViewById(R.id.swagTube_follow);
            main_social_layout_linear = itemView.findViewById(R.id.main_social_layout_linear);
            main_count_layout = itemView.findViewById(R.id.main_count_layout);
        }
    }
}
