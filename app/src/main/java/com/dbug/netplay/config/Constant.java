package com.dbug.netplay.config;

public class Constant {
        public static final String RADIO_STREAM_URL = "http://classicalking.streamguys1.com/king-fm-mp3";
        public static final String PRIVACY_URL = "https://vid-mates.com";
        public static final String Terms_URL = "https://vid-mates.com";
        public static final String FAQ_URL = "https://vid-mates.com";
        public static final String FEEDBACK_URL = "https://vid-mates.com";

        //OpenAdsCode change
        public static final String OPEN_ADS = "ca-app-pub-3940256099942544/3419835294";

        // Put Your Server Root Url Here
        public static final String ADMIN_PANEL_URL = "https://vid-mates.com";
        // Put Your Server subfolder as like this if you have any
        public static final String ADMIN_PANEL_PATH="/shobuj/LiveTvMini";

        //Put Your Ads Type, As per documentation if you want to load ads code from server then make it "server", Other wise put your ads code in strings file and
        // pass ADS_TYPE value "local" to load ads id code from local
        public static final String ADS_TYPE = "server";


        //****don't changes anything below code***
        public static final String POST_PANEL_URL = ADMIN_PANEL_PATH+"/public/api/get_posts";
        public static final String CATEGORY_PANEL_URL = ADMIN_PANEL_PATH+ "/public/api/get_category_index";
        public static final String CATEGORY_IMAGE_URL = ADMIN_PANEL_PATH+"/public/upload/category/";
        public static final String VIDEO_IMAGE_URL = ADMIN_PANEL_PATH+"/public/upload/video/";
        public static final String CATEGORY_ALL_ITEM_URL = ADMIN_PANEL_PATH+ "/public/api/get_category_posts";
        public static final String SLIDERS_PANEL_URL = ADMIN_PANEL_PATH+ "/public/api/get_slide";
        public static final String SLIDER_IMAGE_URL = ADMIN_PANEL_PATH+"/public/upload/slider/";
//        public static final String ADS_URL = ADMIN_PANEL_PATH+"/ads/adsid.json";
        public static final String ADS_URL = ADMIN_PANEL_URL+ADMIN_PANEL_PATH+"/public/api/advertisement";

        //send/save fcm token
        public static final String POST_FCM_TOKEN = ADMIN_PANEL_URL+ADMIN_PANEL_PATH+"/public/api/store-token";
        public static final String POST_VIDEO_COUNT = ADMIN_PANEL_URL+ADMIN_PANEL_PATH+"/public/api/item_view_count";
        public static final String POST_SLIDER_VIDEO_COUNT = ADMIN_PANEL_URL+ADMIN_PANEL_PATH+"/public/api/slider_view_count";
    }


