package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.adapter.CategoryRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.listener.OnItemClickListener;
import com.app.swagse.model.category.CategoryItem;
import com.app.swagse.model.category.CategoryResponse;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class CategoryListActivity extends AppCompatActivity {

    private static final String TAG = CategoryListActivity.class.getSimpleName();
    private RecyclerView categoryRecyclerView;
    private Api apiInterface;
    private List<CategoryItem> categoryList;
    private RelativeLayout nothing_layout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        getSupportActionBar().setTitle("Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterface = RetrofitClient.getInstance().getApi();
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        progressBar = findViewById(R.id.progressBar);
        nothing_layout = findViewById(R.id.nothing_main_layout);

        getCategoryData();
    }

    private void getCategoryData() {
        if (App.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            Call<CategoryResponse> userResponseCall = apiInterface.getswagtubecategory();
            userResponseCall.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            categoryList = response.body().getCategory();
                            if (categoryList.size() != 0 && categoryList != null && !categoryList.isEmpty()) {
                                CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(CategoryListActivity.this, categoryList);
                                categoryRecyclerView.setAdapter(categoryRecyclerViewAdapter);
                                categoryRecyclerView.setVisibility(View.VISIBLE);
                                nothing_layout.setVisibility(View.GONE);
                            } else {
                                categoryRecyclerView.setVisibility(View.GONE);
                                nothing_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(CategoryListActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(CategoryListActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
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


    public void selectCategory(String categoryName) {
        startActivity(new Intent(CategoryListActivity.this, CategoryActivity.class).putExtra(CategoryListActivity.class.getSimpleName(), categoryName));
    }
}