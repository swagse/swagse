package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.adapter.NotificationRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.RemoveDataResponse;
import com.app.swagse.model.notification.NotificationResponse;
import com.app.swagse.model.notification.NotificationsItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class NotificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RelativeLayout nothing_main_layout;
    RecyclerView notificationRecyclerView;
    Api apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        progressBar = findViewById(R.id.progress_bar);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);

        apiInterface = RetrofitClient.getInstance().getApi();
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setHasFixedSize(true);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        progressBar = findViewById(R.id.progress_bar);

        getNotificationList();
    }

    private void getNotificationList() {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<NotificationResponse> userResponseCall = apiInterface.getnotifications(PrefConnect.readString(NotificationActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<NotificationResponse>() {
                @Override
                public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<NotificationsItem> subscriptionlistItemList = response.body().getNotifications();
                            if (subscriptionlistItemList.size() != 0 && subscriptionlistItemList != null && !subscriptionlistItemList.isEmpty()) {
                                NotificationRecyclerViewAdapter notificationAdapter = new NotificationRecyclerViewAdapter(NotificationActivity.this, subscriptionlistItemList);
                                notificationRecyclerView.setAdapter(notificationAdapter);
                                nothing_main_layout.setVisibility(View.GONE);
                                notificationRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                nothing_main_layout.setVisibility(View.VISIBLE);
                                notificationRecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            notificationRecyclerView.setVisibility(View.GONE);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(NotificationActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(NotificationActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<NotificationResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clear_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.clearAll) {
            removeVideoFromList();
//            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeVideoFromList() {
        ProgressDialog progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Api apiInterface = RetrofitClient.getInstance().getApi();
        Call<RemoveDataResponse> removeDataResponseCall = apiInterface.clearNotification(PrefConnect.readString(NotificationActivity.this, Constants.USERID, ""));
        removeDataResponseCall.enqueue(new Callback<RemoveDataResponse>() {
            @Override
            public void onResponse(Call<RemoveDataResponse> call, Response<RemoveDataResponse> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        OwnerGlobal.toast(NotificationActivity.this, response.body().getMessage());
                        nothing_main_layout.setVisibility(View.VISIBLE);
                        notificationRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    OwnerGlobal.toast(NotificationActivity.this, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<RemoveDataResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t);
            }
        });
    }
}