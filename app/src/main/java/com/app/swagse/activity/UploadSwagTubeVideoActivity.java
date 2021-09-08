package com.app.swagse.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.adapter.SwagTubeAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.FileUploadNotification;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.helper.ProgressRequestBody;
import com.app.swagse.model.category.CategoryItem;
import com.app.swagse.model.category.CategoryResponse;
import com.app.swagse.model.swagTube.SwagTubeResponse;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.app.swagse.model.userDetails.UserDetailResponse;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.app.swagse.utils.WebUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;
import static com.google.android.exoplayer2.upstream.cache.CacheDataSink.DEFAULT_BUFFER_SIZE;

public class UploadSwagTubeVideoActivity extends BaseActivity implements ProgressRequestBody.UploadCallbacks {

    private static final int GALLERY = 001;
    private static final String VIDEO_DIRECTORY = "/swagtube_video";
    private static final String TAG = UploadSwagTubeVideoActivity.class.getSimpleName();
    AppCompatButton chooseVideo;
    VideoView showVideo;
    AppCompatImageView showVideoThumbnail;
    private ProgressDialog progressDialog;
    private Api apiInterface;
    AppCompatSpinner categorySpinner;
    private List<CategoryItem> categoryList;
    AppCompatButton uploadSwagTubePost;
    String selectedVideoPath = null;
    AppCompatEditText addSwagTubeHagTag, addSwagTubeDes, addSwagTubeTitle;
    String categoryName;
    Bitmap bitmap;
    FileUploadNotification fileUploadNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_swag_tube_video);
        apiInterface = RetrofitClient.getInstance().getApi();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        enablePermission();

        getSupportActionBar().setTitle("Upload SwagTube Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showVideo = findViewById(R.id.showVideo);
        categorySpinner = findViewById(R.id.categorySpinner);
        chooseVideo = findViewById(R.id.chooseVideo);
        showVideoThumbnail = findViewById(R.id.showVideoThumbnail);
        uploadSwagTubePost = findViewById(R.id.uploadSwagTubePost);
        addSwagTubeHagTag = findViewById(R.id.addSwagTubeHagTag);
        addSwagTubeDes = findViewById(R.id.addSwagTubeDes);
        addSwagTubeTitle = findViewById(R.id.addSwagTubeTitle);

        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });

        getCategory();

        uploadSwagTubePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefConnect.readBoolean(UploadSwagTubeVideoActivity.this, Constants.GUEST_USER, false)) {
                    startActivity(new Intent(UploadSwagTubeVideoActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (isValid()) {
                        OwnerGlobal.toast(UploadSwagTubeVideoActivity.this, "Video is Uploading in Background");
                        UpdateProfileService(uploadSwagTubePost(), getImg());
                    }
                }
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryName = categoryList.get(i).getCategoryName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("result", "" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            Log.d("what", "cancle");
            return;
        }
        if (requestCode == GALLERY) {
            Log.d("what", "gale");
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    showVideo.setVisibility(View.VISIBLE);
                    selectedVideoPath = getPath(contentURI);
                    Log.d("path", selectedVideoPath);
                    saveVideoToInternalStorage(selectedVideoPath);
                    showVideo.setVideoURI(contentURI);
                    showVideo.requestFocus();
                    showVideo.isPlaying();
/*
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showVideo.pause();
                        }
                    }, 2000);*/
                    if (selectedVideoPath != null) {
                        bitmap = ThumbnailUtils.createVideoThumbnail(new File(selectedVideoPath).getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        Log.d(TAG, "onActivityResult: " + bitmap);
                        showVideoThumbnail.setVisibility(View.VISIBLE);
                        Glide.with(UploadSwagTubeVideoActivity.this).load(bitmap).into(showVideoThumbnail);
                    }
                }
            }
        }
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private void saveVideoToInternalStorage(String filePath) {
        File newfile;
        try {
            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }
            if (currentFile.exists()) {
                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v("vii", "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

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
                            toast(UploadSwagTubeVideoActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(UploadSwagTubeVideoActivity.this);
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
        ArrayAdapter<CategoryItem> arrayAdapter = new ArrayAdapter<CategoryItem>(UploadSwagTubeVideoActivity.this, android.R.layout.simple_spinner_item, categoryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);
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

    public void UpdateProfileService(Map<String, RequestBody> stringRequestBodyMap, MultipartBody.Part img) {
        if (App.getInstance().isOnline()) {
            // if (isValid()) {
//            progressDialog.show();
            fileUploadNotification = new FileUploadNotification(UploadSwagTubeVideoActivity.this);
            Call<UserDetailResponse> call = apiInterface.uploadSwagTubeVideo(stringRequestBodyMap, img);
            call.enqueue(new Callback<UserDetailResponse>() {
                @Override
                public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
//                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response.errorBody());
                    if (response.code() == Constants.SUCCESS) {
                        Toast.makeText(UploadSwagTubeVideoActivity.this, "Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (response.code() == Constants.FAILED) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            toast(UploadSwagTubeVideoActivity.this, jObjError.getString("response_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (response.code() == Constants.UNAUTHORIZED) {
                        OwnerGlobal.LoginRedirect(UploadSwagTubeVideoActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
            //}
        } else {
            OwnerGlobal.networkToast(UploadSwagTubeVideoActivity.this);
        }
    }

    private boolean isValid() {
        if (addSwagTubeTitle.getText().toString().isEmpty()) {
            addSwagTubeTitle.setError("Enter Video Title");
            return false;
        }

        if (categoryName.equalsIgnoreCase("Select Category")) {
            OwnerGlobal.toast(UploadSwagTubeVideoActivity.this, "Select Category");
            return false;
        }

        if (selectedVideoPath == null) {
            OwnerGlobal.toast(UploadSwagTubeVideoActivity.this, "Choose Video File");
            return false;
        }

        long videoDurationTime = durationTime();
        Log.d(TAG, "videoDurationTime: " + videoDurationTime);
        long seconds = (videoDurationTime / 1000);
        Log.d(TAG, "seconds: " + seconds);
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(videoDurationTime);
//        long seconds = videoDurationTime / 60;
        Log.d(TAG, "isValid: " + seconds);
        String videoDurationValue = PrefConnect.readString(UploadSwagTubeVideoActivity.this, Constants.VIDEO_DURATION, "");
        int videoDurationValuesInt = Integer.parseInt(videoDurationValue);
        if (seconds > videoDurationValuesInt) {
//            Toast.makeText(this, "Show Subscription Module", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadSwagTubeVideoActivity.this);
            builder.setTitle("Choose Plan");
            builder.setMessage("You don't want to upload above " + videoDurationValuesInt + " sec video. if you want to upload more than " + videoDurationValuesInt + " sec video. So please select plan.");
            builder.setPositiveButton("Choose Plan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(UploadSwagTubeVideoActivity.this, SubcriptionPackageActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        // Get file from file name
        File file = new File(selectedVideoPath);
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        String videoSize = PrefConnect.readString(UploadSwagTubeVideoActivity.this, Constants.VIDEO_SIZE, "");
        int videoSizeValue = Integer.parseInt(videoSize);
        if (fileSizeInMB > videoSizeValue) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadSwagTubeVideoActivity.this);
            builder.setTitle("Choose Plan");
            builder.setMessage("You don't want to upload above " + videoSizeValue + " MB video. if you want to upload more than " + videoSizeValue + " MB video. So please select plan.");
            builder.setPositiveButton("Choose Plan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(UploadSwagTubeVideoActivity.this, SubcriptionPackageActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
    }

    public Map<String, RequestBody> uploadSwagTubePost() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("userid", WebUtils.createRequest(PrefConnect.readString(UploadSwagTubeVideoActivity.this, Constants.USERID, "")));
        map.put("post_title", WebUtils.createRequest(addSwagTubeTitle.getText().toString()));
        map.put("post_category", WebUtils.createRequest(categoryName));
        map.put("post_description", WebUtils.createRequest(addSwagTubeDes.getText().toString()));
        map.put("post_description", WebUtils.createRequest(addSwagTubeDes.getText().toString()));
        if (bitmap != null) {
            Log.d(TAG, "base64String: " + getBase64String(bitmap));
            map.put("thumbnail_video", WebUtils.createRequest(getBase64String(bitmap)));
        }
        return map;
    }

    public MultipartBody.Part getImg() {
        return WebUtils.getImagePart("file-input", new File(selectedVideoPath));
    }

    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }

    @Override
    public void onProgressUpdate(int percentage) {
        // set current progress
//        progressBar.setProgress(percentage);
        FileUploadNotification.updateNotification(String.valueOf(percentage), selectedVideoPath, "Uploading");
    }

    @Override
    public void onError() {
        // do something on error
    }

    @Override
    public void onFinish() {
        // do something on upload finished,
        // for example, start next uploading at a queue
//        progressBar.setProgress(100);
        FileUploadNotification.updateNotification("100", selectedVideoPath, "Uploaded");
    }

    public long durationTime() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(UploadSwagTubeVideoActivity.this, Uri.fromFile(new File(selectedVideoPath)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        Log.d(TAG, "timeInMillisec: " + timeInMillisec);
        retriever.release();
        return timeInMillisec;
    }

}