package com.app.swagse.SimpleClasses;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.backends.pipeline.Fresco;



/**
 * Created by AQEEL on 3/18/2019.
 */

public class PikPok extends Application {

    public static String shareurl;
    public static String downloadurl;
    public static String videoId;
    public static String videoUsername;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
//        FirebaseApp.initializeApp(this);
//        AudienceNetworkAds.initialize(this);

    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        PikPok app = (PikPok) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .build();
    }

}
