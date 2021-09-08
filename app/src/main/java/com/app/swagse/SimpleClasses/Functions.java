package com.app.swagse.SimpleClasses;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.swagse.R;
import com.bumptech.glide.Glide;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.googlecode.mp4parser.authoring.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by AQEEL on 2/20/2019.
 */

public class Functions {
    private static final String TAG = "Functions";

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String getSuffix(String value) {
        try {

            int count = Integer.parseInt(value);
            if (count < 1000) return "" + count;
            int exp = (int) (Math.log(count) / Math.log(1000));
            return String.format("%.1f %c",
                    count / Math.pow(1000, exp),
                    "kMGTPE".charAt(exp - 1));
        } catch (Exception e) {
            return value;
        }

    }

    public static void show_Alert(Context context, String title, String Message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    public static void show_Alert(Context context, String title, String Message, String postivebtn, String negitivebtn, final Callback callback) {

        new AlertDialog.Builder(context)
                .setTitle(null)
                .setMessage(Message)
                .setNegativeButton(negitivebtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.Responce("no");
                    }
                })
                .setPositiveButton(postivebtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        callback.Responce("yes");

                    }
                }).show();
    }

    public static Dialog dialog;

    public static void show_loader(Context context, boolean outside_touch, boolean cancleable) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog_loading_view);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_white_background));


        CamomileSpinner loader = dialog.findViewById(R.id.loader);
        loader.start();


        if (!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            dialog.setCancelable(false);

        dialog.show();
    }

    public static void cancel_loader() {
        if (dialog != null) {
            dialog.cancel();
        }
    }


    public static float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    public static void share_through_app(final Activity activity, final String link) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, link);
                activity.startActivity(Intent.createChooser(intent, ""));

            }
        }).start();
    }


    public static String bitmap_to_base64(Activity activity, Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static Bitmap base64_to_bitmap(String base_64) {
        Bitmap decodedByte = null;
        try {

            byte[] decodedString = Base64.decode(base_64, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {

        }
        return decodedByte;
    }


    public static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }


    public static void make_directry(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }


    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }


    // Bottom is all the Apis which is mostly used in app we have add it
    // just one time and whenever we need it we will call it

    public static void call_Api_For_like_video(final Activity activity,
                                               String video_id, String action,
                                               final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("video_id", video_id);
            parameters.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.call_Api(activity, Variables.likeDislikeVideo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.e(TAG, "Responce: likeDisLike resp " + resp);
                api_callBack.OnSuccess(resp);
            }
        });


    }


    public static void call_Api_For_Send_Comment(final Activity activity, String video_id, String comment, final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("video_id", video_id);
            parameters.put("comment", comment);

        } catch (JSONException e) {
            e.printStackTrace();
        }

      /*  ApiRequest.call_Api(activity, Variables.postComment, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                ArrayList<Comment_Get_Set> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Comment_Get_Set item = new Comment_Get_Set();
                            item.fb_id = itemdata.optString("fb_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");
                            item.video_id = itemdata.optString("id");
                            item.comments = itemdata.optString("comments");
                            item.created = itemdata.optString("created");

                            arrayList.add(item);
                        }

                        api_callBack.ArrayData(arrayList);

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }

            }
        });*/


    }

    public static void call_Api_For_get_Comment(final Activity activity, String video_id, final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        ApiRequest.call_Api(activity, Variables.showVideoComments, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                ArrayList<Comment_Get_Set> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Comment_Get_Set item = new Comment_Get_Set();
                            item.fb_id = itemdata.optString("fb_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");
                            item.video_id = itemdata.optString("id");
                            item.comments = itemdata.optString("comments");
                            item.created = itemdata.optString("created");
                            arrayList.add(item);
                        }

                        api_callBack.ArrayData(arrayList);

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });
*/

    }


    public static void call_Api_For_update_view(final Activity activity,
                                                String video_id) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("id", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.call_Api(activity, Variables.updateVideoView, parameters, null);


    }


    public static void call_Api_For_Follow_or_unFollow
            (final Activity activity,
             String fb_id,
             String followed_fb_id,
             String status,
             final API_CallBack api_callBack) {

        Functions.show_loader(activity, false, false);


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", fb_id);
            parameters.put("followed_fb_id", followed_fb_id);
            parameters.put("status", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.call_Api(activity, Variables.follow_users, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.OnSuccess(response.toString());

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });


    }


    public static void call_Api_For_Get_User_data
            (final Activity activity,
             String fb_id,
             final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", fb_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        ApiRequest.call_Api(activity, com.app.swagse.constants.Variables.get_user_data, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    Log.d("notiresp", resp);

                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.OnSuccess(response.toString());

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });

    }


    public static void call_Api_For_Delete_Video
            (final Activity activity,
             String video_id,
             final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.call_Api(activity, Variables.deleteVideo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        if (api_callBack != null)
                            api_callBack.OnSuccess(response.toString());

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    if (api_callBack != null)
                        api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }

            }
        });


    }


    public static Dialog indeterminant_dialog;

    public static void show_indeterminent_loader(Context context, boolean outside_touch, boolean cancleable) {

        indeterminant_dialog = new Dialog(context);
        indeterminant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indeterminant_dialog.setContentView(R.layout.item_indeterminant_progress_layout);
        indeterminant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));


        if (!outside_touch)
            indeterminant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            indeterminant_dialog.setCancelable(false);

        indeterminant_dialog.show();

    }


    public static void cancel_indeterminent_loader() {
        if (indeterminant_dialog != null) {
            indeterminant_dialog.cancel();
        }
    }


    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;

    public static void show_determinent_loader(Context context, boolean outside_touch, boolean cancleable) {

        determinant_dialog = new Dialog(context);
        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
        determinant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));

        determinant_progress = determinant_dialog.findViewById(R.id.pbar);

        if (!outside_touch)
            determinant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            determinant_dialog.setCancelable(false);

        determinant_dialog.show();

    }

    public static void show_loading_progress(int progress) {
        if (determinant_progress != null) {
            determinant_progress.setProgress(progress);

        }
    }


    public static void cancel_determinent_loader() {
        if (determinant_dialog != null) {
            determinant_progress = null;
            determinant_dialog.cancel();
        }
    }


    public static boolean checkstoragepermision(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {

            return true;
        }
    }


    // these function are remove the cache memory which is very helpfull in memmory managmet
    public static void deleteCache(Context context) {
        Glide.get(context).clearMemory();

        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public static String ChangeDate_to_today_or_yesterday(Context context,String date){
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal=Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ");
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            long difference=(current_cal.getTimeInMillis()- date_cal.getTimeInMillis())/1000;

            if(difference<86400)
            {
                if(current_cal.get(Calendar.DAY_OF_YEAR)-date_cal.get(Calendar.DAY_OF_YEAR)==0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    return sdf.format(d);
                }
                else
                    return "yesterday";
            }

            else if(difference<172800){
                return "yesterday";
            }

            else
                return (difference/86400)+" day ago";

        }catch (Exception e){

            return date;
        }


    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static Bitmap addStampToImage(Context context, Bitmap originalBitmap, String username) {


        int extraHeight = (int) (originalBitmap.getHeight() * 0.30);
        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth() + extraHeight + extraHeight + extraHeight + extraHeight + extraHeight,
                originalBitmap.getHeight() + extraHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(originalBitmap, 5, 5, null);


        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        String text = username;
        Paint pText = new Paint();
        pText.setColor(Color.WHITE);

        setTextSizeForWidth(pText, (int) (originalBitmap.getHeight() * 0.14), text);
        Rect bounds = new Rect();
        pText.getTextBounds(text, 0, text.length(), bounds);
        Rect textHeightWidth = new Rect();
        pText.getTextBounds(text, 0, text.length(), textHeightWidth);
        canvas.drawText(text, (canvas.getWidth() / 2) - (textHeightWidth.width() / 2),
                originalBitmap.getHeight() + (extraHeight / 2) + (textHeightWidth.height() / 2),
                pText);


//        canvas.drawText(text, 0,
//                0,
//                pText);


//        canvas.drawText(text, 20,
//                20,
//                pText);

        //imageView.setImageBitmap(newBitmap);

        return newBitmap;
    }

    private static void setTextSizeForWidth(Paint paint, float desiredHeight,
                                            String text) {

        final float testTextSize = 88f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredHeight / bounds.height();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize + 8);
        //paint.setTextSize(30);
        paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
    }


}
