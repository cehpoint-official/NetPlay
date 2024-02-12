package com.dbug.netplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dbug.netplay.SplashActivity;
import com.dbug.netplay.ads.InterAdClickInterFace;
import com.dbug.netplay.ads.SessionAds;
import com.dbug.netplay.api.ApiInter;
import com.dbug.netplay.retofit.RetrofitClient;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dbug.netplay.R;
import com.dbug.netplay.databinding.ActivityItemViewBinding;
import com.dbug.netplay.model.AllPost;
import com.ironsource.mediationsdk.IronSource;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.dbug.netplay.SplashActivity.ADSKEY;
import static com.dbug.netplay.SplashActivity.MYPREFERENCE;
import static com.dbug.netplay.SplashActivity.UNITY_APP_APP_ID;
import static com.dbug.netplay.config.Constant.ADMIN_PANEL_URL;
import static com.dbug.netplay.config.Constant.SLIDER_IMAGE_URL;
import static com.dbug.netplay.config.Constant.VIDEO_IMAGE_URL;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewActivity extends AppCompatActivity implements InterAdClickInterFace, EasyPermissions.PermissionCallbacks {

    ActivityItemViewBinding activityItemViewBinding;
    String videoTitle;
    String videoUrl;
    String videoId;
    String videoThumbnail;
    String videoDuration;
    String videoType;
    String size;
    String totalViews;
    String categoryName;
    String dateTime;
    String videoDescription1;
    String vid;
    String fromIntent;
    int vidINT;

    AdRequest adRequest;

    ImageView thumbnailImageView;
    ImageView playButton;
    ImageView shearButton;
    AdView mAdView;
    FrameLayout adContainerView;
    TextView total_views_textView;

    ArrayList<AllPost.Post> post;

    int arrayIndex;


//    TextView videoDescription;
    WebView videoDescription;
    SessionAds sessionAds;

    SharedPreferences sharedPreferences;
    boolean isDark,isListGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("darkMode", false);
        isListGrid = sharedPreferences.getBoolean("listGrid", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        activityItemViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_item_view);
        this.sessionAds = new SessionAds(ItemViewActivity.this,this);
        Intent callingIntent = getIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        thumbnailImageView = findViewById(R.id.thumbnailImageView);
        total_views_textView = findViewById(R.id.total_views_textView);
        playButton = findViewById(R.id.play_buton);
//        shearButton = findViewById(R.id.shear_button);


        vid = callingIntent.getStringExtra("vid");

        vidINT = Integer.parseInt(vid);


        videoTitle = callingIntent.getStringExtra("video_title");
        videoUrl = callingIntent.getStringExtra("video_url");
        videoId = callingIntent.getStringExtra("video_id");
        videoThumbnail = callingIntent.getStringExtra("video_thumbnail");
        videoDuration = callingIntent.getStringExtra("video_duration");
        videoType = callingIntent.getStringExtra("video_type");
        size = callingIntent.getStringExtra("size");
        totalViews = callingIntent.getStringExtra("total_views");
        categoryName = callingIntent.getStringExtra("category_name");
        dateTime = callingIntent.getStringExtra("date_time");
        videoDescription1 = callingIntent.getStringExtra("video_description");
        fromIntent = callingIntent.getStringExtra("from");
        loadData();

        activityItemViewBinding.titleActivityItemView.setText(videoTitle);
        activityItemViewBinding.categoryNameTextView.setText(categoryName);
        activityItemViewBinding.totalViewsTextView.setText(totalViews + " views");
        String splitDate[] = dateTime.split(" ");
        activityItemViewBinding.dateTextView.setText(splitDate[0]);
        activityItemViewBinding.timeTextView.setText(splitDate[1]);


        //webView

        videoDescription = findViewById(R.id.video_description);
        videoDescription.setBackgroundColor(Color.TRANSPARENT);
        videoDescription.setFocusableInTouchMode(false);
        videoDescription.setFocusable(false);
        videoDescription.getSettings().setDefaultTextEncodingName("UTF-8");


        //web view
        WebSettings webSettings = videoDescription.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
//        webSettings.setFixedFontFamily("res/font/satoshi_bold.ttf");

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = videoDescription1;

        String bgParagraph;
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/satoshi_bold.ttf");

        bgParagraph = "<style type=\"text/css\">@font-face {font-family:MyFont;src:url(\"file:///android_asset/font/satoshi_regular.ttf\")}body{font-family:MyFont; color: #6A6E7D;text-align: justify;}";
        String text = "<html><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bgParagraph
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";
        videoDescription.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        activityItemViewBinding.favButton.setOnClickListener(v -> {
            post.add(new AllPost.Post(vidINT, videoTitle, videoUrl, videoId, videoThumbnail, videoDuration,
                    videoDescription1, videoType, size, Integer. parseInt(totalViews), dateTime, categoryName,fromIntent));

            activityItemViewBinding.favButton.setVisibility(View.GONE);
            activityItemViewBinding.favButtonFull.setVisibility(View.VISIBLE);
            saveData();
            Toast.makeText(ItemViewActivity.this, "Add to favorite", Toast.LENGTH_SHORT).show();
            loadData();
        });
        activityItemViewBinding.favButtonFull.setOnClickListener(v -> {
            activityItemViewBinding.favButton.setVisibility(View.VISIBLE);
            activityItemViewBinding.favButtonFull.setVisibility(View.GONE);
            post.remove(arrayIndex);
            saveData();
            loadData();
            Toast.makeText(ItemViewActivity.this, "Remove from favorite", Toast.LENGTH_SHORT).show();

        });


        Log.d("sdfsdf", videoType+"  onCreate: "+ADMIN_PANEL_URL + VIDEO_IMAGE_URL + videoThumbnail);
        if (videoType.equals("Url")) {
            Glide.with(ItemViewActivity.this)
                    .load(ADMIN_PANEL_URL + VIDEO_IMAGE_URL + videoThumbnail)
                    .fitCenter()
                    .into(thumbnailImageView);


        } else if (videoType.equals("Upload")) {

            if(fromIntent.equals("video")) {
                Glide.with(ItemViewActivity.this)
                        .load(ADMIN_PANEL_URL + VIDEO_IMAGE_URL + videoThumbnail)
                        .fitCenter()
                        .into(thumbnailImageView);
            }else if(fromIntent.equals("slider")){
                Glide.with(ItemViewActivity.this)
                        .load(ADMIN_PANEL_URL + SLIDER_IMAGE_URL + videoThumbnail)
                        .fitCenter()
                        .into(thumbnailImageView);
            }
        } else if(videoType.equals("Youtube")){
            Glide.with(ItemViewActivity.this)
                    .load("https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg")
                    .fitCenter()
                    .into(thumbnailImageView);
        }else {
            if(fromIntent.equals("video")) {
                Glide.with(ItemViewActivity.this)
                        .load(ADMIN_PANEL_URL + VIDEO_IMAGE_URL + videoThumbnail)
                        .fitCenter()
                        .into(thumbnailImageView);
            }else if(fromIntent.equals("slider")){
                Glide.with(ItemViewActivity.this)
                        .load(ADMIN_PANEL_URL + SLIDER_IMAGE_URL + videoThumbnail)
                        .fitCenter()
                        .into(thumbnailImageView);
            }
        }

        playButton.setOnClickListener(v -> {
            if (fromIntent.equals("video")){
                viewContPost(vid);
            }else {
                viewContSliderPost(vid);
            }
            if(videoType.toLowerCase().contains("radio") || categoryName.toLowerCase().contains("radio")){
                permissionRadio();
            }else {
                Intent intent = new Intent(ItemViewActivity.this, PlayerActivity.class);
                intent.putExtra("url", videoUrl);
                intent.putExtra("video_type", videoType);
                intent.putExtra("video_id", videoId);
                intent.putExtra("from", fromIntent);
                startActivity(intent);
            }
        });

//        shearButton.setOnClickListener(v -> {
//            Intent myIntent = new Intent(Intent.ACTION_SEND);
//            myIntent.setType("text/plain");
//            String body = videoTitle + "\n" + videoDuration + "\n" + videoUrl;
//            String sub = "Your Subject";
//            myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
//            myIntent.putExtra(Intent.EXTRA_TEXT, body);
//            startActivity(Intent.createChooser(myIntent, "Share Using"));
//        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adContainerView = findViewById(R.id.adView);
        if (sharedPreferences.getString(ADSKEY, "6").equals("6")) {
            String unityGameID = sharedPreferences.getString(UNITY_APP_APP_ID, "");
            Boolean testMode = true;
            Boolean enableLoad = true;
            String adUnitId = "Banner_Android";
            // Listener for banner events:
            UnityAds.initialize(this, unityGameID, testMode);

            BannerView bannerView  = new BannerView(this,adUnitId,new UnityBannerSize(320, 50));
            bannerView.load();
            adContainerView.addView(bannerView);
        }else {
            loadBanner();
        }

        activityItemViewBinding.backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        sessionAds.show();
    }

    //View Count
    public void viewContPost(String containId) {
        ApiInter apiInter = RetrofitClient.getRetrofit().create(ApiInter.class);
        apiInter.setPostViewCount(containId)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            //Empty
                            Log.d("ViewCountEpisod", "onResponse:   sss");
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        //Empty
                        Log.d("ViewCountEpisod", "onFailure: " + t.getMessage());
                    }
                });
    }

    public void viewContSliderPost(String containId) {
        ApiInter apiInter = RetrofitClient.getRetrofit().create(ApiInter.class);
        apiInter.setPostViewCountSlider(containId)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            //Empty
                            Log.d("ViewCountEpisod", "onResponse:   sss");
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        //Empty
                        Log.d("ViewCountEpisod", "onFailure: " + t.getMessage());
                    }
                });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(post);
        editor.putString("test", json);
        editor.apply();

    }

    private void loadData() {
        int count = 0;
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("test", null);
        Type type = new TypeToken<ArrayList<AllPost.Post>>() {
        }.getType();
        post = gson.fromJson(json, type);

        if (post == null) {
            post = new ArrayList<>();

        } else {
            for (AllPost.Post number : post) {
                if (number.getVid() == vidINT) {
                    arrayIndex = count;
                    activityItemViewBinding.favButton.setVisibility(View.GONE);
                    activityItemViewBinding.favButtonFull.setVisibility(View.VISIBLE);
                }
                count = count + 1;
            }
        }
    }

    @Override
    public void onAdClick() {
        Toast.makeText(this, "onAdClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdFailed() {

    }
    private void loadBanner() {
        mAdView = new AdView(this);
        adContainerView.addView(mAdView);
        mAdView.setAdUnitId(sharedPreferences.getString(SplashActivity.BANNER_ADS, "ca-app-pub-3940256099942544/6300978111"));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d("testAdsCheck", adError.toString());
            }

            @Override
            public void onAdImpression() {

            }

            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdOpened() {
            }
        });
    }

    private AdSize getAdSize() {
        //Determine the screen width to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    private void intentRadio(){
        Intent intent = new Intent(ItemViewActivity.this, RadioActivity.class);
        intent.putExtra("url", videoUrl);
        intent.putExtra("radio_title", videoTitle);
        intent.putExtra("video_type", videoType);
        intent.putExtra("video_id", videoId);
        intent.putExtra("from", fromIntent);
        startActivity(intent);
    }
    @AfterPermissionGranted(123)
    private void permissionRadio() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            intentRadio();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions because this and that",
                    123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //Empty
        intentRadio();
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //Empty
        Toast.makeText(this, "play Radio need permission!", Toast.LENGTH_SHORT).show();
    }
}

