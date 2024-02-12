package com.dbug.netplay.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dbug.netplay.config.Constant;
import com.dbug.netplay.R;
import com.dbug.netplay.utils.HTML5WebView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

public class PlayerActivity extends AppCompatActivity {


    YouTubePlayerView youTubePlayerView;
    String url;
    String videoType;
    String videoId;

    VideoView videoView;
    boolean webisVideo = true;
    FrameLayout webViewEm;
    RelativeLayout weblPlay;
    RelativeLayout webControlLayout;
    RelativeLayout backFavorit1;
    HTML5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mWebView = new HTML5WebView(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent callingIntent = getIntent();

        videoType = callingIntent.getStringExtra("video_type");
        url = callingIntent.getStringExtra("url");
        videoId = callingIntent.getStringExtra("video_id");

        youTubePlayerView = findViewById(R.id.youtube_player_view);
      //  exoPlayerView = findViewById(R.id.newExo);
        getLifecycle().addObserver(youTubePlayerView);
//new Player Update
        videoView = findViewById(R.id.player);
        videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);

        videoView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
            } else {
                // In portrait
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
            }

        });

        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(IjkPlayerFactory.create())
                .build());

        if(isEmbedURL(url)){
            String newUrl = extractURLFromEmbedCode(url);
            webisVideo = true;
            openWebActivity(newUrl);
        }else{
            switch (videoType) {
                case "Url": {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setUrl(url); //设置视频地址

                    StandardVideoController controller = new StandardVideoController(this);
                    controller.addDefaultControlComponent("", false);
                    videoView.setVideoController(controller);
                    videoView.setKeepScreenOn(true);//设置控制器

                    videoView.startFullScreen();
                    videoView.start();
                    youTubePlayerView.setVisibility(View.GONE);

                    break;
                }
                case "Upload": {
                    String url1 = Constant.ADMIN_PANEL_URL + Constant.ADMIN_PANEL_PATH +
                            "/public/upload/" + getIntent().getStringExtra("from") + "/" + url;
                    Log.d("checkUrls", "onCreate: " + url1);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setUrl(url1); //设置视频地址

                    StandardVideoController controller = new StandardVideoController(this);
                    controller.addDefaultControlComponent("", false);
                    videoView.setVideoController(controller);
                    videoView.setKeepScreenOn(true);//设置控制器

                    videoView.startFullScreen();
                    videoView.start();
                    youTubePlayerView.setVisibility(View.GONE);
                    break;
                }
                case "Youtube":
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    youTubePlayerView.setVisibility(View.VISIBLE);
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(YouTubePlayer youTubePlayer) {
                            String id = videoId;
                            youTubePlayer.loadVideo(id, 0);
                            youTubePlayer.play();
                        }
                    });
                    youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                        @Override
                        public void onYouTubePlayerEnterFullScreen() {
                            youTubePlayerView.enterFullScreen();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }

                        @Override
                        public void onYouTubePlayerExitFullScreen() {

                            youTubePlayerView.exitFullScreen();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    });
                    break;
                default: {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setUrl(url); //设置视频地址

                    StandardVideoController controller = new StandardVideoController(this);
                    controller.addDefaultControlComponent("", false);
                    videoView.setVideoController(controller);
                    videoView.setKeepScreenOn(true);//设置控制器

                    videoView.startFullScreen();
                    videoView.start();
                    youTubePlayerView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    public static boolean isEmbedURL(String url) {
        Pattern embedPattern = Pattern.compile(".*\\/embed\\/.*");

        return embedPattern.matcher(url).matches();
    }

    public static String extractURLFromEmbedCode(String embedCode) {
        String url = null;

        Pattern pattern = Pattern.compile("<iframe.*?src=[\"'](.*?)[\"'].*?>");

        Matcher matcher = pattern.matcher(embedCode);
        if (matcher.find()) {
            url = matcher.group(1);
        }

        return url;
    }

    private void openWebActivity(String s) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        youTubePlayerView.setVisibility(GONE);

        webViewEm = findViewById(R.id.webViewEm);
        weblPlay = findViewById(R.id.webplay);
        weblPlay.setVisibility(VISIBLE);
        setVideoView(s, videoType);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.OFF);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        webViewEm.removeView(mWebView.getLayout());

        webViewEm.addView(mWebView.getLayout());
    }

    private void setVideoView(String videoId, String websiteName) {
        if (websiteName.equals("daily motion")) {
            try {
                String videoID[] = videoId.split("(dai.ly)/|(/(video|hub))/");
                mWebView.loadUrl("https://www.dailymotion.com/embed/video/" + videoID[1]);
            }catch (Exception e){
                mWebView.loadUrl("https://www.dailymotion.com/embed/video/" + videoId);
            }
        } else if (websiteName.equals("vimeo")) {
            try {
                String videoID[] = videoId.split("(vimeo.com)|(/video)/");
                mWebView.loadUrl("http://player.vimeo.com/video/" + videoID[1] + "?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800");
            }catch (Exception e){

            }
        } else {
            mWebView.loadUrl(videoId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        videoView.resume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!videoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

}