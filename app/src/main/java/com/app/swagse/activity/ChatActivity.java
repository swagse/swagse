package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.app.swagse.Chat.Chat_Fragment;
import com.app.swagse.R;
import com.app.swagse.SimpleClasses.Fragment_Callback;

public class ChatActivity extends AppCompatActivity {

    String name;
    String userid;
    String user_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /*getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        name=getIntent().getStringExtra("user_name");
        userid=getIntent().getStringExtra("user_id");
        user_pic=getIntent().getStringExtra("user_pic");

        Log.d("user_name",name +userid );
        chatFragment(userid,name,user_pic);
    }

    public void chatFragment(String receiverid, String name, String picture){
        Chat_Fragment chat_activity = new Chat_Fragment(new Fragment_Callback() {
            @Override
            public void response(Bundle bundle) {

            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

        Bundle args = new Bundle();
        args.putString("user_id", receiverid);
        args.putString("user_name",name);
        args.putString("user_pic",picture);

        chat_activity.setArguments(args);
//        transaction.addToBackStack(null);
        transaction.replace(R.id.container_chat, chat_activity).commit();
    }
}