package com.dbug.netplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.dbug.netplay.fragment.SettingsFragment;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.dbug.netplay.R;
import com.dbug.netplay.databinding.ActivityMainBinding;
import com.dbug.netplay.fragment.CategoryFragment;
import com.dbug.netplay.fragment.FavoriteFragment;
import com.dbug.netplay.fragment.HomeFragment;
import com.facebook.ads.AudienceNetworkAds;
import static com.dbug.netplay.SplashActivity.FBINTERS_ID;
import static com.dbug.netplay.SplashActivity.GINTERS_ID;
import static com.dbug.netplay.SplashActivity.ADSKEY;
import static com.dbug.netplay.SplashActivity.MYPREFERENCE;

import java.lang.ref.SoftReference;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    SharedPreferences sharedpreferences;

    private com.facebook.ads.InterstitialAd facebookInterstitialAd;
    private InterstitialAd mInterstitialAd;
    String viewSelection ;
    AdRequest adRequest;
    boolean isDark;
    public static SoftReference<MainActivity> reference;

    public static MainActivity getInstance() {
        return reference.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        isDark = sharedpreferences.getBoolean("darkMode", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        reference = new SoftReference<>(MainActivity.this);
        if (isDark) {
            activityMainBinding.bottomBg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.nav_bottom_bg_dark));
        } else {
            activityMainBinding.bottomBg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.nav_bottom_bg));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        prepareInterstitialAdAdmob();
        prepareInterstitialAd();



        AudienceNetworkAds.initialize(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_frame_layout,new HomeFragment()).commit();


        MobileAds.initialize(MainActivity.this);

        // BottomNavigation

        activityMainBinding.navHome.setOnClickListener(this::onClick);
        activityMainBinding.navCategory.setOnClickListener(this::onClick);
        activityMainBinding.navFav.setOnClickListener(this::onClick);
        activityMainBinding.navSettings.setOnClickListener(this::onClick);
    }



    private void prepareInterstitialAd() {
        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(MainActivity.this,sharedpreferences.getString(FBINTERS_ID, ""));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback

            }
        };

        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    public void onClick(View v) {
        Fragment tem = null;
        switch (v.getId()){
            case R.id.nav_home: tem = new HomeFragment();
                navIconColorChanged("home");
                adsShow();
                break;
            case R.id.nav_category: tem = new CategoryFragment("gon");
                navIconColorChanged("category");
                adsShow();
                break;
            case R.id.nav_fav: tem = new FavoriteFragment();
                navIconColorChanged("fav");
                adsShow();
                break;
            case R.id.nav_settings: tem = new SettingsFragment();
                navIconColorChanged("settings");
                adsShow();
                break;
            default: tem = new HomeFragment();
        }

        if (tem != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_frame_layout,tem).commit();
        }
    }

    public void navIconColorChanged(String s){
        if (s.contains("home")){
            activityMainBinding.navHome.setClickable(false);
            activityMainBinding.navCategory.setClickable(true);
            activityMainBinding.navFav.setClickable(true);
            activityMainBinding.navSettings.setClickable(true);
            activityMainBinding.navHomeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_active));
            activityMainBinding.navCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_category));
            activityMainBinding.navFavIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart));
            activityMainBinding.navSettingsIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_setting));
        }else if (s.contains("category")){
            activityMainBinding.navHome.setClickable(true);
            activityMainBinding.navCategory.setClickable(false);
            activityMainBinding.navFav.setClickable(true);
            activityMainBinding.navSettings.setClickable(true);
            activityMainBinding.navHomeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
            activityMainBinding.navCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_category_active));
            activityMainBinding.navFavIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart));
            activityMainBinding.navSettingsIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_setting));
        }else if (s.contains("fav")){
            activityMainBinding.navHome.setClickable(true);
            activityMainBinding.navCategory.setClickable(true);
            activityMainBinding.navFav.setClickable(false);
            activityMainBinding.navSettings.setClickable(true);
            activityMainBinding.navHomeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
            activityMainBinding.navCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_category));
            activityMainBinding.navFavIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_active));
            activityMainBinding.navSettingsIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_setting));
        }else if (s.contains("settings")){
            activityMainBinding.navHome.setClickable(true);
            activityMainBinding.navCategory.setClickable(true);
            activityMainBinding.navFav.setClickable(true);
            activityMainBinding.navSettings.setClickable(false);
            activityMainBinding.navHomeIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home));
            activityMainBinding.navCategoryIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_category));
            activityMainBinding.navFavIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart));
            activityMainBinding.navSettingsIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_setting_active));
        }
    }
    private void prepareInterstitialAdAdmob() {

        adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,sharedpreferences.getString(GINTERS_ID, ""), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error

                mInterstitialAd = null;
            }
        });

    }

    public void adsShow(){

        if(sharedpreferences.getString(ADSKEY, "").equals("Admob")){
            prepareInterstitialAdAdmob();
            prepareInterstitialAd();
            mInterstitialAd.show(MainActivity.this);
        } else if (sharedpreferences.getString(ADSKEY, "").equals("fb")){
            prepareInterstitialAdAdmob();
            prepareInterstitialAd();
            facebookInterstitialAd.show();


        }
        else if (sharedpreferences.getString(ADSKEY, "").equals("both")){
            if ( viewSelection == null ){
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    viewSelection = "fb";
                } else {
                    prepareInterstitialAdAdmob();
                    prepareInterstitialAd();
                }

            }else if (viewSelection.equals("fb")){

                if (facebookInterstitialAd.isAdLoaded()){
                    facebookInterstitialAd.show();
                    viewSelection = "admob";

                }else {
                    prepareInterstitialAdAdmob();
                    prepareInterstitialAd();
                }

            }
        }

    }
}