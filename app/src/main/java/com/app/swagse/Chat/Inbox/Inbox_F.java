package com.app.swagse.Chat.Inbox;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.Chat.Chat_Fragment;
import com.app.swagse.R;
import com.app.swagse.SimpleClasses.Fragment_Callback;
import com.app.swagse.SimpleClasses.Functions;
import com.app.swagse.activity.ChatActivity;
import com.app.swagse.constants.Variables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inbox_F extends AppCompatActivity {


    Context context;

    RecyclerView inbox_list;

    ArrayList<Inbox_Get_Set> inbox_arraylist;
    DatabaseReference root_ref;

    Inbox_Adapter inbox_adapter;

    ProgressBar pbar;

    boolean isview_created=false;
    private String Tag = "INBOXFRAG";

    public Inbox_F() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_inbox);
        context =   Inbox_F.this;
        root_ref = FirebaseDatabase.getInstance().getReference();


        pbar = findViewById(R.id.pbar);
        inbox_list = findViewById(R.id.inboxlist);

        // intialize the arraylist and and inboxlist
        inbox_arraylist = new ArrayList<>();

        inbox_list = (RecyclerView)findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inbox_list.setLayoutManager(layout);
        inbox_list.setHasFixedSize(false);
        inbox_adapter = new Inbox_Adapter(context, inbox_arraylist, new Inbox_Adapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(Inbox_Get_Set item) {

                // if user allow the stroage permission then we open the chat view
                if (check_ReadStoragepermission())
                    chatFragment(item.getId(), item.getName(), item.getPic());


            }
        }, new Inbox_Adapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Inbox_Get_Set item) {

                showPopUpOptions(item);
                Log.d("datata",""+item.getId());
            }
        });

        inbox_list.setAdapter(inbox_adapter);


        findViewById(R.id.Inbox_F).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.hideSoftKeyboard((Activity) context);

            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


        isview_created = true;

        getData();
    }

    private void showPopUpOptions(Inbox_Get_Set item) {
        //create dialog for update
        AlertDialog.Builder  builder = new AlertDialog.Builder(context);

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to remove this contact from chat ?" )
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       DatabaseReference query_getchat = root_ref.child("chat").child(Variables.user_id + "-" + item.getId());
                       query_getchat.removeValue();

                        DatabaseReference  mPostReference = root_ref
                                .child("Inbox").child(Variables.user_id).child(item.getId());
                        mPostReference.removeValue();
                       // inbox_arraylist.remove()
                        inbox_adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Remove chat with "+item.name);
        alert.show();
    }

    // on start we will get the Inbox Message of user  which is show in bottom list of third tab
    ValueEventListener eventListener2;
    Query inbox_query;
    public void getData() {

        pbar.setVisibility(View.VISIBLE);

        Log.d("user__sisi",Variables.user_id);
        inbox_query=root_ref.child("Inbox").child(Variables.user_id).orderByChild("date");
        eventListener2=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inbox_arraylist.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Inbox_Get_Set model = ds.getValue(Inbox_Get_Set.class);
                    model.setId(ds.getKey());

                    inbox_arraylist.add(model);
                }

                pbar.setVisibility(View.GONE);

                if (inbox_arraylist.isEmpty())
                    findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                else {
                    findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                    Collections.reverse(inbox_arraylist);
                    inbox_adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        inbox_query.addValueEventListener(eventListener2);


    }



    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if(inbox_query!=null)
        inbox_query.removeEventListener(eventListener2);
    }



    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String receiverid, String name, String picture){
        /*Chat_Fragment chat_fragment = new Chat_Fragment(new Fragment_Callback() {
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

        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Inbox_F, chat_fragment).commit();*/
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("user_id",receiverid);
        i.putExtra("user_name",name);
        i.putExtra("user_pic",picture);
        startActivity(i);
    }



    //this method will check there is a storage permission given or not
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean check_ReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Variables.permission_Read_data );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }



}
