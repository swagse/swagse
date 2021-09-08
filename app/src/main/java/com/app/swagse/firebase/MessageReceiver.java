package com.app.swagse.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.swagse.LoginActivity;
import com.app.swagse.R;
import com.app.swagse.SubscriberUserProfileActivity;
import com.app.swagse.activity.MainActivity;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.adapter.NotificationRecyclerViewAdapter;
import com.app.swagse.constants.Constants;
import com.app.swagse.sharedpreferences.PrefConnect;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MessageReceiver extends FirebaseMessagingService {

    private Intent intent;
    private String from, user_type;
    private Bitmap bitmap;
    private Random rand = new Random();
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    ImageView dlinksplash, dlinksplash1;
    LinearLayout linearLayout;
    private WebView webView;

    public MessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Map<String, String> params = remoteMessage.getData();
            if (params != null) {
                JSONObject object = new JSONObject(params);
                Log.e("JSON_OBJECT-----------", object.toString());
                if (object != null) {
                    String title = object.optString("title");
                    String body = object.optString("body");
                    String id = object.optString("id");
                    String hashtag = object.optString("hashtag");
                    String imageUrl = object.optString("image");
                    if (!imageUrl.equals("")) {
                        bitmap = getBitmapfromUrl(imageUrl);
                    }
                    if (id != null) {
                        String[] type = id.split("-");
                        user_type = type[0];
                        from = type[1];
                    }
                    Log.e("notify data---", title + "----------" + body + "===" + id);
                    if (bitmap == null) {
                        displayCustomNotificationForOrders(title, body, from, user_type, hashtag);
                    } else if (bitmap != null) {
                        displayCustomNotificationForImage(title, body, from, user_type, hashtag, bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayCustomNotificationForOrders(String title, String description, String from, String user_type, String hashtag) {
        try {
            if (!PrefConnect.readString(getApplicationContext(), Constants.USERID, "").equals("0") && !(PrefConnect.readString(getApplicationContext(), Constants.USERID, "").equals(""))) {
                if (from.equalsIgnoreCase("swagtube_followers")) {
                    startActivity(new Intent(getApplicationContext(), SwagTubeDetailsActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), user_type));
                } else if (from.equalsIgnoreCase("swagtube_post")) {
                    startActivity(new Intent(getApplicationContext(), SubscriberUserProfileActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), user_type));
                }
            } else {
                intent = new Intent(this, LoginActivity.class).putExtra("tag", "login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int rand_int1 = rand.nextInt(9000);
        Log.e("number", "" + rand_int1);
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description).setSummaryText(hashtag))
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify(rand_int1, notification);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description).setSummaryText(hashtag))
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(rand_int1, notificationBuilder.build());
        }
    }

    private void displayCustomNotificationForImage(String title, String description, String from, String user_type, String hashtag, Bitmap bitmap) {
        try {
            if (!PrefConnect.readString(getApplicationContext(), Constants.USERID, "").equals("0") && !(PrefConnect.readString(getApplicationContext(), Constants.USERID, "").equals(""))) {
                if (from.equalsIgnoreCase("swagtube_followers")) {
                    startActivity(new Intent(getApplicationContext(), SwagTubeDetailsActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), user_type));
                } else if (from.equalsIgnoreCase("swagtube_post")) {
                    startActivity(new Intent(getApplicationContext(), SubscriberUserProfileActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), user_type));
                }
            } else {
                intent = new Intent(this, LoginActivity.class).putExtra("tag", "login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int rand_int1 = rand.nextInt(9000);
        Log.e("number", "" + rand_int1);
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description).setSummaryText(hashtag))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify(rand_int1, notification);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    //.setColor(Color.parseColor("#f00000"))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description).setSummaryText(hashtag))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(rand_int1, notificationBuilder.build());
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
