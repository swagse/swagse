package com.app.swagse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.swagse.activity.MainActivity;
import com.app.swagse.activity.ShowPhoneNumberActivity;
import com.app.swagse.activity.SignUpActivity;
import com.app.swagse.activity.VerifyOTPActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.app.swagse.utils.AppUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    @BindView(R.id.google_login)
    AppCompatButton google_login;
    @BindView(R.id.facebook_login)
    AppCompatButton facebook_login;
    @BindView(R.id.phone_login)
    AppCompatButton phone_login;
    @BindView(R.id.signUp)
    AppCompatButton signUp;
    @BindView(R.id.skipLogin)
    AppCompatTextView skipLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private Api apiInterface;
    private CallbackManager callbackManager;
    private String fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
    }

    @OnClick({R.id.google_login, R.id.phone_login, R.id.facebook_login, R.id.signUp, R.id.skipLogin})
    public void onClickViewed(View view) {
        switch (view.getId()) {
            case R.id.google_login: {
                if (App.isOnline()) {
                    signIn();
                } else {
                    OwnerGlobal.networkToast(LoginActivity.this);
                }
                break;
            }
            case R.id.facebook_login: {
                if (App.isOnline()) {
                    facebookSignIn();
                } else {
                    OwnerGlobal.networkToast(LoginActivity.this);
                }
                break;
            }
            case R.id.phone_login: {
                if (App.isOnline()) {
                    startActivity(new Intent(LoginActivity.this, ShowPhoneNumberActivity.class));
                } else {
                    OwnerGlobal.networkToast(LoginActivity.this);
                }
                break;
            }
            case R.id.signUp: {
                if (App.isOnline()) {
                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class).putExtra("from", ""));
                } else {
                    OwnerGlobal.networkToast(LoginActivity.this);
                }
                break;
            }
            case R.id.skipLogin: {
                PrefConnect.writeBoolean(LoginActivity.this, Constants.USER_LOGGED, true);
                PrefConnect.writeBoolean(LoginActivity.this, Constants.GUEST_USER, true);
                PrefConnect.writeString(LoginActivity.this, Constants.USERNAME, "Guest User");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
        }
    }

    private void facebookSignIn() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "onSuccess: " + loginResult);
                        facebookProfileData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    private void facebookProfileData(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json_object, GraphResponse response) {
                Log.d(TAG, "onCompleted: " + json_object.toString());
                JSONObject picture = null;
                try {
                    picture = json_object.getJSONObject("picture");
                    JSONObject data = picture.getJSONObject("data");
                    //Fetch the data from the response
                    String facebook_pic = data.optString("url");
                    String social_name = json_object.optString("name", null);
                    String social_email = json_object.optString("email", null);
                    String facebook_fname = json_object.optString("first_name", null);
                    String facebook_lname = json_object.optString("last_name", null);
                    String social_type = "facebook";
                    // String social_pic = URLEncoder.encode(facebook_pic, "UTF-8");
                    Log.d("peofile_fname", facebook_fname);
                    Log.d("peofile_lname", facebook_lname);
                    Log.d("peofile_pic", facebook_pic);
                    Log.d("peofile_name", social_name);
                    Log.d("peofile_email", social_email);
                    socialLogin(social_name, social_email, facebook_pic, "Facebook");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,first_name,last_name,name,email,picture.width(480).height(480)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        Log.d(TAG, "updateUI: " + account);
        String name = account.getDisplayName();
        Log.d(TAG, "googleName: " + name);
        String email = account.getEmail();
        Log.d(TAG, "googleEmail: " + email);
        Uri photoUri = account.getPhotoUrl();
        Log.d(TAG, "updateUI: " + photoUri);
        socialLogin(name, email, String.valueOf(photoUri), "Google");
    }

    private void socialLogin(String name, String email, String pic, String loginType) {
        if (App.getInstance().isOnline()) {
            progressDialog.show();
            Call<UserDetailResponse> userResponseCall = apiInterface.socialLogin(name, email, pic, loginType, AppUtil.getDeviceId(this), AppUtil.getFCMToken(this));
            userResponseCall.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            PrefConnect.writeBoolean(LoginActivity.this, Constants.USER_LOGGED, true);
                            PrefConnect.writeBoolean(LoginActivity.this, Constants.GUEST_USER, false);
                            PrefConnect.writeString(LoginActivity.this, Constants.USERID, response.body().getUserdata().getId());
                            PrefConnect.writeString(LoginActivity.this, Constants.USERNAME, response.body().getUserdata().getUserName());
                            PrefConnect.writeString(LoginActivity.this, Constants.USERPIC, response.body().getUserdata().getImg());
                            PrefConnect.writeString(LoginActivity.this, Constants.CHANNEL_NAME, response.body().getUserdata().getChannelName());
                            PrefConnect.writeString(LoginActivity.this, Constants.VIDEO_DURATION, String.valueOf(response.body().getVideoLength()));
                            PrefConnect.writeString(LoginActivity.this, Constants.VIDEO_SIZE, String.valueOf(response.body().getVideoSize()));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finishAffinity();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(LoginActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(LoginActivity.this);
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