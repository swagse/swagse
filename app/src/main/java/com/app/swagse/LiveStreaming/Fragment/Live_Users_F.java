package com.app.swagse.LiveStreaming.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.LiveStreaming.Adapter_ClickListener;
import com.app.swagse.LiveStreaming.activities.LiveActivity;
import com.app.swagse.LiveStreaming.activities.MainActivity;
import com.app.swagse.R;
import com.app.swagse.activity.CodeClasses.RootFragment;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.agora.rtc.Constants;

/**
 * A simple {@link Fragment} subclass.
 */

public class Live_Users_F extends AppCompatActivity implements View.OnClickListener {


    // View view;
    Context context;
    ArrayList<Live_user_Model> data_list;
    RecyclerView recyclerView;
    Live_user_Adapter adapter;

    DatabaseReference rootref;

    TextView no_data_found;
    RelativeLayout relative_layout_no_live_users, relative_layout_live_users;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_live_users);
        context = Live_Users_F.this;

        initM();
        rootref = FirebaseDatabase.getInstance().getReference();

        data_list = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        recyclerView.setHasFixedSize(true);

        adapter = new Live_user_Adapter(context, data_list, new Adapter_ClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void On_Item_Click(int postion, Object Model, View view) {
                Live_user_Model live_user_model = (Live_user_Model) Model;
                Open_hugme_live(live_user_model.getUser_id(),
                        live_user_model.getUser_name(), live_user_model.getUser_picture(), Constants.CLIENT_ROLE_AUDIENCE);
            }

            @Override
            public void On_Long_Item_Click(int postion, Object Model, View view) {

            }
        });

        recyclerView.setAdapter(adapter);


        findViewById(R.id.go_live_layout).setOnClickListener(this::onClick);
        relative_layout_no_live_users.setOnClickListener(this::onClick);
        Get_Data();

        no_data_found = findViewById(R.id.no_data_found);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_live_users, container, false);
        context=getContext();
        initM(view);
        rootref= FirebaseDatabase.getInstance().getReference();

        data_list=new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));

        recyclerView.setHasFixedSize(true);

        adapter=new Live_user_Adapter(context, data_list, new Adapter_ClickListener() {
            @Override
            public void On_Item_Click(int postion, Object Model, View view) {
                Live_user_Model live_user_model=(Live_user_Model) Model;
                Open_hugme_live(live_user_model.getUser_id(),
                        live_user_model.getUser_name(),live_user_model.getUser_picture(),Constants.CLIENT_ROLE_AUDIENCE);
            }

            @Override
            public void On_Long_Item_Click(int postion, Object Model, View view) {

            }
        });

        recyclerView.setAdapter(adapter);


        view.findViewById(R.id.go_live_layout).setOnClickListener(this::onClick);
        relative_layout_no_live_users.setOnClickListener(this::onClick);
        Get_Data();

        no_data_found=view.findViewById(R.id.no_data_found);
        return  view;
    }*/

    private void initM() {
        relative_layout_live_users = findViewById(R.id.relative_layout_live_users);
        relative_layout_no_live_users = findViewById(R.id.relative_layout_no_live_users);
    }

    ChildEventListener valueEventListener;

    public void Get_Data() {
        valueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Live_user_Model model = dataSnapshot.getValue(Live_user_Model.class);
                data_list.add(model);
                adapter.notifyDataSetChanged();
                relative_layout_no_live_users.setVisibility(View.GONE);
                relative_layout_live_users.setVisibility(View.VISIBLE);
                no_data_found.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Live_user_Model model = dataSnapshot.getValue(Live_user_Model.class);

                for (int i = 0; i < data_list.size(); i++) {
                    if (model.getUser_id().equals(data_list.get(i).getUser_id())) {
                        data_list.remove(i);
                        // Log.d("myusers",model.getUser_id() + " "+ data_list.get(i).getUser_id());
                    }
                }
                adapter.notifyDataSetChanged();

                if (data_list.isEmpty()) {
                    relative_layout_no_live_users.setVisibility(View.VISIBLE);
                    relative_layout_live_users.setVisibility(View.GONE);

                    no_data_found.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rootref.child("LiveUsers").addChildEventListener(valueEventListener);
        if (data_list.size() > 0) {
            relative_layout_no_live_users.setVisibility(View.GONE);
            relative_layout_live_users.setVisibility(View.VISIBLE);
        } else {
            relative_layout_no_live_users.setVisibility(View.VISIBLE);
            relative_layout_live_users.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rootref != null && valueEventListener != null)
            rootref.child("LiveUsers").removeEventListener(valueEventListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Open_hugme_live(String user_id, String user_name, String user_image, int role) {

        if (check_permissions()) {

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("user_id", user_id);
            intent.putExtra("user_name", user_name);
            intent.putExtra("user_picture", user_image);
            intent.putExtra("user_role", role);
            startActivity(intent);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_permissions() {
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {
            return true;
        }
        return false;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_live_layout:
                String user_id = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERID, "");
                String user_name = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERNAME, "");
                String user_image = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERPIC, "");
                Open_hugme_live(user_id, user_name, user_image, Constants.CLIENT_ROLE_BROADCASTER);
                break;

            case R.id.relative_layout_no_live_users:
                String user_id2 = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERID, "");
                String user_name2 = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERNAME, "");
                String user_image2 = PrefConnect.readString(context, com.app.swagse.constants.Constants.USERPIC, "");
                Open_hugme_live(user_id2, user_name2, user_image2, Constants.CLIENT_ROLE_BROADCASTER);
                break;
        }
    }
}
