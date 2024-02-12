package com.dbug.netplay;

import static com.dbug.netplay.FirebaseNotificationService.NOTIFICATION_CHANNEL_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;


import com.dbug.netplay.api.ApiInter;
import com.dbug.netplay.config.Constant;
import com.dbug.netplay.databinding.ActivitySplashBinding;
import com.dbug.netplay.model.Ads;
import com.dbug.netplay.model.FCMItem;
import com.dbug.netplay.retofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static final String OpenAds = "OpenAds";
    public static final String MYPREFERENCE = "mypref";
    public static final String ADSKEY = "adsKey";

    public static final String ADMOB_APP_ID = "admob_app_id";
    public static final String BANNER_ADS = "banner_ads";
    public static final String INTER_ADS = "inter_ads";
    public static final String NATIVE_ADS = "admob_native";
    public static final String REWARD_ADS = "admob_reward";
    public static final String APPNEX_INTER = "appnex_inter";
    public static final String APPNEX_BANNER = "appnex_banner";
    public static final String UNITY_APP_APP_ID = "unity_app_id";
    public static final String STARTAPP_APP_ID = "startapp_app_id";
    public static final String IRON_APP_ID = "iron_appKey";
    public static final String GBANNER_ID = "gbanner_id";
    public static final String GINTERS_ID = "ginters_id";
    public static final String GNATIVE_ID = "gnative_id";
    public static final String FBBANNER_ID = "fbbanner_id";
    public static final String FBINTERS_ID = "fbinters_id";
    public static final String FBNATIVE_ADS = "fb_native";
    public static final String FBREWARD_ADS = "fb_reward";

    public static final String ADS_INTERVAL = "ads_interval";
    public static final String MAIN_ADS_INTERVAL = "main_ads_interval";


    public static final String ITEM_CLICK = "item_click";
    public static Boolean checkFirstTime;
    private int _adsType = 0;
    private boolean isDark;
    ActivitySplashBinding activitySplashBinding;

    SharedPreferences sharedpreferences;
    AlertDialog.Builder builder;
    AlertDialog alert;
    boolean notificationEnable;
    private static final int RESULT_ACTION_NOTIFICATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        activitySplashBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedpreferences = getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        isDark = sharedpreferences.getBoolean("darkMode", false);
        if (isDark) {
            activitySplashBinding.splashImg.setBackground(ContextCompat.getDrawable(this,R.drawable.splash_screen));
        } else {
            activitySplashBinding.splashImg.setBackground(ContextCompat.getDrawable(this,R.drawable.splash_screen_light));
            activitySplashBinding.appName.setTextColor(ContextCompat.getColor(this,R.color.bg_screen3));
//            activitySplashBinding.appName.setShadowLayer(0, 0, 5, ContextCompat.getColor(this,R.color.white));
        }
        SharedPreferences sharedPreference = getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        checkFirstTime = sharedPreference.getBoolean("FCM_TOKEN", true);
        if (checkFirstTime) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    saveData(token, sharedPreference);
                    Log.d("FCMTOKEN", token);
                }
            });
        }else {
            Log.d("FCMTOKEN", "previously saved");
            Log.d("FCMTOKEN", ""+sharedPreference.getString("FCM_TOKEN_KEY", ""));
        }
    }
    public  void saveData(String token, SharedPreferences sharedPreferences){
        String phoneModel = android.os.Build.MODEL;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("FCM_TOKEN", false);
        editor.putString("FCM_TOKEN_KEY", token);
        editor.apply();
        ApiInter apiInter = RetrofitClient.getRetrofit().create(ApiInter.class);
        Call<FCMItem> call = apiInter.postFCMToken(token,phoneModel);
        call.enqueue(new Callback<FCMItem>() {
            @Override
            public void onResponse(Call<FCMItem> call, Response<FCMItem> response) {
                Log.d("FCMTOKEN", "onResponse"+response.code());
                Log.d("FCMTOKEN", "onResponse: name"+response.body().getFcm_token());
                Log.d("FCMTOKEN", "onResponse: id"+response.body().getId());
            }

            @Override
            public void onFailure(Call<FCMItem> call, Throwable t) {
                Log.d("FCMTOKEN", "onFailure"+t.getMessage());
            }
        });
    }
    private void adsfromLocal() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(ADMOB_APP_ID, getResources().getString(R.string.admob_app_id));
        editor.putString(ADSKEY, getResources().getString(R.string.inter_ads));
        editor.putString(BANNER_ADS, getResources().getString(R.string.banner_ads));
        editor.putString(GBANNER_ID, getResources().getString(R.string.gbanner_id));
        editor.putString(GINTERS_ID, getResources().getString(R.string.ginters_id));
        editor.putString(GNATIVE_ID, getResources().getString(R.string.gnative_id));
        editor.putString(FBBANNER_ID, getResources().getString(R.string.fbbanner_id));
        editor.putString(FBINTERS_ID, getResources().getString(R.string.fbinters_id));
        editor.commit();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Your Code
            Intent intent = new Intent(SplashActivity.this, MainIntroActivity.class);
            startActivity(intent);
            finish();

        }, 3000);
    }

    private void adsFromServer() {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        ApiInter ads = RetrofitClient.getRetrofit().create(ApiInter.class);
        ads.getAds()
                .enqueue(new Callback<Ads>() {
                    @Override
                    public void onResponse(Call<Ads> call, Response<Ads> response) {
                        if (response.isSuccessful()) {
                            Ads ads1 = response.body();
                            editor.putString(ADMOB_APP_ID, getResources().getString(R.string.admob_app_id));
                            editor.putString(ADSKEY, ads1.getData().getAdTypes());
                            editor.putString(BANNER_ADS, ads1.getData().getAdmobBanner());
                            editor.putString(INTER_ADS, ads1.getData().getAdmobInter());
                            editor.putString(NATIVE_ADS, ads1.getData().getAdmobNative());
                            editor.putString(REWARD_ADS, ads1.getData().getAdmobReward());
                            editor.putString(OpenAds, Constant.OPEN_ADS);

                            editor.putString(UNITY_APP_APP_ID, ads1.getData().getUnityAppIdGameId());
                            editor.putString(STARTAPP_APP_ID, ads1.getData().getStartappAppId());

                            editor.putString(APPNEX_INTER, ads1.getData().getAppnextPlacementId());

                            editor.putString(IRON_APP_ID, ads1.getData().getIronAppKey());

                            editor.putString(GNATIVE_ID, ads1.getData().getAdmobNative());
                            editor.putString(GINTERS_ID, ads1.getData().getAdmobInter());
                            editor.putString(GBANNER_ID, ads1.getData().getAdmobBanner());

                            editor.putString(FBBANNER_ID, ads1.getData().getFbBanner());
                            editor.putString(FBINTERS_ID, ads1.getData().getFbInter());
                            editor.putString(FBNATIVE_ADS, ads1.getData().getFbNative());
                            editor.putString(FBREWARD_ADS, ads1.getData().getFbReward());

                            editor.putInt(ADS_INTERVAL, ads1.getData().getIndustrialInterval());
                            editor.putInt(MAIN_ADS_INTERVAL, ads1.getData().getIndustrialInterval());
                            editor.commit();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                // Your Code
                                Intent intent = new Intent(SplashActivity.this, MainIntroActivity.class);
                                startActivity(intent);
                                finish();

                            }, 3000);

                        }
                    }

                    @Override
                    public void onFailure(Call<Ads> call, Throwable t) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(ADMOB_APP_ID, getResources().getString(R.string.admob_app_id));
                        editor.putString(ADSKEY, getResources().getString(R.string.inter_ads));
                        editor.putString(BANNER_ADS, getResources().getString(R.string.banner_ads));
                        editor.putString(GBANNER_ID, getResources().getString(R.string.gbanner_id));
                        editor.putString(GINTERS_ID, getResources().getString(R.string.ginters_id));
                        editor.putString(GNATIVE_ID, getResources().getString(R.string.gnative_id));
                        editor.putString(FBBANNER_ID, getResources().getString(R.string.fbbanner_id));
                        editor.putString(FBINTERS_ID, getResources().getString(R.string.fbinters_id));
                        editor.putString(OpenAds, Constant.OPEN_ADS);
                        editor.commit();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Your Code
                            Intent intent = new Intent(SplashActivity.this, MainIntroActivity.class);
                            startActivity(intent);
                            finish();

                        }, 3000);


                    }
                });
    }

    @Override
    protected void onResume() {
        notificationEnable = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!notificationEnable) {
            showNotificationDialog();
        } else {
            try {
                if (alert.isShowing()){
                    alert.dismiss();
                }
            }catch (Exception e){

            }finally {
                if (Constant.ADS_TYPE.toLowerCase(Locale.ROOT).contains("server")) {
                    adsFromServer();
                } else {
                    adsfromLocal();
                }
            }
        }
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showNotificationDialog() {
        builder.setMessage("Do you want to use this application ? must be enable notification.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                            .putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID);
                    startActivityForResult(settingsIntent, RESULT_ACTION_NOTIFICATION);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                    finish();
                });
        //Creating dialog box
        alert = builder.create();
        //Setting the title manually
        alert.setTitle("Allow notification ");
        alert.show();

        return;
    }
}