package com.app.swagse.LiveStreaming.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.swagse.LiveStreaming.Adapter_ClickListener;
import com.app.swagse.LiveStreaming.Live_Comment_Model;
import com.app.swagse.LiveStreaming.stats.LocalStatsData;
import com.app.swagse.LiveStreaming.stats.RemoteStatsData;
import com.app.swagse.LiveStreaming.stats.StatsData;
import com.app.swagse.LiveStreaming.ui.VideoGridContainer;
import com.app.swagse.LiveStreaming.utils.KeyboardHeightObserver;
import com.app.swagse.LiveStreaming.utils.KeyboardHeightProvider;
import com.app.swagse.R;
import com.app.swagse.SimpleClasses.ApiRequest;
import com.app.swagse.SimpleClasses.Functions;
import com.app.swagse.SimpleClasses.Variables;
import com.app.swagse.activity.FollowerUserActivity;
import com.app.swagse.activity.menu.ProfileActivity;
import com.app.swagse.adapter.Live_Comments_Adapter;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class LiveActivity extends RtcBaseActivity implements View.OnClickListener, KeyboardHeightObserver {
    private static final String TAG = LiveActivity.class.getSimpleName();

    private VideoGridContainer mVideoGridContainer;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;

    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;

    DatabaseReference rootref;

    String user_id,user_name,user_picture;
    int user_role;
    EditText message_edit;
    private KeyboardHeightProvider keyboardHeightProvider;
    RelativeLayout write_layout;
    Context context;

    String giftType="";
    AlertDialog alertDialog=null;
    int count=0;
    int countRemove=0;
    ArrayList<String> arrayListOtherUsers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);

        context=LiveActivity.this;
        rootref= FirebaseDatabase.getInstance().getReference();
        initM();
        Intent bundle=getIntent();
        if(bundle!=null){
            user_id=bundle.getStringExtra("user_id");
            user_name=bundle.getStringExtra("user_name");
            user_picture=bundle.getStringExtra("user_picture");
            user_role=bundle.getIntExtra("user_role", Constants.CLIENT_ROLE_BROADCASTER);

        }

        Log.d("my_id", PrefConnect.readString(LiveActivity.this, com.app.swagse.constants.Constants.USERID, "")+" " + user_id);

        if(user_role== Constants.CLIENT_ROLE_BROADCASTER){

            Add_firebase_node();
            findViewById(R.id.live_btn_mute_video).setOnClickListener(this);

            count=-1;
//            listener_node("add");
            //StartTimer();
            StartTimerLiveStream();
        }else {
  //          listener_node("add");
        }

        TextView live_user_name=findViewById(R.id.live_user_name);
        live_user_name.setText(user_name);

        initUI();
        initData();

        message_edit=findViewById(R.id.message_edit);
        write_layout=findViewById(R.id.write_layout);
        keyboardHeightProvider = new KeyboardHeightProvider(this);

        findViewById(R.id.send_btn).setOnClickListener(this);

        findViewById(R.id.live_activity).post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });

        Get_Comment_Data();
    }

    private void initM() {
       // totalViewers=findViewById(R.id.totalViewers);
    }

    private void initUI() {
        
        initUserIcon();

       boolean isBroadcaster =  (user_role == Constants.CLIENT_ROLE_BROADCASTER);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(isBroadcaster);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(isBroadcaster);

        ImageView beautyBtn = findViewById(R.id.live_btn_beautification);
        beautyBtn.setActivated(true);
        rtcEngine().setBeautyEffectOptions(beautyBtn.isActivated(),
                com.app.swagse.LiveStreaming.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(user_role);
        if (isBroadcaster) startBroadcast();
    }

    private void initUserIcon() {
        CircleImageView iconView = findViewById(R.id.live_name_board_icon);
        if(user_picture!=null && !user_picture.equals("")) {
            Uri uri = Uri.parse(user_picture);
            iconView.setImageURI(uri);
        }
    }

    private void initData() {
        mVideoDimension = com.app.swagse.LiveStreaming.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
        mMuteAudioBtn.setActivated(true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
        mMuteAudioBtn.setActivated(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    private void openAlertBox(){

        alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Exit")
                .setMessage("Exiting will end Live Streaming")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        alertDialog.cancel();
                        //Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                    }
                })
                .show();

        Button n = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button p = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if(n != null) {
            n.setTextColor(ContextCompat.getColor(LiveActivity.this,R.color.red));
//            n.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_style_2));
        }

        if(p != null) {
            p.setTextColor(ContextCompat.getColor(LiveActivity.this,R.color.black));
            //b.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_button));
        }
    }
    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    public void onLeaveClicked(View view) {
        openAlertBox();
        //finish();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine().switchCamera();
    }

    public void onBeautyClicked(View view) {
        view.setActivated(!view.isActivated());
        rtcEngine().setBeautyEffectOptions(view.isActivated(),
                com.app.swagse.LiveStreaming.Constants.DEFAULT_BEAUTY_OPTIONS);
    }



    public void onMuteAudioClicked(View view) {
        if (!mMuteVideoBtn.isActivated()) return;

        rtcEngine().muteLocalAudioStream(view.isActivated());
        view.setActivated(!view.isActivated());
    }



    @Override
    public void onBackPressed() {
        openAlertBox();
        //finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(user_role== Constants.CLIENT_ROLE_BROADCASTER){
          Remove_node();
        }else {
           // remove_listener();
        }
        keyboardHeightProvider.close();

        remove_comment_listener();

        Stop_timer();
        Stop_LiveStreamStatus_timer();
    }

    public void Add_firebase_node(){
        HashMap map=new HashMap();
        map.put("user_id",user_id);
        map.put("user_name",user_name);
        map.put("user_picture",user_picture);
        map.put("total_users","0");
        rootref.child("LiveUsers").child(user_id).setValue(map);
    }

    public void Remove_node(){
        rootref.child("LiveUsers").child(user_id).removeValue();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.live_btn_mute_video:
                if (view.isActivated()) {
                    stopBroadcast();
                } else {
                    startBroadcast();
                }
                view.setActivated(!view.isActivated());
                break;

            case R.id.send_btn:
                if(!TextUtils.isEmpty(message_edit.getText().toString())){
                    Add_Messages();
                  }
                break;
        }
    }


    ArrayList<Object> data_list;
    RecyclerView recyclerView;
    Live_Comments_Adapter adapter;
    public void init_adapter(){
        data_list=new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        adapter=new Live_Comments_Adapter(this, data_list, new Adapter_ClickListener() {
            @Override
            public void On_Item_Click(int postion, Object Model, View view) {

            }

            @Override
            public void On_Long_Item_Click(int postion, Object Model, View view) {

            }
        });

        recyclerView.setAdapter(adapter);

    }

    public void Add_Messages(){

        DatabaseReference dref = rootref.child("LiveUsers").child(user_id).child("Chat").push();

        final String key=dref.getKey();
        String my_id = PrefConnect.readString(LiveActivity.this, com.app.swagse.constants.Constants.USERID, "");
        String my_name = PrefConnect.readString(LiveActivity.this, com.app.swagse.constants.Constants.USERNAME, "");
        String my_image = PrefConnect.readString(LiveActivity.this, com.app.swagse.constants.Constants.USERPIC, "");

        HashMap message_user_map = new HashMap<>();
        message_user_map.put("id",key);
        message_user_map.put("user_id", my_id);
        message_user_map.put("user_name", my_name);
        message_user_map.put("user_picture", my_image);
        message_user_map.put("comment",message_edit.getText().toString());

        rootref.child("LiveUsers").child(user_id).child("Chat").child(key).setValue(message_user_map);

        message_edit.setText(null);

    }

    ChildEventListener childEventListener;
    public void Get_Comment_Data(){
        init_adapter();

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Live_Comment_Model model = dataSnapshot.getValue(Live_Comment_Model.class);
                data_list.add(model);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(data_list.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rootref.child("LiveUsers").child(user_id).child("Chat").addChildEventListener(childEventListener);
    }

    public void remove_comment_listener(){
        if(rootref!=null && childEventListener!=null)
            rootref.child("LiveUsers").child(user_id).child("Chat").removeEventListener(childEventListener);

    }



    CountDownTimer countDownTimer;
/*
    public void StartTimer(){
        countDownTimer= new CountDownTimer(Variables.max_streming_time, 1000) {

            public void onTick(long millisUntilFinished) {
                int streaming=SharedPrefrence.get_int(LiveActivity.this,SharedPrefrence.streaming_used_time);
                SharedPrefrence.save_int(LiveActivity.this,streaming+1000,SharedPrefrence.streaming_used_time);

                if(streaming>Variables.max_streming_time){
                    finish();
                }

            }

            public void onFinish() {
               finish();
            }
        }.start();
    }
*/

    public void Stop_timer(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    public void Stop_LiveStreamStatus_timer(){
        if(countDownTimerForLiveStatus!=null){
            countDownTimerForLiveStatus.cancel();
        }
    }

    CountDownTimer countDownTimerForLiveStatus;
    public void StartTimerLiveStream(){
        countDownTimerForLiveStatus= new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                saveLiveStatus();
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }

    private void saveLiveStatus() {
       /* JSONObject parameters = new JSONObject();
        try {

            String user_id = SharedPrefrence.get_string(context,SharedPrefrence.u_id);
            parameters.put("fb_id", user_id);
            parameters.put("streamer", "1");
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Api_Links.save_last_login, parameters, new CallBack() {
            @Override
            public void Get_Response(String requestType, String response) {
                Functions.cancel_loader();
                try {
                    Log.d("lastLoginResponse",response);
                    JSONObject jsonObject=new JSONObject(response);
                    String code=jsonObject.getString("code");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Functions.cancel_loader();
                }

            }
        } );*/
    }


    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);

        if(user_role== Constants.CLIENT_ROLE_BROADCASTER){
            Remove_node();
        }else {
           // remove_listener();
        }
        keyboardHeightProvider.close();

        remove_comment_listener();

        Stop_timer();
        Stop_LiveStreamStatus_timer();
        finish();
    }


    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(write_layout.getWidth(), write_layout.getHeight());
        params.bottomMargin = height;
        write_layout.setLayoutParams(params);
    }
}
