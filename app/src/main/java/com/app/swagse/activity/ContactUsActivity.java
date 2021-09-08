package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.swagse.R;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class ContactUsActivity extends AppCompatActivity {

    private static final String TAG = ContactUsActivity.class.getSimpleName();
    @BindView(R.id.enter_comment)
    AppCompatEditText enter_comment;
    @BindView(R.id.submit_contact)
    AppCompatButton submit_contact;
    private Api apiInterface;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        apiInterface = RetrofitClient.getInstance().getApi();

    }

    @OnClick(R.id.submit_contact)
    public void onClickViewed(View view) {
        if (isValid()) {
            if (App.isOnline()) {
                progressDialog.show();
                Call<JsonElement> userResponseCall = apiInterface.contactUs(PrefConnect.readString(ContactUsActivity.this, Constants.USERID, ""), enter_comment.getText().toString());
                userResponseCall.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        progressDialog.dismiss();
                        if (response.code() == Constants.SUCCESS) {
//                            if (response.body().getStatus().equals("1")) {
                            Toast.makeText(ContactUsActivity.this, "Thank your for contacting Us", Toast.LENGTH_SHORT).show();
                            finish();
//                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(ContactUsActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(ContactUsActivity.this);
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
        if (enter_comment.getText().toString().isEmpty()) {
            enter_comment.setError("Enter Comments");
            return false;
        }
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