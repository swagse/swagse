package com.app.swagse.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.MakeVideoActivity;
import com.app.swagse.activity.SwaggerCommentActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.fragment.SwaggerFragment;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private List<VideoItem> mVideoItems;
    Context context;
    boolean flag = false;

    MediaController.MediaPlayerControl mediaPlayerControl;

    public VideosAdapter(Context context, List<VideoItem> videoItems) {
        this.context = context;
        mVideoItems = videoItems;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(mVideoItems.get(position));

        holder.image_view_option_like_title.setText("" + 1);
        holder.image_view_option_comment_title.setText("" + 1);
        holder.image_view_option_share_title.setText("" + 1);

        Glide.with(context).load(PrefConnect.readString(context, Constants.USERPIC, "")).placeholder(R.drawable.ic_user).into(holder.userImage);

        holder.image_view_option_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.image_view_option_like.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.like_selector));
                holder.image_view_option_like_title.setText("" + 1);
            }
        });

        holder.image_view_option_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SwaggerCommentActivity.class));
            }
        });

        holder.image_view_option_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hii");
                    String shareMessage = "\nHi! I found this Post on SwagSe App - check it out now \n\n" + mVideoItems.get(position).videoURL;
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
//                View view = getLayoutInflater().inflate(R.layout.swagger_menu_layout, null);
//                ListView weightListView = view.findViewById(R.id.weight_listView);
//                AppCompatButton selectWeight = view.findViewById(R.id.selectWeight);
//                final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
//                mBottomSheetDialog.setContentView(view);
//                mBottomSheetDialog.setCancelable(true);
//                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//                mBottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoItems == null ? 0 : mVideoItems.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView mVideoView;
        TextView txtTitle, txtDesc, createVideo;
        ProgressBar mProgressBar;
        AppCompatImageView image_view_option_like, image_view_option_share, image_view_option_comment;
        AppCompatTextView image_view_option_like_title, image_view_option_share_title, image_view_option_comment_title,album_view;
        ConstraintLayout container;
        ImageView ivVolumeControl;
        CircleImageView userImage;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.videoView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            userImage = itemView.findViewById(R.id.userImage);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            container = itemView.findViewById(R.id.container);
            ivVolumeControl = itemView.findViewById(R.id.ivVolumeControl);
            createVideo = itemView.findViewById(R.id.createVideo);
            image_view_option_share = itemView.findViewById(R.id.image_view_option_share);
            image_view_option_comment = itemView.findViewById(R.id.image_view_option_comment);
            image_view_option_like_title = itemView.findViewById(R.id.image_view_option_like_title);
            image_view_option_like = itemView.findViewById(R.id.image_view_option_like);
            image_view_option_comment_title = itemView.findViewById(R.id.image_view_option_comment_title);
            image_view_option_share_title = itemView.findViewById(R.id.image_view_option_share_title);
            album_view = itemView.findViewById(R.id.album_view);
        }

        void setVideoData(VideoItem videoItem) {
            flag = false;
            txtTitle.setText(videoItem.videoTitle);
            txtDesc.setText(videoItem.videoDesc);
            mVideoView.setVideoPath(videoItem.videoURL);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
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
                        ivVolumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_filled_24));
                        ivVolumeControl.animate().alpha(1f);
                        mVideoView.start();
//                        ivVolumeControl.animate()
//                                .alpha(0f)
//                                .setDuration(600).setStartDelay(1000);
                        flag = false;
                    } else {
                        flag = true;
                        ivVolumeControl.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_play_circle_filled_24));
                        ivVolumeControl.animate().alpha(1f);
//                        ivVolumeControl.animate()
//                                .alpha(0f)
//                                .setDuration(600).setStartDelay(1000);
                        mVideoView.pause();
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
}
