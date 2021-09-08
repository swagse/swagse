package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.app.swagse.R;
import com.app.swagse.adapter.NavWatchAdapter;
import com.app.swagse.adapter.SearchRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchRecyclerView;
    private Api apiInterface;
    private AppCompatImageView back_icon;
    private AppCompatEditText place_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        apiInterface = RetrofitClient.getInstance().getApi();
        back_icon = findViewById(R.id.back_icon);
        place_search = findViewById(R.id.place_search);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));

        place_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  filter(charSequence.toString().substring(0,3));
                if (charSequence.toString().length() >= 3) {
                    getSearchData(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
            }
        });
//        getVideoHistory();

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getSearchData(String searchData) {
        if (App.isOnline()) {
            Call<SwagTubeResponse> userResponseCall = apiInterface.searchVideo(PrefConnect.readString(SearchActivity.this, Constants.USERID, ""), searchData);
            userResponseCall.enqueue(new Callback<SwagTubeResponse>() {
                @Override
                public void onResponse(Call<SwagTubeResponse> call, Response<SwagTubeResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwagtubedataItem> swagTubeDataList = response.body().getSwagtubedata();
                            if (swagTubeDataList.size() != 0 && swagTubeDataList != null && !swagTubeDataList.isEmpty()) {
                                SearchRecyclerViewAdapter swagTubeAdapter = new SearchRecyclerViewAdapter(SearchActivity.this, swagTubeDataList);
                                searchRecyclerView.setAdapter(swagTubeAdapter);

                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(SearchActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(SearchActivity.this);
                        }

                    }
                }

                @Override
                public void onFailure(Call<SwagTubeResponse> call, Throwable t) {

                }
            });
        }
    }
}