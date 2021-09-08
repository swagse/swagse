package com.app.swagse.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.SimpleClasses.Functions;
import com.app.swagse.adapter.FollowVideoRecyclerViewAdapter;
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.adapter.NotificationRecyclerViewAdapter;
import com.app.swagse.adapter.PlayerViewHolder;
import com.app.swagse.adapter.SearchRecyclerViewAdapter;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.adapter.TrendingViewHolder;
import com.app.swagse.constants.Constants;
import com.app.swagse.constants.Variables;
import com.app.swagse.controller.App;
import com.app.swagse.deeplinking.DLinkingActivity;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.swaggerData.SwaggerdataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.receiver.BackgroundNotificationService;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.danikula.videocache.HttpProxyCacheServer;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class SwagTubeDetailsActivity extends AppCompatActivity implements Player.EventListener, View.OnClickListener {

    public static final String PROGRESS_UPDATE = "progress_update";
    private static final String TAG = SwagTubeDetailsActivity.class.getSimpleName();
    String fileN = null;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Dialog downloadDialog;

    SimpleExoPlayer player;
    String postId, url;
    ImageView fullscreenButton;
    ImageButton exo_pause, exo_play;
    boolean fullscreen = false;
    SimpleExoPlayerView simpleExoPlayerView;
    RecyclerView swagDetailRecyclerView;
    private Api apiInterface;
    ProgressBar progressBar;

    AppCompatTextView channelSubscriberViews, swagDetailChannelName, swagDetailViews, swagDetailComment, swagDetailCommentCount, swagDetailSave, swagDetailDownload, swagDetailShare, swagDetailDisLike, swagDetailLike, swagDetailTimeago, swagDetailTitle;
    CircleImageView swagDetailUserPic;
    AppCompatButton swagTubeDetailFollow;
    private List<SwagtubedataItem> swagtubedata;
    RelativeLayout swagTubeDetailLayout;
    ProgressBar swagTubeDetailProgress;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swag_tube_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        apiInterface = RetrofitClient.getInstance().getApi();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        //Initialize simpleExoPlayerView
        simpleExoPlayerView = findViewById(R.id.exoplayer);
        simpleExoPlayerView.setPlayer(player);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(PlayerViewHolder.class.getSimpleName())) {
                postId = intent.getExtras().getString(PlayerViewHolder.class.getSimpleName());
            } else if (intent.hasExtra(NavWatchAdapter.class.getSimpleName())) {
                postId = intent.getExtras().getString(NavWatchAdapter.class.getSimpleName());
            } else if (intent.hasExtra(TrendingViewHolder.class.getSimpleName())) {
                postId = intent.getExtras().getString(TrendingViewHolder.class.getSimpleName());
            } else if (intent.hasExtra(DLinkingActivity.class.getSimpleName())) {
                postId = intent.getExtras().getString(DLinkingActivity.class.getSimpleName());
            } else if (intent.hasExtra(FollowVideoRecyclerViewAdapter.class.getSimpleName())) {
                postId = intent.getExtras().getString(FollowVideoRecyclerViewAdapter.class.getSimpleName());
            } else if (intent.hasExtra(SearchRecyclerViewAdapter.class.getSimpleName())) {
                postId = intent.getExtras().getString(SearchRecyclerViewAdapter.class.getSimpleName());
            }else if (intent.hasExtra(NotificationRecyclerViewAdapter.class.getSimpleName())) {
                postId = intent.getExtras().getString(NotificationRecyclerViewAdapter.class.getSimpleName());
            }
        }

        swagDetailRecyclerView = findViewById(R.id.swagDetailRecyclerView);
        swagDetailRecyclerView.setHasFixedSize(true);
        swagDetailRecyclerView.setLayoutManager(new LinearLayoutManager(SwagTubeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));


        swagTubeDetailProgress = findViewById(R.id.swagTubeDetailProgress);
        swagTubeDetailFollow = findViewById(R.id.swagTubeDetailFollow);
        swagDetailComment = findViewById(R.id.swagDetailComment);
        swagDetailCommentCount = findViewById(R.id.swagDetailCommentCount);
        swagDetailTimeago = findViewById(R.id.swagDetailTimeago);
        swagDetailLike = findViewById(R.id.swagDetailLike);
        swagDetailViews = findViewById(R.id.swagDetailViews);
        swagDetailDownload = findViewById(R.id.swagDetailDownload);
        swagDetailSave = findViewById(R.id.swagDetailSave);
        swagDetailShare = findViewById(R.id.swagDetailShare);
        swagDetailTitle = findViewById(R.id.swagDetailTitle);
        swagDetailUserPic = findViewById(R.id.swagDetailUserPic);
        swagDetailViews = findViewById(R.id.swagDetailViews);
        swagDetailChannelName = findViewById(R.id.swagDetailChannelName);
        channelSubscriberViews = findViewById(R.id.channelSubscriberViews);

        swagDetailComment.setOnClickListener(this);
        swagDetailLike.setOnClickListener(this);
        //   swagDetailDisLike.setOnClickListener(this);
        swagDetailShare.setOnClickListener(this);
        swagDetailDownload.setOnClickListener(this);
        swagDetailSave.setOnClickListener(this);
        swagTubeDetailFollow.setOnClickListener(this);


        swagTubeDataByID();

        result = checkPermission();
        if (result) {
            checkFolder();
        }


    }

    private void initializePlayer() {
        progressBar = findViewById(R.id.progressBar);
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(url);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        player.prepare(videoSource);
        simpleExoPlayerView.setPlayer(player);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
        swagTubeDetailLayout = findViewById(R.id.swagTubeDetailLayout);
        swagTubeDetailLayout.setVisibility(View.VISIBLE);
        swagTubeDetailProgress.setVisibility(View.GONE);

        fullscreenButton = findViewById(R.id.exo_fullscreen_icon);
        exo_pause = findViewById(R.id.exo_pause);
        exo_play = findViewById(R.id.exo_play);

        exo_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPlayer(player);
            }
        });
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullscreen) {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(SwagTubeDetailsActivity.this, R.drawable.ic_fullscreen_open));

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (250 * getApplicationContext().getResources().getDisplayMetrics().density);
                    simpleExoPlayerView.setLayoutParams(params);

                    fullscreen = false;
                } else {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(SwagTubeDetailsActivity.this, R.drawable.ic_fullscreen_close));

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    simpleExoPlayerView.setLayoutParams(params);

                    fullscreen = true;
                }
            }
        });

        exo_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPlayer(player);
            }
        });

        exo_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlayer(player);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //initializePlayer();
    }

    private void pausePlayer(SimpleExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void playPlayer(SimpleExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    private void stopPlayer() {
        simpleExoPlayerView.setPlayer(null);
        player.release();
        player = null;
    }

    private void seekTo(SimpleExoPlayer player, long positionInMS) {
        if (player != null) {
            player.seekTo(positionInMS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playPlayer(player);
        player.getPlaybackState();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "onPause: ");
        if (player != null) {
            player.setPlayWhenReady(false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    public void getSwagTubeData(String category) {
        if (App.isOnline()) {
            Call<SwagTubeResponse> userResponseCall = apiInterface.getTrendingCategoryData(PrefConnect.readString(SwagTubeDetailsActivity.this, Constants.USERID, ""), category);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                SwagTubeAdapter swagTubeAdapter = new SwagTubeAdapter(SwagTubeDetailsActivity.this, swagTubeDataList);
                                swagDetailRecyclerView.setAdapter(swagTubeAdapter);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwagTubeDetailsActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeDetailsActivity.this);
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
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY) {
            Log.i("TEST", "ExoPlayer State is: READY");
            progressBar.setVisibility(View.GONE);
        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            Log.i("TEST", "ExoPlayer State is: BUFFERING");
            progressBar.setVisibility(View.VISIBLE);
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            Log.i("TEST", "ExoPlayer State is: ENDED");
        } else if (playbackState == ExoPlayer.STATE_IDLE) {
            Log.i("TEST", "ExoPlayer State is: IDLE");
        }
    }

    public void swagTubeDataByID() {
        if (App.isOnline()) {
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeVideoDetails(PrefConnect.readString(SwagTubeDetailsActivity.this, Constants.USERID, ""), postId);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            swagtubedata = response.body().getSwagtubedata();
                            if (swagtubedata != null) {
                                setUIData(swagtubedata);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwagTubeDetailsActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeDetailsActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }

    private void setUIData(List<SwagtubedataItem> swagtubedata) {
        getSwagTubeData(swagtubedata.get(0).getCategory());
        url = swagtubedata.get(0).getVideourl();
        Log.d(TAG, "setUIData: " + url);
        swagDetailTitle.setText(swagtubedata.get(0).getTitle());
        swagDetailTimeago.setText(swagtubedata.get(0).getTimeago());
        if (swagtubedata.get(0).getViewscount() != 0) {
            swagDetailViews.setText(swagtubedata.get(0).getViewscount() + " views");
        } else {
            swagDetailViews.setVisibility(View.GONE);
        }

        urlString = swagtubedata.get(0).getVideourl();
        swagDetailChannelName.setText(swagtubedata.get(0).getName());
        channelSubscriberViews.setText("" + swagtubedata.get(0).getViewscount() + " views");
        swagDetailCommentCount.setText("Comments " + swagtubedata.get(0).getCommentcount());
        swagDetailLike.setText("" + swagtubedata.get(0).getLikecount());
        swagDetailShare.setText("" + swagtubedata.get(0).getLikecount());
        swagDetailDownload.setText("" + swagtubedata.get(0).getLikecount());
        swagDetailSave.setText("" + swagtubedata.get(0).getLikecount());

        if (swagtubedata.get(0).getUserfollowstatus() == 0) {
            swagTubeDetailFollow.setText("Follow");
            swagTubeDetailFollow.setBackground(getResources().getDrawable(R.drawable.button_design));
        } else {
            swagTubeDetailFollow.setText("Un-Follow");
            swagTubeDetailFollow.setBackground(getResources().getDrawable(R.drawable.button_design_gray));
        }

        if (swagtubedata.get(0).getUserlikestatus() == 1) {
            swagDetailLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unlike, 0, 0);
        } else {
            swagDetailLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.like, 0, 0);
        }

        initializePlayer();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swagDetailComment: {
                if (PrefConnect.readBoolean(SwagTubeDetailsActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, SwagTubeCommentActivity.class).putExtra(SwagTubeDetailsActivity.class.getSimpleName(), (Serializable) swagtubedata.get(0).getCommentdata()));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            }
            case R.id.swagDetailLike: {
                if (PrefConnect.readBoolean(SwagTubeDetailsActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (swagtubedata.get(0).getUserlikestatus() == 1) {
                        swagtubedata.get(0).setUserlikestatus(0);
                        swagtubedata.get(0).setLikecount(swagtubedata.get(0).getLikecount() - 1);
                        swagDetailLike.setText("" + swagtubedata.get(0).getLikecount());
                        swagDetailLike.setTextColor(Color.parseColor("#000000"));
                        swagDetailLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.like, 0, 0);
                        swagTubeLike(swagtubedata.get(0).getId());
                    } else if (swagtubedata.get(0).getUserlikestatus() == 0) {
                        swagtubedata.get(0).setUserlikestatus(1);
                        swagtubedata.get(0).setLikecount(swagtubedata.get(0).getLikecount() + 1);
                        swagDetailLike.setTextColor(Color.parseColor("#025FA4"));
                        swagDetailLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unlike, 0, 0);
                        swagDetailLike.setText("" + swagtubedata.get(0).getLikecount());
                        swagTubeLike(swagtubedata.get(0).getId());
                    }
                }
                break;
            }
            case R.id.swagDetailShare: {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hii");
                    String shareMessage = "\nHi! I found this Post on SwagSe App - check it out now \n\n " + Constants.BASE_URL + "watch/" + OwnerGlobal.getMd5(postId);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Send To"));
                } catch (Exception e) {
                    e.toString();
                }
                break;
            }
            case R.id.swagDetailDownload: {
                if (PrefConnect.readBoolean(SwagTubeDetailsActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
//                    OwnerGlobal.toast(SwagTubeDetailsActivity.this, "Work In Progress");
                    Log.d(TAG, "onClick: " + urlString);
//                    newDownload(urlString);
                    downloadVideo(urlString);
                }
                break;
            }
            case R.id.swagDetailSave: {

//                HttpProxyCacheServer proxy = App.getProxy(SwagTubeDetailsActivity.this);
//                String proxyUrl = proxy.getProxyUrl(swagtubedata.get(0).getVideourl());
//                videoView.setVideoPath(proxyUrl);
//                cachingUrl(swagtubedata.get(0).getVideourl());
//                HttpProxyCacheServer proxy = App.getProxy(SwagTubeDetailsActivity.this);
//                String proxyUrl = proxy.getProxyUrl(swagtubedata.get(0).getVideourl());
                if (PrefConnect.readBoolean(SwagTubeDetailsActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    toast(SwagTubeDetailsActivity.this, "Video Saved in Cache Memory.");
                    HttpProxyCacheServer proxy = App.getProxy(SwagTubeDetailsActivity.this);
                    String proxyUrl = proxy.getProxyUrl(swagtubedata.get(0).getVideourl());
                    Log.d(TAG, "onClick: " + proxyUrl);
                    cachingUrl(proxyUrl);
                }

//              videoView.setVideoPath(proxyUrl);
                break;
            }
            case R.id.swagTubeDetailFollow: {
                if (PrefConnect.readBoolean(SwagTubeDetailsActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(SwagTubeDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    swagTubeFollow(swagtubedata.get(0).getUserid());
                }
                break;
            }
        }
    }

    private void downloadVideo(String urlString) {
        Functions.show_determinent_loader(this, false, false);
        PRDownloader.initialize(this);
        DownloadRequest prDownloader = PRDownloader.download(url, Variables.app_folder, swagtubedata.get(0).getId() + "no_watermark" + ".mp4")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        Functions.show_loading_progress(prog);
                    }
                });


        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Functions.cancel_determinent_loader();
                Toast.makeText(SwagTubeDetailsActivity.this, "Video downloaded", Toast.LENGTH_SHORT).show();
//                scan_file(swagtubedata.get(0));
//                applywatermark(item);
            }

            @Override
            public void onError(Error error) {
//                delete_file_no_watermark(item);
                Toast.makeText(SwagTubeDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Functions.cancel_determinent_loader();
            }


        });

    }

    public void scan_file(SwagtubedataItem item) {
        MediaScannerConnection.scanFile(this,
                new String[]{Variables.app_folder + item.getId() + ".mp4"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Toast.makeText(SwagTubeDetailsActivity.this, "Video Downloaded", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public String cachingUrl(String urlPath) {
        return App.getProxy(this).getProxyUrl(urlPath, true);
    }

    private void swagTubeFollow(String postId) {
        if (App.isOnline()) {
            progressDialog.show();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeFollow(postId, PrefConnect.readString(SwagTubeDetailsActivity.this, Constants.USERID, ""), "");
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            OwnerGlobal.toast(SwagTubeDetailsActivity.this, response.body().getMessage());
                            if (swagtubedata.get(0).getUserfollowstatus() == 0) {
                                swagTubeDetailFollow.setText("Un-Follow");
                                swagTubeDetailFollow.setBackground(getResources().getDrawable(R.drawable.button_design_gray));
                            } else {
                                swagTubeDetailFollow.setText("Follow");
                                swagTubeDetailFollow.setBackground(getResources().getDrawable(R.drawable.button_design));
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwagTubeDetailsActivity.this, jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeDetailsActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void swagTubeSave(String postId) {
        if (App.isOnline()) {
            progressDialog.show();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeSaveVideo(PrefConnect.readString(SwagTubeDetailsActivity.this, Constants.USERID, ""), postId);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            OwnerGlobal.toast(SwagTubeDetailsActivity.this, response.body().getMessage());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwagTubeDetailsActivity.this, jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeDetailsActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void swagTubeLike(String postId) {
        if (App.isOnline()) {
            apiInterface = RetrofitClient.getInstance().getApi();
            Call<SwagTubeResponse> userResponseCall = apiInterface.swagTubeLike(postId, PrefConnect.readString(SwagTubeDetailsActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {

                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwagTubeDetailsActivity.this, jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeDetailsActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }

    public class DownloadTask {
        private static final String TAG = "Download Task";
        private Context context;

        private String downloadUrl = "", downloadFileName = "";
        private ProgressDialog progressDialog;

        public DownloadTask(Context context, String downloadUrl) {
            this.context = context;

            this.downloadUrl = downloadUrl;


            downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), downloadUrl.length());//Create file name by picking download file name from URL
            Log.e(TAG, downloadFileName);

            //Start Downloading Task
            new DownloadingTask().execute();
        }

        private class DownloadingTask extends AsyncTask<Void, Void, Void> {

            File apkStorage = null;
            File outputFile = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Downloading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    if (outputFile != null) {
                        progressDialog.dismiss();
                        ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog_Alert);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                        alertDialogBuilder.setTitle("Video  ");
                        alertDialogBuilder.setMessage("Video Downloaded Successfully ");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        alertDialogBuilder.setNegativeButton("Open report", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/SwagSe/" + downloadFileName);  // -> filename = maven.pdf
                                Uri path = Uri.fromFile(pdfFile);
                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                pdfIntent.setDataAndType(path, "application/pdf");
                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                try {
                                    context.startActivity(pdfIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDialogBuilder.show();
//                    Toast.makeText(context, "Document Downloaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);

                        Log.e(TAG, "Download Failed");

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    //Change button text if exception occurs

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);
                    Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

                }


                super.onPostExecute(result);
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    URL url = new URL(downloadUrl);//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                    c.connect();//connect the URL Connection

                    //If Connection response is not OK then show Logs
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage());

                    }


                    //Get File if SD card is present
                    if (new CheckForSDCard().isSDCardPresent()) {

                        apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "SwagSe");
                    } else
                        Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                    //If File is not present create directory
                    if (!apkStorage.exists()) {
                        apkStorage.mkdir();
                        Log.e(TAG, "Directory Created.");
                    }

                    outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                    //Create New File if not present
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                        Log.e(TAG, "File Created");
                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                    InputStream is = c.getInputStream();//Get InputStream for connection

                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);//Write new file
                    }

                    //Close all connection after doing task
                    fos.close();
                    is.close();

                } catch (Exception e) {

                    //Read exception if something went wrong
                    e.printStackTrace();
                    outputFile = null;
                    Log.e(TAG, "Download Error Exception " + e.getMessage());
                }

                return null;
            }
        }
    }

    //hare you can start downloding video
    public void newDownload(String url) {
        new DownloadTask(SwagTubeDetailsActivity.this, url);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SwagTubeDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SwagTubeDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SwagTubeDetailsActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SwagTubeDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(SwagTubeDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SwagTubeDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SwagTubeDetailsActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SwagTubeDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(SwagTubeDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }


    //Here you can check App Permission 
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    //hare you can check folfer whare you want to store download Video
    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SwagSe/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }

    public class CheckForSDCard {
        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }
    }

}

