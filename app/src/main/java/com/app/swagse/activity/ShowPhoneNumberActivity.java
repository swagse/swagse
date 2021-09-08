package com.app.swagse.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.swagse.R;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.GetOTPResponse;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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

public class ShowPhoneNumberActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ShowPhoneNumberActivity.class.getSimpleName();
    private static final int RESOLVE_HINT = 0;
    @BindView(R.id.verify_number)
    AppCompatButton verify_number;
    @BindView(R.id.input_mobileNumber)
    AppCompatEditText input_mobileNumber;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private Api apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_phone_number);
        ButterKnife.bind(this);


        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        getHintPhoneNumber();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // Construct a request for phone numbers and show the picker
    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // Obtain the phone number from the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
                String number = credential.getId().replace("+91", "");
                Log.d(TAG, "onActivityResult: " + number);
                input_mobileNumber.setText(number);
            }
        }
    }

    @OnClick(R.id.verify_number)
    public void onClickViewed(View view) {
        if (App.getInstance().isOnline()) {
            if (isValid()) {
                progressDialog.show();
                //input_number.getText().toString()
                Call<GetOTPResponse> userResponseCall = apiInterface.getOTP(input_mobileNumber.getText().toString().replace("+91", ""));
                userResponseCall.enqueue(new Callback<GetOTPResponse>() {
                    @Override
                    public void onResponse(Call<GetOTPResponse> call, Response<GetOTPResponse> response) {
                        progressDialog.dismiss();
                        if (response.code() == Constants.SUCCESS) {
                            if (response.body().getStatus().equals("1")) {
                                Toast.makeText(ShowPhoneNumberActivity.this, "" + response.body().getOtp(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ShowPhoneNumberActivity.this, VerifyOTPActivity.class).putExtra(ShowPhoneNumberActivity.class.getSimpleName(), input_mobileNumber.getText().toString()));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else {
                                toast(ShowPhoneNumberActivity.this, response.body().getMessage());
                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(ShowPhoneNumberActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(ShowPhoneNumberActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetOTPResponse> call, Throwable t) {
                        progressDialog.dismiss();

                    }
                });
            }
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(input_mobileNumber.getText())) {
            input_mobileNumber.setError("Field is required*");
            input_mobileNumber.requestFocus();
            return false;
        }
        input_mobileNumber.setError(null);

        if (input_mobileNumber.getText().length() != 10) {
            input_mobileNumber.setError("Enter correct digits of Mobile Number");
            input_mobileNumber.requestFocus();
            return false;
        }
        input_mobileNumber.setError(null);

        if (!(input_mobileNumber.getText().charAt(0) == '6' || input_mobileNumber.getText().charAt(0) == '7' || input_mobileNumber.getText().charAt(0) == '8' || input_mobileNumber.getText().charAt(0) == '9')) {
            input_mobileNumber.setError("Enter correct Mobile Number");
            input_mobileNumber.requestFocus();
            return false;
        }
        input_mobileNumber.setError(null);
        input_mobileNumber.clearFocus();

        return true;
    }



}