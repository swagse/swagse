package com.app.swagse.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.swagse.R;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.GetOTPResponse;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.model.userDetails.Userdata;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.receiver.SMSReceiver;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.app.swagse.utils.AppUtil;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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

public class VerifyOTPActivity extends AppCompatActivity implements SMSReceiver.OTPReceiveListener {
    private SMSReceiver smsReceiver;

    @BindView(R.id.enterOTP)
    Pinview enterOTP;
    @BindView(R.id.showMobileNumber)
    AppCompatTextView showMobileNumber;
    @BindView(R.id.verifyOTP)
    AppCompatButton verifyOTP;
    private ProgressDialog progressDialog;
    private Api apiInterface;
    private String phoneNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        ButterKnife.bind(this);
        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        enterOTP.requestFocus();

        Intent intent = getIntent();
        if (intent.hasExtra(ShowPhoneNumberActivity.class.getSimpleName())) {
            phoneNumber = intent.getExtras().getString(ShowPhoneNumberActivity.class.getSimpleName());
            if (phoneNumber != null) {
                showMobileNumber.setText(phoneNumber);
            }
        }

        // Call server API for requesting OTP and when you got success start
        // SMS Listener for listing auto read message lsitner
        startSMSListener();
    }

    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        showToast("OTP Received: " + otp);

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
        showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        progressDialog.show();showToast(error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.verifyOTP)
    public void onClickViewed(View view) {
        OwnerGlobal.hideKeyboard(VerifyOTPActivity.this);
        if (App.getInstance().isOnline()) {
            if (enterOTP.getValue().isEmpty()) {
                toast(VerifyOTPActivity.this, "Enter OTP");
            } else {

                Call<UserDetailResponse> userResponseCall = apiInterface.verifyOTP(phoneNumber, enterOTP.getValue(), AppUtil.getDeviceId(this), AppUtil.getFCMToken(this));
                userResponseCall.enqueue(new Callback<UserDetailResponse>() {
                    @Override
                    public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                        progressDialog.dismiss();
                        if (response.code() == Constants.SUCCESS) {
                            if (response.body().getStatus().equals("1")) {
                                Toast.makeText(VerifyOTPActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                if (response.body().getUserstatus().equals("0")) {
                                    startActivity(new Intent(VerifyOTPActivity.this, SignUpActivity.class).putExtra("number", phoneNumber).putExtra("from", "verify"));
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else {
                                    Userdata userdata = response.body().getUserdata();
                                    if (userdata != null) {
                                        startActivity(new Intent(VerifyOTPActivity.this, MainActivity.class));
                                        PrefConnect.writeBoolean(VerifyOTPActivity.this, Constants.USER_LOGGED, true);
                                        PrefConnect.writeBoolean(VerifyOTPActivity.this, Constants.GUEST_USER, false);
                                        PrefConnect.writeString(VerifyOTPActivity.this, Constants.USERID, userdata.getId());
                                        PrefConnect.writeString(VerifyOTPActivity.this, Constants.USERNAME, userdata.getUserName());
                                        PrefConnect.writeString(VerifyOTPActivity.this, Constants.VIDEO_DURATION, String.valueOf(response.body().getVideoLength()));
                                        PrefConnect.writeString(VerifyOTPActivity.this, Constants.VIDEO_SIZE, String.valueOf(response.body().getVideoSize()));
                                        finishAffinity();
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                }
                            } else {
                                toast(VerifyOTPActivity.this, response.body().getMessage());
                            }
                        } else if (response.code() == Constants.FAILED) {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                toast(VerifyOTPActivity.this, jObjError.getString("response_msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == Constants.UNAUTHORIZED) {
                            OwnerGlobal.LoginRedirect(VerifyOTPActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                        progressDialog.dismiss();

                    }
                });
            }
        }
    }

    public void resendOTP(View view) {
        progressDialog.show();
        Call<GetOTPResponse> userResponseCall = apiInterface.getOTP(phoneNumber.replace("+91", ""));
        userResponseCall.enqueue(new Callback<GetOTPResponse>() {
            @Override
            public void onResponse(Call<GetOTPResponse> call, Response<GetOTPResponse> response) {
                progressDialog.dismiss();
                if (response.code() == Constants.SUCCESS) {
                    if (response.body().getStatus().equals("1")) {
                        toast(VerifyOTPActivity.this, String.valueOf(response.body().getOtp()));
                    } else {
                        toast(VerifyOTPActivity.this, response.body().getMessage());
                    }
                } else if (response.code() == Constants.FAILED) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        toast(VerifyOTPActivity.this, jObjError.getString("response_msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (response.code() == Constants.UNAUTHORIZED) {
                    OwnerGlobal.LoginRedirect(VerifyOTPActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetOTPResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }
}