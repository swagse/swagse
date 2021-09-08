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
import com.app.swagse.adapter.SwagTubeCommentAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.commentData.CommentResponse;
import com.app.swagse.model.swagTube.CommentdataItem;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
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

public class SwagTubeCommentActivity extends AppCompatActivity {

    AppCompatTextView add_swagTubeComment;
    List<SwagtubedataItem> swagTubeDataList;
    SwagTubeAdapter swagTubeAdapter;
    RecyclerView recyclerView;
    Api apiInterface;
    AppCompatEditText enterComment;
    private List<CommentdataItem> commentDataList;
    RecyclerView commentRecyclerView;
    NestedScrollView nestedScrollView;
    SwagTubeCommentAdapter commentAdapter;
    private ProgressDialog progressDialog;
    SwagtubedataItem swagtubedataItem;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swag_tube_comment);

        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);

        commentDataList = new ArrayList<>();
        nestedScrollView = findViewById(R.id.nestedScrollView);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        commentAdapter = new SwagTubeCommentAdapter(this, commentDataList);
        commentRecyclerView.setAdapter(commentAdapter);

        add_swagTubeComment = findViewById(R.id.add_swagTubeComment);
        enterComment = findViewById(R.id.enterComment);

        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(PlayerViewHolder.class.getSimpleName())) {
            commentDataList = (List<CommentdataItem>) intent.getExtras().getSerializable(PlayerViewHolder.class.getSimpleName());
            id = intent.getExtras().getString("id");
            if (commentDataList.size() != 0 && commentDataList != null && !commentDataList.isEmpty()) {
                commentAdapter.notifyList(commentDataList);
            }
        } else if (intent.hasExtra(SwagTubeDetailsActivity.class.getSimpleName())) {
            commentDataList = (List<CommentdataItem>) intent.getSerializableExtra(SwagTubeDetailsActivity.class.getSimpleName());
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
                        if (PrefConnect.readBoolean(SwagTubeCommentActivity.this, Constants.GUEST_USER, false)) {
                            startActivity(new Intent(SwagTubeCommentActivity.this, LoginActivity.class));
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
            Call<CommentResponse> userResponseCall = apiInterface.swagTubeComment(id, PrefConnect.readString(this, Constants.USERID, ""), URLEncoder.encode(enterComment.getText().toString(), "UTF-8"));
            userResponseCall.enqueue(new Callback<CommentResponse>() {
                @Override
                public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            List<com.app.swagse.model.commentData.CommentdataItem> commentdata = response.body().getCommentdata();
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
                            toast(SwagTubeCommentActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SwagTubeCommentActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<CommentResponse> call, Throwable t) {
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