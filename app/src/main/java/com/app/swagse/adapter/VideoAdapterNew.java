package com.app.swagse.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.activity.MakeVideoActivity;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.activity.SwaggerCommentActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.swaggerData.SwaggerdataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
//import com.banuba.sdk.ve.flow.VideoCreationActivity;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PUT;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class VideoAdapterNew extends RecyclerView.Adapter<VideoAdapterNew.VideoViewHolder> {

    private static final String TAG = VideoAdapterNew.class.getSimpleName();
    private List<SwaggerdataItem> mVideoItems;
    Context context;
    boolean flag = false;
    boolean volumeFlag = false;
    Api apiInterface;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;

    public VideoAdapterNew(Context context, List<SwaggerdataItem> mVideoItems) {
        this.mVideoItems = mVideoItems;
        this.context = context;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoAdapterNew.VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(mVideoItems.get(position));
        if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
            holder.album_view.setVisibility(View.GONE);
        } else {
            holder.album_view.setVisibility(View.VISIBLE);
        }

        Glide.with(context).load(PrefConnect.readString(context, Constants.USERPIC, "")).placeholder(R.drawable.ic_user).into(holder.userImage);

        holder.image_view_option_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.like_selector));
//                holder.image_view_option_like_title.setText();
                if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    if (mVideoItems.get(position).getUserlikestatus() == 1) {
                        mVideoItems.get(position).setUserlikestatus(0);
                        mVideoItems.get(position).setLikecount(mVideoItems.get(position).getLikecount() - 1);
                        holder.image_view_option_like_title.setText("" + mVideoItems.get(position).getLikecount());
                        holder.image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_unfilled));
                        swaggerLike(mVideoItems.get(position).getId());
                    } else if (mVideoItems.get(position).getUserlikestatus() == 0) {
                        mVideoItems.get(position).setUserlikestatus(1);
                        mVideoItems.get(position).setLikecount(mVideoItems.get(position).getLikecount() + 1);
                        holder.image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.like_selector));
                        holder.image_view_option_like_title.setText("" + mVideoItems.get(position).getLikecount());
                        swaggerLike(mVideoItems.get(position).getId());
                    }
                }
            }
        });

        holder.image_view_option_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    context.startActivity(new Intent(context, SwaggerCommentActivity.class).putExtra(VideoAdapterNew.class.getSimpleName(), (Serializable) mVideoItems.get(position).getCommentdata()).putExtra("id", mVideoItems.get(position).getId()));
                }
            }
        });

        holder.image_view_option_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hii");
                    String shareMessage = "\nHi! I found this Post on SwagSe App - check it out now \n\n" + Constants.BASE_URL + "watch/" + OwnerGlobal.getMd5(mVideoItems.get(position).getId());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, "Send To"));
                } catch (Exception e) {
                    e.toString();
                }
            }
        });

        holder.album_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(context).inflate(R.layout.swagger_menu_layout, null);
                AppCompatTextView reportVideo = view1.findViewById(R.id.reportVideo);
                AppCompatTextView copy_video_url = view1.findViewById(R.id.copy_video_url);
                final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView(view1);
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                mBottomSheetDialog.show();

                reportVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
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
                                        removePost(mVideoItems.get(position).getId(), "Not Relevant to SwagSe", holder.getAdapterPosition());
                                        break;
                                    }
                                    case 1: // cow
                                    {
                                        removePost(mVideoItems.get(position).getId(), "Suspicious or Spam", holder.getAdapterPosition());
                                        break;
                                    }
                                    case 2: // camel
                                    {
                                        removePost(mVideoItems.get(position).getId(), "Abusive or Obscene", holder.getAdapterPosition());
                                        break;
                                    }
                                    case 3: // sheep
                                    {
                                        removePost(mVideoItems.get(position).getId(), "Posted Multiple Times", holder.getAdapterPosition());
                                        break;
                                    }
                                    case 4: // sheep
                                    {
                                        removePost(mVideoItems.get(position).getId(), "Fake news", holder.getAdapterPosition());
                                        break;
                                    }
                                }
                            }
                        });

                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

                copy_video_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText("text to clip");
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("copy", mVideoItems.get(position).getVideourl());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                });

            }
        });

        holder.container_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefConnect.readBoolean(context, Constants.GUEST_USER, false)) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    if (mVideoItems.get(position).getUserfollowstatus() == 1) {
                        mVideoItems.get(position).setUserfollowstatus(0);
                        holder.image_view_follow_option.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_follow_avatar_bottom_icon));
                    } else if (mVideoItems.get(position).getUserfollowstatus() == 0) {
                        mVideoItems.get(position).setUserfollowstatus(1);
                        holder.image_view_follow_option.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));
                    }
                    swagTubeFollow(mVideoItems.get(0).getUserid());
                }
            }
        });

    }

    private void swagTubeFollow(String postId) {
        if (App.isOnline()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeFollow(postId, PrefConnect.readString(context, Constants.USERID, ""), "");
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            OwnerGlobal.toast((Activity) context, response.body().getMessage());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast((Activity) context, jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect((Activity) context);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void removePost(String videoId, String fake_news, int adapterPosition) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swaggerReport(videoId, PrefConnect.readString(context, Constants.USERID, ""), fake_news);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            mVideoItems.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, mVideoItems.size());
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


    private void swaggerLike(String postId) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swaggerLike(postId, PrefConnect.readString(context, Constants.USERID, ""));
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

    @Override
    public int getItemCount() {
        return mVideoItems == null ? 0 : mVideoItems.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView mVideoView;
        TextView txtTitle, txtDesc, createVideo;
        ProgressBar mProgressBar;
        AppCompatImageView image_view_option_like, image_view_option_share, image_view_option_comment, image_view_follow_option;
        AppCompatTextView image_view_option_like_title, image_view_option_share_title, image_view_option_comment_title;
        ConstraintLayout container;
        AppCompatImageView album_view;
        ImageView ivVolumeControl, volumeControl;
        CircleImageView userImage;
        ConstraintLayout container_profile;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.videoView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            userImage = itemView.findViewById(R.id.userImage);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            container = itemView.findViewById(R.id.container);
            ivVolumeControl = itemView.findViewById(R.id.ivVolumeControl);
            volumeControl = itemView.findViewById(R.id.muteButton);
            createVideo = itemView.findViewById(R.id.createVideo);
            image_view_option_share = itemView.findViewById(R.id.image_view_option_share);
            image_view_option_comment = itemView.findViewById(R.id.image_view_option_comment);
            image_view_option_like_title = itemView.findViewById(R.id.image_view_option_like_title);
            image_view_option_like = itemView.findViewById(R.id.image_view_option_like);
            image_view_option_comment_title = itemView.findViewById(R.id.image_view_option_comment_title);
            image_view_option_share_title = itemView.findViewById(R.id.image_view_option_share_title);
            album_view = itemView.findViewById(R.id.album_view);
            container_profile = itemView.findViewById(R.id.container_profile);
            image_view_follow_option = itemView.findViewById(R.id.image_view_follow_option);
        }

        void setVideoData(SwaggerdataItem videoItem) {
            flag = false;
            txtTitle.setText(videoItem.getTitle());
            txtDesc.setText(videoItem.getId());
            mVideoView.setVideoPath(videoItem.getVideourl());

            if (videoItem.getUserlikestatus() == 1) {
                image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.like_selector));
            } else {
                image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_unfilled));
            }

            image_view_option_like_title.setText("" + videoItem.getLikecount());
            image_view_option_comment_title.setText("" + videoItem.getCommentcount());
            image_view_option_share_title.setText("0");

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mProgressBar.setVisibility(View.GONE);
                    mp.start();

                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = mVideoView.getWidth() / (float) mVideoView.getHeight();
                    float scale = videoRatio / screenRatio;
                    if (scale >= 1f) {
                        mVideoView.setScaleX(scale);
                    } else {
                        mVideoView.setScaleY(1f / scale);
                    }
                }
            });
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });


            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flag == true) {
                        flag = false;
                        ivVolumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_24));
                        mVideoView.start();
                        ivVolumeControl.animate().cancel();
                        ivVolumeControl.setAlpha(1f);
                        ivVolumeControl.animate()
                                .alpha(0f)
                                .setDuration(1000).setStartDelay(2000);
                    } else {
                        flag = true;
                        ivVolumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_circle_filled_24));
                        mVideoView.pause();
                        ivVolumeControl.animate().cancel();
                        ivVolumeControl.setAlpha(1f);
                        ivVolumeControl.animate()
                                .alpha(0f)
                                .setDuration(1000).setStartDelay(2000);
                    }

                }
            });

            volumeControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (volumeFlag == false) {
                        mute();
                        volumeFlag = true;
                        volumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_volume_off));
                        volumeControl.animate().cancel();
                        volumeControl.setAlpha(1f);
                        volumeControl.animate()
                                .alpha(0f)
                                .setDuration(1000).setStartDelay(2000);
                    } else if (volumeFlag == true) {
                        unmute();
                        volumeFlag = false;
                        volumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_volume_on));
                        volumeControl.animate().cancel();
                        volumeControl.setAlpha(1f);
                        volumeControl.animate()
                                .alpha(0f)
                                .setDuration(1000).setStartDelay(2000);
                    }
                }
            });

            createVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, MakeVideoActivity.class));
                }
            });

        }
    }

    public void mute() {
        this.setVolume(0);
    }

    public void unmute() {
        this.setVolume(100);
    }

    private void setVolume(int amount) {
        final int max = 100;
        final double numerator = max - amount > 0 ? Math.log(max - amount) : 0;
        final float volume = (float) (1 - (numerator / Math.log(max)));

        this.mediaPlayer.setVolume(volume, volume);
    }

}
