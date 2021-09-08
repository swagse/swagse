package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.swagse.R;
import com.app.swagse.adapter.PackageRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.SubPackageResponse;
import com.app.swagse.model.subPackage.PackageResponse;
import com.app.swagse.model.subPackage.SubscriptionItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class SubcriptionPackageActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = SubcriptionPackageActivity.class.getSimpleName();
    RecyclerView packageRecyclerVew;
    Api apiInterface;
    private ProgressDialog progressDialog;
    RelativeLayout nothing_main_layout, packageLayout;
    ProgressBar progress_bar;
    String packageId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription_package);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subscription Plan");

        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        nothing_main_layout = findViewById(R.id.nothing_main_layout);
        packageLayout = findViewById(R.id.packageLayout);
        progress_bar = findViewById(R.id.progress_bar1);


        packageRecyclerVew = findViewById(R.id.packageRecyclerVew);
        packageRecyclerVew.setHasFixedSize(true);
        packageRecyclerVew.setLayoutManager(new LinearLayoutManager(SubcriptionPackageActivity.this, LinearLayoutManager.VERTICAL, false));

        getSubscriptionPackage();
    }

    private void getSubscriptionPackage() {
        progress_bar.setVisibility(View.VISIBLE);
        Call<PackageResponse> responseCall = apiInterface.getSubscriptionPackage();
        responseCall.enqueue(new Callback<PackageResponse>() {
            @Override
            public void onResponse(Call<PackageResponse> call, Response<PackageResponse> response) {
                progress_bar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        List<SubscriptionItem> subscriptionItemList = response.body().getSubscription();
                        if (subscriptionItemList.size() != 0 && subscriptionItemList != null) {
                            PackageRecyclerViewAdapter packageRecyclerViewAdapter = new PackageRecyclerViewAdapter(SubcriptionPackageActivity.this, subscriptionItemList);
                            packageRecyclerVew.setAdapter(packageRecyclerViewAdapter);
                            nothing_main_layout.setVisibility(View.GONE);
                            packageLayout.setVisibility(View.VISIBLE);
                        } else {
                            nothing_main_layout.setVisibility(View.VISIBLE);
                            packageLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PackageResponse> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t);
            }
        });
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

    public void openPaymentGetWay(String id, String price) {
        final Activity activity = this;
        packageId = id;
        String razorpayKey = "rzp_test_7RZ08DDcF4rQ5k"; //Generate your razorpay key from Settings-> API Keys-> copy Key Id
        Checkout chackout = new Checkout();
        chackout.setKeyID(razorpayKey);
        try {
            JSONObject options = new JSONObject();
            options.put("name", PrefConnect.readString(SubcriptionPackageActivity.this, Constants.USERNAME, ""));
            options.put("description", "Razorpay Payment Test");
            options.put("currency", "INR");
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
            preFill.put("email", PrefConnect.readString(SubcriptionPackageActivity.this, Constants.EMAIL, ""));
            preFill.put("contact", PrefConnect.readString(SubcriptionPackageActivity.this, Constants.MOBILE_NUMBER, ""));
            options.put("prefill", preFill);

            chackout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(SubcriptionPackageActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // After successful payment Razorpay send back a unique id
        Log.d(TAG, "onPaymentSuccess: " + razorpayPaymentID);
        Toast.makeText(SubcriptionPackageActivity.this, "Transaction Successful: " + razorpayPaymentID, Toast.LENGTH_LONG).show();
        requestForPackage(razorpayPaymentID, packageId);
    }

    @Override
    public void onPaymentError(int i, String error) {
        // Error message
        Toast.makeText(SubcriptionPackageActivity.this, "Transaction unsuccessful: " + error, Toast.LENGTH_LONG).show();
    }

    private void requestForPackage(String razorpayPaymentID, String packageId) {
        if (App.getInstance().isOnline()) {
//            if (isValid()) {
//                progressDialog.show();
            //input_number.getText().toString()
            Api apiInterface = RetrofitClient.getInstance().getApi();
            Call<SubPackageResponse> userResponseCall = apiInterface.subscribePackages(razorpayPaymentID, packageId, PrefConnect.readString(SubcriptionPackageActivity.this, Constants.USERID, ""));
            userResponseCall.enqueue(new Callback<SubPackageResponse>() {
                @Override
                public void onResponse(Call<SubPackageResponse> call, Response<SubPackageResponse> response) {
//                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            PrefConnect.writeString(SubcriptionPackageActivity.this, Constants.VIDEO_DURATION, response.body().getVideoLength());
                            PrefConnect.writeString(SubcriptionPackageActivity.this, Constants.VIDEO_SIZE, response.body().getVideoSize());
                            new SweetAlertDialog(SubcriptionPackageActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Thank you!").setContentText("Your Subscription is Purchases Successfully").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    Intent intent = new Intent(SubcriptionPackageActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).show();
//                            Toast.makeText(context, "" + response.body().getOtp(), Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(context, VerifyOTPActivity.class).putExtra(ShowPhoneNumberActivity.class.getSimpleName(), input_mobileNumber.getText().toString()));
//                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
//                            toast(context, response.body().getMessage());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SubcriptionPackageActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SubcriptionPackageActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<SubPackageResponse> call, Throwable t) {
//                    progressDialog.dismiss();

                }
            });
        }
    }
}