package com.app.swagse.activity.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.adapter.SubscriptionRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.subscription.SubscriptionResponse;
import com.app.swagse.model.subscription.SubscriptionlistItem;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
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

public class NavSubscriptionActivity extends AppCompatActivity {

    RecyclerView subscriptionRecyclerView;
    RelativeLayout nothing_main_layout, subscriptionLayout;
    ProgressBar progressBar;
    private Api apiInterface;
    private SwipeRefreshLayout subscriptionSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_subscription);

        getSupportActionBar().setTitle("Subscription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterface = RetrofitClient.getInstance().getApi();
        subscriptionRecyclerView = findViewById(R.id.subscriptionRecyclerView);
        subscriptionRecyclerView.setHasFixedSize(true);
        subscriptionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        subscriptionLayout = findViewById(R.id.subscriptionLayout);
        subscriptionSwipeLayout = findViewById(R.id.subscriptionSwipeLayout);
        progressBar = findViewById(R.id.progress_bar);

        getSubscriptionChannelList();

        subscriptionSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                subscriptionSwipeLayout.setRefreshing(false);
                getSubscriptionChannelList();
            }
        });

    }

    private void getSubscriptionChannelList() {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SubscriptionResponse> userResponseCall = apiInterface.getSubscriptionList(PrefConnect.readString(NavSubscriptionActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SubscriptionResponse>() {
                @Override
                public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SubscriptionlistItem> subscriptionlistItemList = response.body().getSubscriptionlist();
                            if (subscriptionlistItemList.size() != 0 && subscriptionlistItemList != null && !subscriptionlistItemList.isEmpty()) {
                                SubscriptionRecyclerViewAdapter swagTubeAdapter = new SubscriptionRecyclerViewAdapter(NavSubscriptionActivity.this, subscriptionlistItemList);
                                subscriptionRecyclerView.setAdapter(swagTubeAdapter);
                                nothing_main_layout.setVisibility(View.GONE);
                                subscriptionLayout.setVisibility(View.VISIBLE);
                            } else {
                                nothing_main_layout.setVisibility(View.VISIBLE);
                                subscriptionLayout.setVisibility(View.GONE);
                            }
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            subscriptionLayout.setVisibility(View.GONE);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(NavSubscriptionActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(NavSubscriptionActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}