package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.swagse.R;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class NavFeedbackActivity extends AppCompatActivity {

    private static final String TAG = NavFeedbackActivity.class.getSimpleName();
    @BindView(R.id.feedback_edittext)
    AppCompatEditText feedback_editText;
    @BindView(R.id.submit_feedback)
    AppCompatButton submit_feedback;
    private Api apiInterface;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_feedback);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        apiInterface = RetrofitClient.getInstance().getApi();

    }

    @OnClick(R.id.submit_feedback)
    public void onClickedView(View view) {
        if (isValid()) {
            if (App.isOnline()) {
                progressDialog.show();
                Call<JsonElement> userResponseCall = apiInterface.sendFeedback(PrefConnect.readString(NavFeedbackActivity.this, Constants.USERID, ""), feedback_editText.getText().toString());
                userResponseCall.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        progressDialog.dismiss();
                        if (response.code() == Constants.SUCCESS) {
//                            if (response.body().getStatus().equals("1")) {
                            Toast.makeText(NavFeedbackActivity.this, "Thanks for giving your feedback", Toast.LENGTH_SHORT).show();
                            finish();
//                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(NavFeedbackActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(NavFeedbackActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t);
                        progressDialog.dismiss();
                    }
                });
            }
        }
    }

    private boolean isValid() {
        if (feedback_editText.getText().toString().isEmpty()) {
            feedback_editText.setError("Field is required*");
            return false;
        }
        feedback_editText.setError(null);
        return true;
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