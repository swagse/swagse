package com.app.swagse.activity.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.RemoveDataResponse;
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

public class NavReportHistoryActivity extends AppCompatActivity {

    RecyclerView historyRecyclerView;
    RelativeLayout nothing_main_layout, historyLayout;
    ProgressBar progressBar;
    private Api apiInterface;
    private SwipeRefreshLayout historySwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_report_history);
        getSupportActionBar().setTitle("Report History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterface = RetrofitClient.getInstance().getApi();
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setHasFixedSize(true);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        historyLayout = findViewById(R.id.historyLayout);
        historySwipeLayout = findViewById(R.id.historySwipeLayout);
        progressBar = findViewById(R.id.progress_bar);

        getSwagTubeData();

        historySwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                historySwipeLayout.setRefreshing(false);
                getSwagTubeData();
            }
        });

    }

    public void getSwagTubeData() {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<SwagTubeResponse> userResponseCall = apiInterface.getReportHistory(PrefConnect.readString(NavReportHistoryActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                NavWatchAdapter swagTubeAdapter = new NavWatchAdapter(NavReportHistoryActivity.this, swagTubeDataList,"report");
                                historyRecyclerView.setAdapter(swagTubeAdapter);
                                nothing_main_layout.setVisibility(View.GONE);
                                historyLayout.setVisibility(View.VISIBLE);
                            } else {
                                nothing_main_layout.setVisibility(View.VISIBLE);
                                historyLayout.setVisibility(View.GONE);
                            }
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            historyLayout.setVisibility(View.GONE);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(NavReportHistoryActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(NavReportHistoryActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {
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
//            finish();
            removeVideoFromList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeVideoFromList() {
        ProgressDialog progressDialog = new ProgressDialog(NavReportHistoryActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Api apiInterface = RetrofitClient.getInstance().getApi();
        Call<RemoveDataResponse> removeDataResponseCall = apiInterface.deleteReportVideo("", PrefConnect.readString(NavReportHistoryActivity.this, Constants.USERID, ""));
        removeDataResponseCall.enqueue(new Callback<RemoveDataResponse>() {
            @Override
            public void onResponse(Call<RemoveDataResponse> call, Response<RemoveDataResponse> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        OwnerGlobal.toast(NavReportHistoryActivity.this, response.body().getMessage());
                        nothing_main_layout.setVisibility(View.VISIBLE);
                        historyLayout.setVisibility(View.GONE);
                    } else {
                        OwnerGlobal.toast(NavReportHistoryActivity.this, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RemoveDataResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t);
            }
        });
    }
}