package com.dbug.netplay.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ads {

    //    public String message;
//    public String status;
//    public String details_content;
//    public String live_visibility;
//    public String player_type;
//    public String launch_url;
//    public String option_visibility;
//    public String series_visibility;
//    public String advertisement_image_url;
//    public String banner_ads;
//    public String inter_ads;
//    public String gbanner_id;
//    public String ginters_id;
//    public String gnative_id;
//    public String fbbanner_id;
//    public String fbinters_id;
//    public String admob_app_id;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("admob_inter")
        @Expose
        private String admobInter;
        @SerializedName("admob_native")
        @Expose
        private String admobNative;
        @SerializedName("admob_banner")
        @Expose
        private String admobBanner;
        @SerializedName("admob_reward")
        @Expose
        private String admobReward;
        @SerializedName("fb_inter")
        @Expose
        private String fbInter;
        @SerializedName("fb_native")
        @Expose
        private String fbNative;
        @SerializedName("fb_banner")
        @Expose
        private String fbBanner;
        @SerializedName("fb_reward")
        @Expose
        private String fbReward;
        @SerializedName("unity_appId_gameId")
        @Expose
        private String unityAppIdGameId;
        @SerializedName("iron_appKey")
        @Expose
        private String ironAppKey;
        @SerializedName("appnext_placementId")
        @Expose
        private String appnextPlacementId;
        @SerializedName("startapp_appId")
        @Expose
        private String startappAppId;
        @SerializedName("industrial_interval")
        @Expose
        private Integer industrialInterval;
        @SerializedName("native_ads")
        @Expose
        private Integer nativeAds;
        @SerializedName("ad_types")
        @Expose
        private String adTypes;
        @SerializedName("created_at")
        @Expose
        private Object createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getAdmobInter() {
            return admobInter;
        }

        public void setAdmobInter(String admobInter) {
            this.admobInter = admobInter;
        }

        public String getAdmobNative() {
            return admobNative;
        }

        public void setAdmobNative(String admobNative) {
            this.admobNative = admobNative;
        }

        public String getAdmobBanner() {
            return admobBanner;
        }

        public void setAdmobBanner(String admobBanner) {
            this.admobBanner = admobBanner;
        }

        public String getAdmobReward() {
            return admobReward;
        }

        public void setAdmobReward(String admobReward) {
            this.admobReward = admobReward;
        }

        public String getFbInter() {
            return fbInter;
        }

        public void setFbInter(String fbInter) {
            this.fbInter = fbInter;
        }

        public String getFbNative() {
            return fbNative;
        }

        public void setFbNative(String fbNative) {
            this.fbNative = fbNative;
        }

        public String getFbBanner() {
            return fbBanner;
        }

        public void setFbBanner(String fbBanner) {
            this.fbBanner = fbBanner;
        }

        public String getFbReward() {
            return fbReward;
        }

        public void setFbReward(String fbReward) {
            this.fbReward = fbReward;
        }

        public String getUnityAppIdGameId() {
            return unityAppIdGameId;
        }

        public void setUnityAppIdGameId(String unityAppIdGameId) {
            this.unityAppIdGameId = unityAppIdGameId;
        }

        public String getIronAppKey() {
            return ironAppKey;
        }

        public void setIronAppKey(String ironAppKey) {
            this.ironAppKey = ironAppKey;
        }

        public String getAppnextPlacementId() {
            return appnextPlacementId;
        }

        public void setAppnextPlacementId(String appnextPlacementId) {
            this.appnextPlacementId = appnextPlacementId;
        }

        public String getStartappAppId() {
            return startappAppId;
        }

        public void setStartappAppId(String startappAppId) {
            this.startappAppId = startappAppId;
        }

        public Integer getIndustrialInterval() {
            return industrialInterval;
        }

        public void setIndustrialInterval(Integer industrialInterval) {
            this.industrialInterval = industrialInterval;
        }

        public Integer getNativeAds() {
            return nativeAds;
        }

        public void setNativeAds(Integer nativeAds) {
            this.nativeAds = nativeAds;
        }

        public String getAdTypes() {
            return adTypes;
        }

        public void setAdTypes(String adTypes) {
            this.adTypes = adTypes;
        }

        public Object getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Object createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

}
