package com.app.swagse.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.GetOTPResponse;
import com.app.swagse.model.category.CategoryItem;
import com.app.swagse.model.category.CategoryResponse;
import com.app.swagse.model.cityData.CityDataResponse;
import com.app.swagse.model.cityData.CitydataItem;
import com.app.swagse.model.countryData.CountryResponse;
import com.app.swagse.model.countryData.CountrydataItem;
import com.app.swagse.model.stateData.StateDataResponse;
import com.app.swagse.model.stateData.StatedataItem;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.app.swagse.utils.AppUtil;
import com.app.swagse.utils.PathUtil;
import com.app.swagse.utils.WebUtils;
import com.bumptech.glide.Glide;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private static final int GALLERY_RESULT_CODE = 005;
    private static final int CAMERA_RESULT_CODE = 006;
    @BindView(R.id.userImage)
    CircleImageView userImage;

    @BindView(R.id.signUp)
    AppCompatButton signUp;

    @BindViews({R.id.username, R.id.channelName, R.id.tagline, R.id.monbileNumber, R.id.emailId})
    List<AppCompatEditText> appCompatEditTextList;

    AppCompatSpinner categorySpinner;

    @BindView(R.id.dob)
    AppCompatEditText dateOfBirth;

    @BindView(R.id.addImage)
    AppCompatImageView addImage;

    @BindView(R.id.gender_spinner)
    AppCompatSpinner genderSpinner;
    @BindView(R.id.countrySpinner)
    AppCompatSpinner countrySpinner;
    @BindView(R.id.stateSpinner)
    AppCompatSpinner stateSpinner;
    @BindView(R.id.citySpinner)
    AppCompatSpinner citySpinner;
    String stateISO, countryISO;
    List<String> genderList = new ArrayList<>();

    private int mYear, mMonth, mDay, mHour, mMinute;
    private ProgressDialog progressDialog;
    private Api apiInterface;
    private PickerMode pickerMode;
    private Uri filePath;
    private File file;
    private String _filePath;
    private Bitmap bitmap;
    private String userName, userPic, userEmail;
    String from = "";

    String selectedGender;
    private List<CategoryItem> categoryList;
    private String categoryName;
    private String loginType = "Manual";

    String simCountryName;
    String simStateName;
    String countryName, stateName, cityName;

    private enum PickerMode {
        CAMERA,
        GALLERY
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        simCountryName = telephonyManager.getSimCountryIso();
        Log.d(TAG, "countryName: " + simCountryName);

        categorySpinner = findViewById(R.id.categorySpinner);

        Intent intent = getIntent();
        if (intent != null) {
            from = intent.getExtras().getString("from");
            if (from.equals("social")) {
                userName = intent.getExtras().getString("name");
                userEmail = intent.getExtras().getString("email");
                userPic = intent.getExtras().getString("pic");
                loginType = intent.getExtras().getString("loginType");
                appCompatEditTextList.get(0).setText(userName);
                appCompatEditTextList.get(4).setText(userEmail);
                appCompatEditTextList.get(4).setText(userEmail);
                Log.d(TAG, "userPic: " + userPic);
                if (userPic == null) {
                    from = "";
                } else {
                    Glide.with(this).load(userPic).centerCrop().placeholder(R.drawable.ic_user).into(userImage);
                }
            } else if (from.equals("verify")) {
                from = "";
                String phoneNumber = intent.getExtras().getString("number");
                appCompatEditTextList.get(3).setEnabled(false);
                appCompatEditTextList.get(3).setClickable(false);
                appCompatEditTextList.get(3).setText(phoneNumber);
            }
        }

        enablePermission();

        genderList.add("Select Gender");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Transgender");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genderList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = genderList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance(Locale.getDefault());
                // c.add(Calendar.YEAR, -12);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                dateOfBirth.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        getCategory();


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (categoryList.size() != 0 && categoryList != null && !categoryList.isEmpty()) {
                    categoryName = categoryList.get(i).getCategoryName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getCountry();

    }

    private void getCountry() {
        Call<CountryResponse> apiInterfaceCountry = apiInterface.getCountry();
        apiInterfaceCountry.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        if (response.body().getCountrydata().size() != 0 && response.body().getCountrydata() != null) {
                            setCountrySpinner(response.body().getCountrydata());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {

            }
        });
    }

    private void setCountrySpinner(List<CountrydataItem> countrydata) {
        ArrayAdapter<CountrydataItem> dataAdapter = new ArrayAdapter<CountrydataItem>(SignUpActivity.this,
                android.R.layout.simple_spinner_item, countrydata);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(dataAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String countryId = countrydata.get(position).getId();
                countryISO = countrydata.get(position).getCountryIso();
                countryName = countrydata.get(position).getCountryName();
                Log.d(TAG, "country: " + countryId + "---" + countryISO + "------" + countryName);
                if (countryId != null) {
                    getStateData(countryISO);
                }

                for (int i = 0; i < countrydata.size(); i++) {
                    if (countrydata.get(i).getCountryIso().toLowerCase().equals(simCountryName)) {
                        countrySpinner.setSelection(i);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getStateData(String countryId) {
        Call<StateDataResponse> apiInterfaceCountry = apiInterface.getState(countryId);
        apiInterfaceCountry.enqueue(new Callback<StateDataResponse>() {
            @Override
            public void onResponse(Call<StateDataResponse> call, Response<StateDataResponse> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        if (response.body().getStatedata().size() != 0 && response.body().getStatedata() != null) {
                            setStateSpinner(response.body().getStatedata());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<StateDataResponse> call, Throwable t) {

            }
        });
    }

    private void setStateSpinner(List<StatedataItem> statedata) {
        ArrayAdapter<StatedataItem> dataAdapter = new ArrayAdapter<StatedataItem>(SignUpActivity.this,
                android.R.layout.simple_spinner_item, statedata);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(dataAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stateId = statedata.get(position).getId();
                stateISO = statedata.get(position).getStateIso();
                stateName = statedata.get(position).getStateName();
                Log.d(TAG, "state: " + stateId + "---" + stateISO + "------" + stateName);
                if (stateId != null) {
                    getCityData(stateISO);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCityData(String stateISO) {
        Call<CityDataResponse> apiInterfaceCountry = apiInterface.getCity(countryISO, stateISO);
        apiInterfaceCountry.enqueue(new Callback<CityDataResponse>() {
            @Override
            public void onResponse(Call<CityDataResponse> call, Response<CityDataResponse> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        if (response.body().getCitydata().size() != 0 && response.body().getCitydata() != null) {
                            setCitySpinner(response.body().getCitydata());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CityDataResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });

    }

    private void setCitySpinner(List<CitydataItem> citydata) {
        List<CitydataItem> citydataItemList = new ArrayList<>();
        citydataItemList.addAll(citydata);
        ArrayAdapter<CitydataItem> dataAdapter = new ArrayAdapter<CitydataItem>(SignUpActivity.this,
                android.R.layout.simple_spinner_item, citydataItemList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(dataAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityId = citydataItemList.get(position).getId();
                cityName = citydataItemList.get(position).getCity();
                Log.d(TAG, "city: " + cityId + "---" + cityName + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick({R.id.signUp, R.id.userImage, R.id.addImage})
    public void onClickViewed(View view) {
        switch (view.getId()) {
            case R.id.signUp: {
                if (App.isOnline()) {
                    if (appCompatEditTextList.get(0).getText().toString().isEmpty()) {
                        appCompatEditTextList.get(0).setError("Field is required*");
                    } else {
                        if (appCompatEditTextList.get(1).getText().toString().isEmpty()) {
                            appCompatEditTextList.get(1).setError("Field is required*");
                        } else {
                            if (categoryName.equalsIgnoreCase("Select Category")) {
                                Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
                            } else {
                                if (TextUtils.isEmpty(appCompatEditTextList.get(3).getText().toString())) {
                                    appCompatEditTextList.get(3).setError("Field is required*");
                                } else {
                                    if (appCompatEditTextList.get(3).getText().length() != 10) {
                                        appCompatEditTextList.get(3).setError("Enter correct length of Mobile Number");
                                    } else {
                                        if (appCompatEditTextList.get(3).getText().charAt(0) == '6' || appCompatEditTextList.get(3).getText().charAt(0) == '7' || appCompatEditTextList.get(3).getText().charAt(0) == '8' || appCompatEditTextList.get(3).getText().charAt(0) == '9') {
                                            if (appCompatEditTextList.get(4).getText().toString().isEmpty()) {
                                                appCompatEditTextList.get(4).setError("Field is required*");
                                            } else {
                                                if (isValidEmail(appCompatEditTextList.get(4).getText())) {
                                                    if (selectedGender.equalsIgnoreCase("Select Gender")) {
                                                        OwnerGlobal.toast(SignUpActivity.this, "Select Gender");
                                                    } else {
                                                        UpdateProfileService(profileUpdate(), getImg());
                                                    }
                                                } else {
                                                    appCompatEditTextList.get(4).setError("Enter Correct Email");
                                                }
                                            }
                                        } else {
                                            appCompatEditTextList.get(3).setError("Enter correct Mobile Number");
                                        }

                                    }
                                }
                            }
                        }
                    }
                } else {
                    OwnerGlobal.toast(this, "No Internet Connection");
                }
                break;
            }
            case R.id.addImage: {
                showImageSelectionDialog();
                break;
            }
        }
    }

    public void UpdateProfileService(Map<String, RequestBody> stringRequestBodyMap, MultipartBody.Part img) {
        if (App.getInstance().isOnline()) {
            // if (isValid()) {
            progressDialog.show();
            Call<UserDetailResponse> call = apiInterface.signupProfile(stringRequestBodyMap, img);
            call.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                    progressDialog.dismiss();
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(SignUpActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            PrefConnect.writeBoolean(SignUpActivity.this, Constants.USER_LOGGED, true);
                            PrefConnect.writeBoolean(SignUpActivity.this, Constants.GUEST_USER, false);
                            PrefConnect.writeString(SignUpActivity.this, Constants.USERID, response.body().getUserdata().getId());
                            PrefConnect.writeString(SignUpActivity.this, Constants.USERNAME, response.body().getUserdata().getUserName());
                            PrefConnect.writeString(SignUpActivity.this, Constants.USERPIC, response.body().getUserdata().getImg());
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            toast(SignUpActivity.this, response.body().getMessage());
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SignUpActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SignUpActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
            //}
        } else {
            OwnerGlobal.networkToast(SignUpActivity.this);
        }
    }

    public Map<String, RequestBody> profileUpdate() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("user_name", WebUtils.createRequest(appCompatEditTextList.get(0).getText().toString()));
        map.put("channel_name", WebUtils.createRequest(appCompatEditTextList.get(1).getText().toString()));
        map.put("category", WebUtils.createRequest(categoryName));
        map.put("tag_line", WebUtils.createRequest(appCompatEditTextList.get(2).getText().toString()));
        map.put("number", WebUtils.createRequest(appCompatEditTextList.get(3).getText().toString()));
        map.put("email", WebUtils.createRequest(appCompatEditTextList.get(4).getText().toString()));
        map.put("dob", WebUtils.createRequest(dateOfBirth.getText().toString()));
        map.put("gender", WebUtils.createRequest(selectedGender));
        map.put("country", WebUtils.createRequest(countryName));
        map.put("state", WebUtils.createRequest(stateName));
        map.put("city", WebUtils.createRequest(cityName));
        map.put("device_id", WebUtils.createRequest(AppUtil.getDeviceId(this)));
        map.put("fcm_token", WebUtils.createRequest(AppUtil.getFCMToken(this)));
        return map;
    }

    public MultipartBody.Part getImg() {
        return WebUtils.getImagePart("img", file);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //open image picker
    private void showImageSelectionDialog() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    pickerMode = PickerMode.CAMERA;
                    if (pickerMode != null) {
                        cameraIntent();
                    }
                    dialog.dismiss();
                } else if (options[item].equals("Choose From Gallery")) {
                    pickerMode = PickerMode.GALLERY;
                    if (pickerMode != null) {
                        galleryIntent();
                    }
                    dialog.dismiss();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        filePath = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        startActivityForResult(intent, CAMERA_RESULT_CODE);
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), GALLERY_RESULT_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_RESULT_CODE) {
            if (filePath != null) {
                try {
                    startCropImageActivity(filePath);
                } catch (Exception e) {
                    Log.d(TAG, "exception: " + e.toString());
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri cropImage = result.getUri();
                Log.d(TAG, "Crop Image Path: " + cropImage);
                if (filePath != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), cropImage);
                        _filePath = PathUtil.getPath(SignUpActivity.this, cropImage);
                        Log.d(TAG, "FileSize After Crop: " + getFileSize(_filePath));
                        if (_filePath != null) {
                            String filePath = SiliCompressor.with(SignUpActivity.this).compress(_filePath);
                            Log.d(TAG, "FileSize After Compress: " + getFileSize(filePath));
                            file = new File(filePath);
                            if (file.exists()) {
                                from = "";
                                Log.d(TAG, "FilePath: " + file.getAbsolutePath());
                                Glide.with(SignUpActivity.this).load(file.getAbsoluteFile()).into(userImage);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == GALLERY_RESULT_CODE && data != null) {
            filePath = data.getData();
            if (filePath != null) {
                startCropImageActivity(filePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startCropImageActivity(Uri filePath) {
        // start cropping activity for pre-acquired image saved on the device
        CropImage.activity(filePath)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    public long getFileSize(String filepath) {
        File file = new File(filepath);
        if (file.exists())
            return file.length();
        else
            return 0;
    }

/*    @OnItemSelected(R.id.gender_spinner)
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedGender = parent.getItemAtPosition(pos).toString();
        Log.d(TAG, "onItemSelected: "+ge);
    }*/

    public void getCategory() {
        if (App.isOnline()) {
            Call<CategoryResponse> userResponseCall = apiInterface.getswagtubecategory();
            userResponseCall.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    if (response.code() == Constants.SUCCESS) {
                        if (response.body().getStatus().equals("1")) {
                            categoryList = response.body().getCategory();
                            if (categoryList.size() != 0 && categoryList != null && !categoryList.isEmpty()) {
                                categoryList.add(0, new CategoryItem("Select Category"));
                                setCategoryAdapter(categoryList);
                            }
                        }
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(SignUpActivity.this, jObjError.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(SignUpActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
    }

    private void setCategoryAdapter(List<CategoryItem> categoryList) {
        ArrayAdapter<CategoryItem> arrayAdapter = new ArrayAdapter<CategoryItem>(SignUpActivity.this, android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
    }

}