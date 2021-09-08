package com.app.swagse.firebase;


import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

  public MyFirebaseInstanceIDService() {
    super();
  }

  @Override
  public void onNewToken(String token) {
    Log.d("guyfhidjaoskl", "Refreshed token: " + token);
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    Log.e("token firebase", "Refreshed token: " + refreshedToken);
  }
}
