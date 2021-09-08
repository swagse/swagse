package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.adapter.PlayerViewHolder;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.adapter.SwaggerCommentAdapter;
import com.app.swagse.adapter.VideoAdapterNew;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.commentData.CommentResponse;
import com.app.swagse.model.swaggerCommentData.SwaggerCommentResponse;
import com.app.swagse.model.swaggerCommentData.SwaggercommentdataItem;
import com.app.swagse.model.swaggerData.CommentdataItem;
import com.app.swagse.model.swaggerData.SwaggerdataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class SwaggerCommentActivity extends AppCompatActivity {

    AppCompatTextView add_swagTubeComment;
    Api apiInterface;
    AppCompatEditText enterComment;
    private List<CommentdataItem> commentDataList;
    RecyclerView commentRecyclerView;
    NestedScrollView nestedScrollView;
    SwaggerCommentAdapter commentAdapter;
    private ProgressDialog progressDialog;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swagger_comment);
        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        commentDataList = new ArrayList<>();
        nestedScrollView = findViewById(R.id.nestedScrollView);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        commentAdapter = new SwaggerCommentAdapter(this, commentDataList);
        commentRecyclerView.setAdapter(commentAdapter);

        add_swagTubeComment = findViewById(R.id.add_swagTubeComment);
        enterComment = findViewById(R.id.enterComment);

        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(VideoAdapterNew.class.getSimpleName())) {
            commentDataList = (List<CommentdataItem>) intent.getExtras().getSerializable(VideoAdapterNew.class.getSimpleName());
            id = intent.getExtras().getString("id");
            if (commentDataList.size() != 0 && commentDataList != null && !commentDataList.isEmpty()) {
                commentAdapter.notifyList(commentDataList);
            }
        }

        enterComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    nestedScrollView.scrollTo(0, commentRecyclerView.getBottom());
                }
                return false;
            }
        });

        add_swagTubeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isValid()) {
                        if (PrefConnect.readBoolean(SwaggerCommentActivity.this, Constants.GUEST_USER, false)) {
                            startActivity(new Intent(SwaggerCommentActivity.this, LoginActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            commentAction();
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isValid() {
        if (enterComment.getText().toString().isEmpty()) {
            OwnerGlobal.toast(this, "Enter Comment");
            return false;
        }
        return true;
    }

    public void commentAction() throws UnsupportedEncodingException {
        if (App.isOnline()) {
            progressDialog.show();
            Call<SwaggerCommentResponse> userResponseCall = apiInterface.swaggerComment(id, PrefConnect.readString(this, Constants.USERID, ""), URLEncoder.encode(enterComment.getText().toString(), "UTF-8"));
            userResponseCall.enqueue(new Callback<SwaggerCommentResponse>() {
                @Override
                public void onResponse(Call<SwaggerCommentResponse> call, Response<SwaggerCommentResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<SwaggercommentdataItem> commentdata = response.body().getSwaggercommentdata();
                            if (commentdata.size() != 0 && commentdata != null && !commentdata.isEmpty()) {
                                enterComment.setText("");
                                commentDataList.add(new CommentdataItem(commentdata.get(0).getUsername(), commentdata.get(0).getTimeago(), commentdata.get(0).getCommentUserId(), commentdata.get(0).getComment(), commentdata.get(0).getCommentId(), commentdata.get(0).getUserpic()));
                                commentAdapter.notifyList(commentDataList);
                                commentAdapter.notifyDataSetChanged();
//                                commentRecyclerView.smoothScrollToPosition(commentRecyclerView.getBottom());
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SwaggerCommentActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwaggerCommentActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SwaggerCommentResponse> call, Throwable t) {
                    progressDialog.dismiss();
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