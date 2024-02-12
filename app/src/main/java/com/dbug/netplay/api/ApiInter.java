package com.dbug.netplay.api;


import com.dbug.netplay.model.Ads;
import com.dbug.netplay.model.AllPost;
import com.dbug.netplay.model.CategoryMain;
import com.dbug.netplay.model.FCMItem;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.dbug.netplay.config.Constant.ADS_URL;
import static com.dbug.netplay.config.Constant.CATEGORY_ALL_ITEM_URL;
import static com.dbug.netplay.config.Constant.CATEGORY_PANEL_URL;
import static com.dbug.netplay.config.Constant.POST_FCM_TOKEN;
import static com.dbug.netplay.config.Constant.POST_PANEL_URL;
import static com.dbug.netplay.config.Constant.POST_SLIDER_VIDEO_COUNT;
import static com.dbug.netplay.config.Constant.POST_VIDEO_COUNT;
import static com.dbug.netplay.config.Constant.SLIDERS_PANEL_URL;

public interface ApiInter {


    @GET(CATEGORY_PANEL_URL)
    Call<CategoryMain> getCategoryBody(
            @Query("count") Integer count

    );
    @GET(ADS_URL)
    Call<Ads> getAds();

    @GET(POST_PANEL_URL)
    Call<AllPost> getPostBody(
            @Query("count") Integer count, @Query("page") Integer page
    );
    @GET(SLIDERS_PANEL_URL)
    Call<AllPost> getSliderBody(
            @Query("count") Integer count
    );

    @GET(CATEGORY_ALL_ITEM_URL)
    Call<AllPost> getCategoryPostBody(
            @Query("id") String cid
    );


    @FormUrlEncoded
    @POST(POST_FCM_TOKEN)
    Call<FCMItem> postFCMToken(
            @Field("device_token") String fcm_key,
            @Field("device_name") String device_name
    );

    @POST(POST_VIDEO_COUNT+"/{contanId}")
    Call<Object>setPostViewCount(
            @Path("contanId") String containId
    );
    @POST(POST_SLIDER_VIDEO_COUNT+"/{contanId}")
    Call<Object>setPostViewCountSlider(
            @Path("contanId") String containId
    );
}
