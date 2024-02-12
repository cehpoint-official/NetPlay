package com.dbug.netplay.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.dbug.netplay.R;
import com.dbug.netplay.SplashActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;


public class MyApplication extends Application {

    SharedPreferences sharedPreferences ;
    String openAppCode;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(SplashActivity.MYPREFERENCE, MODE_PRIVATE);
        openAppCode = sharedPreferences.getString(SplashActivity.OpenAds, "");
        FirebaseApp.initializeApp(this);
        Log.d("sdljfhsd", "onCreate: "+openAppCode);
        MobileAds.initialize(this, initializationStatus -> new AppOpenManager(MyApplication.this,openAppCode));


        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(IjkPlayerFactory.create())
                .build());

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(getResources().getString(R.string.ONESIGNAL_APP_ID));

    }

    public static int getDisplaySize(Activity activity, String tag) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        if (tag.equals("h")){
            return displayHeight;
        }else {
            return displayWidth;
        }
    }
}
