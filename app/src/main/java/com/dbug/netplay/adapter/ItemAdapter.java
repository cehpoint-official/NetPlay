package com.dbug.netplay.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dbug.netplay.ads.InterAdClickInterFace;
import com.dbug.netplay.ads.SessionAds;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.dbug.netplay.R;
import com.dbug.netplay.activity.ItemViewActivity;
import com.dbug.netplay.model.AllPost;

import java.util.List;

import static com.dbug.netplay.SplashActivity.FBINTERS_ID;
import static com.dbug.netplay.SplashActivity.GINTERS_ID;
import static com.dbug.netplay.SplashActivity.MYPREFERENCE;
import static com.dbug.netplay.config.Constant.ADMIN_PANEL_URL;
import static com.dbug.netplay.config.Constant.VIDEO_IMAGE_URL;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemView> implements InterAdClickInterFace {


    private List<AllPost.Post> itemModelsList;
    private com.facebook.ads.InterstitialAd facebookInterstitialAd;
    private InterstitialAd mInterstitialAd;
    private String viewSelection;
    private SharedPreferences sharedpreferences;
    private SessionAds sessionAds;
    private Context context;
    private boolean isListGrid;

    public ItemAdapter(Context context, List<AllPost.Post> itemModelsList, boolean isListGrid) {
        this.itemModelsList = itemModelsList;
        this.context = context;
        this.isListGrid = isListGrid;
        this.sessionAds = new SessionAds(context, this);
    }

    @NonNull
    @Override
    public ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item.xml using LayoutInflation
        View itemView = null;
        if (isListGrid){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        }
        // return itemView
        return new ItemAdapter.ItemView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemView holder, int position) {
        final AllPost.Post allPosts = itemModelsList.get(position);
        Log.d("asdadas ", "onBindViewHolder: "+ADMIN_PANEL_URL+VIDEO_IMAGE_URL+allPosts.video_thumbnail);
        holder.titleTextView.setText(allPosts.video_title);
        holder.categoryTextView.setText(allPosts.category_name);
        if(allPosts.video_type.equals("Youtube")){
            Glide.with(holder.itemView)
                    .load("https://img.youtube.com/vi/" + allPosts.video_id + "/maxresdefault.jpg")
                    .fitCenter()
                    .into(holder.videoImage);
        }else {
            Glide.with(holder.itemView)
                    .load(ADMIN_PANEL_URL + VIDEO_IMAGE_URL + allPosts.video_thumbnail)
                    .fitCenter()
                    .into(holder.videoImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemViewActivity.class);
            intent.putExtra("video_title", allPosts.video_title);
            intent.putExtra("vid", allPosts.vid + "");
            intent.putExtra("video_url", allPosts.video_url);
            intent.putExtra("video_id", allPosts.video_id);
            intent.putExtra("video_duration", allPosts.video_duration);
            intent.putExtra("video_description", allPosts.video_description);
            intent.putExtra("video_type", allPosts.video_type);
            intent.putExtra("size", allPosts.size);
            intent.putExtra("video_thumbnail", allPosts.video_thumbnail);
            intent.putExtra("total_views", allPosts.total_views + "");
            intent.putExtra("date_time", allPosts.date_time);
            intent.putExtra("category_name", allPosts.category_name);
            intent.putExtra("from", "video");
            v.getContext().startActivity(intent);

            sessionAds.show();

        });

    }

    @Override
    public int getItemCount() {
        return itemModelsList.size();
    }

    @Override
    public void onAdClick() {

    }

    @Override
    public void onAdFailed() {

    }

    public class ItemView extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView categoryTextView;
        ImageView videoImage;


        public ItemView(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            categoryTextView = itemView.findViewById(R.id.category_textView);
            videoImage = itemView.findViewById(R.id.video_image);
        }
    }

    private void prepareInterstitialAd(Context context) {
        sharedpreferences = context.getSharedPreferences(MYPREFERENCE,
                Context.MODE_PRIVATE);
        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(context,sharedpreferences.getString(FBINTERS_ID, ""));
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
                Log.d("sgfgsdgs", "onFailure: "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

                facebookInterstitialAd.show();
                viewSelection = "admob";
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                //empty
            }
        };

        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }
    private void prepareInterstitialAdAdmob(Activity activity) {

        sharedpreferences = activity.getSharedPreferences(MYPREFERENCE,
                Context.MODE_PRIVATE);

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity,sharedpreferences.getString(GINTERS_ID, ""), adRequest, new InterstitialAdLoadCallback() {
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

        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            viewSelection = "fb";

        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

    }
}
