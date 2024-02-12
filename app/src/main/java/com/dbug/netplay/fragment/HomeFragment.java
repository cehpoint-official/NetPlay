package com.dbug.netplay.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbug.netplay.R;
import com.dbug.netplay.activity.MainActivity;
import com.dbug.netplay.activity.RecentActivity;
import com.dbug.netplay.adapter.CategoryAdapter;
import com.dbug.netplay.adapter.ItemAdapter;
import com.dbug.netplay.adapter.SliderAdapter;
import com.dbug.netplay.api.ApiInter;

import com.dbug.netplay.databinding.FragmentHomeBinding;
import com.dbug.netplay.model.AllPost;
import com.dbug.netplay.model.CategoryMain;
import com.dbug.netplay.model.SliderItem;
import com.dbug.netplay.retofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dbug.netplay.SplashActivity.MYPREFERENCE;
import static com.dbug.netplay.config.Constant.ADMIN_PANEL_URL;

import static com.dbug.netplay.config.Constant.SLIDER_IMAGE_URL;

public class HomeFragment extends Fragment {


    FragmentHomeBinding fragmentHomeBinding;
    SliderAdapter sliderAdapter;

    List<SliderItem> sliderItemList = new ArrayList<>();

    CategoryAdapter categoryAdapter;
    ItemAdapter itemAdapter;

    // Linear Layout Manager
    LinearLayoutManager linearLayoutManager1;
    LinearLayoutManager linearLayoutManager;
    int pageCount = 1;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem;
    int visibleItemCount;
    int totalItemCount;

    private int dotsCount = 0;
    private ImageView[] dots;
    Timer timer;
    Handler handler;
    private boolean isListGrid;
    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        isListGrid = sharedPreferences.getBoolean("listGrid", false);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        View view = fragmentHomeBinding.getRoot();
        setHasOptionsMenu(true);

        fragmentHomeBinding.swiperefresh.setRefreshing(true);

        if(isConnectingToInternet(getContext()))
        {
            try {
                dataLoad();
            }catch (Exception e){

            }

        }
        else {
            new AlertDialog.Builder(getContext())
                    .setMessage("No internet Connection ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> dataLoad())
                    .show();

        }

        fragmentHomeBinding.swiperefresh.setOnRefreshListener(() -> {
            fragmentHomeBinding.swiperefresh.setRefreshing(true);
            dataLoad();
        });


        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                //additional code
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.exit_dialog, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().setDimAmount(0.7f);
                TextView yes = dialogView.findViewById(R.id.ivExitBtn);
                yes.setOnClickListener(view -> {
                    getActivity().finish();
                });
                TextView no = dialogView.findViewById(R.id.ivCancelBtn);
                no.setOnClickListener(view -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        fragmentHomeBinding.viewAllCategory.setOnClickListener(v -> {
            Fragment categoryFragment = new CategoryFragment("visible");
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_frame_layout, categoryFragment); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
            MainActivity.getInstance().navIconColorChanged("category");
        });

        fragmentHomeBinding.recentVideo.setOnClickListener(v -> startActivity(new Intent(getContext(), RecentActivity.class)));


        return view;

    }



    public void setCategoryRecyclerView(List<CategoryMain.Category> categoryMList ){

        categoryAdapter = new CategoryAdapter(getActivity(),categoryMList);
        linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        fragmentHomeBinding.categoryRecyclerview.setLayoutManager(linearLayoutManager1);
        fragmentHomeBinding.categoryRecyclerview.setAdapter(categoryAdapter);

    }

    public void sliderImage( List<AllPost.Post> posts ){

        if (!sliderItemList.isEmpty()){
            sliderItemList.clear();
        }
        //Slider
        sliderAdapter = new SliderAdapter();

        //dummy data
        for (int i = 0; i <posts.size(); i++) {

            SliderItem sliderItem = new SliderItem();
            AllPost.Post allpost = posts.get(i);

            sliderItem.setVideo_url(allpost.video_url);
            sliderItem.setVideo_type(allpost.video_type);
            sliderItem.setTitle(allpost.video_title);
            sliderItem.setCat_id(allpost.cat_id);
            sliderItem.setVid(allpost.vid);
            sliderItem.setCategory_name(allpost.category_name);
            sliderItem.setDate_time(allpost.date_time);
            sliderItem.setSize(allpost.size);
            sliderItem.setTotal_views(allpost.total_views);
            sliderItem.setVideo_id(allpost.video_id);
            sliderItem.setVideo_thumbnail(allpost.video_thumbnail);
            sliderItem.setVideo_description(allpost.video_description);

            if (allpost.video_type.equals("Url")){
                sliderItem.setImageUrl(ADMIN_PANEL_URL+SLIDER_IMAGE_URL+allpost.video_thumbnail);
            }else if(allpost.video_type.equals("Upload")){
                sliderItem.setImageUrl(ADMIN_PANEL_URL+SLIDER_IMAGE_URL+allpost.video_thumbnail);
            }
            else if(allpost.video_type.equals("Youtube")){
                sliderItem.setImageUrl("https://img.youtube.com/vi/"+allpost.video_id+"/maxresdefault.jpg");
            }else {
                sliderItem.setImageUrl(ADMIN_PANEL_URL+SLIDER_IMAGE_URL+allpost.video_thumbnail);
            }

            sliderItemList.add(sliderItem);
        }





        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        sliderAdapter.renewItems(sliderItemList);
        fragmentHomeBinding.sliderViewpager.setAdapter(sliderAdapter);
        dotsCount = sliderItemList.size();
        dots = new ImageView[dotsCount];
        for(int i=0; i<dotsCount;i++){
            try {
                dots[i] = new ImageView(getActivity());
                dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(),
                        R.drawable.tab_indicator_default));


                params.setMargins(8,0,8,0);
                fragmentHomeBinding.slideDot.addView(dots[i],params);
            }catch (Exception e){

            }
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity()
                ,R.drawable.tab_indicator_selected));

        fragmentHomeBinding.sliderViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    for(int i=0; i<dotsCount;i++){
                        dots[i].setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                                R.drawable.tab_indicator_default));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(requireActivity()
                            ,R.drawable.tab_indicator_selected));
                }catch (Exception e){

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        handler = new Handler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i = fragmentHomeBinding.sliderViewpager.getCurrentItem();
                        if (i==dotsCount-1){
                            i=0;
                            fragmentHomeBinding.sliderViewpager.setCurrentItem(i);
                        }else {
                            i++;
                            fragmentHomeBinding.sliderViewpager.setCurrentItem(i, true);
                        }
                    }
                });
            }
        }, 5000,5000);
    }


    public void itemRecyclerview(List<AllPost.Post> itamlist){
        itemAdapter = new ItemAdapter(getActivity(),itamlist,isListGrid);
        if (isListGrid){
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            fragmentHomeBinding.itemRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),2));
        }else {
            linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            fragmentHomeBinding.itemRecyclerview.setLayoutManager(linearLayoutManager);
        }
        fragmentHomeBinding.itemRecyclerview.setAdapter(itemAdapter);
//        fragmentHomeBinding.itemRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                visibleItemCount = fragmentHomeBinding.itemRecyclerview.getChildCount();
//                totalItemCount = linearLayoutManager.getItemCount();
//                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
//
//                if (loading && totalItemCount > previousTotal) {
//                    loading = false;
//                    previousTotal = totalItemCount;
//                }
//            }
//        });

        fragmentHomeBinding.swiperefresh.setRefreshing(false);

    }

    public void dataLoad(){
        //apiInter
        ApiInter apiInter = RetrofitClient.getRetrofit()
                .create(ApiInter.class);
        apiInter.getPostBody(10,pageCount)
                .enqueue(new Callback<AllPost>() {
                    @Override
                    public void onResponse(Call<AllPost> call, Response<AllPost> response) {
                        try {
                            if (response.isSuccessful()){
                                AllPost allPost = response.body();
                                List<AllPost.Post> post = allPost.getPosts();

                                itemRecyclerview(post);
                            }
                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onFailure(Call<AllPost> call, Throwable t) {

                        Log.d("check", t.getMessage());
                    }
                });

        apiInter.getCategoryBody(7)
                .enqueue(new Callback<CategoryMain>() {
                    @Override
                    public void onResponse(Call<CategoryMain> call, Response<CategoryMain> response) {
                        if(response.isSuccessful()){
                            try {
                                CategoryMain categoryMain = response.body();
                                List<CategoryMain.Category> postList = categoryMain.getCategories();
                                setCategoryRecyclerView(postList);
                            }catch (Exception e){

                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<CategoryMain> call, Throwable t) {

                        Log.d("sgfgsdgs", "onFailure: "+t.getMessage());
                    }
                });

        apiInter.getSliderBody(5)
                .enqueue(new Callback<AllPost>() {
                    @Override
                    public void onResponse(Call<AllPost> call, Response<AllPost> response) {
                        if (response.isSuccessful()){
                            AllPost allPost = response.body();
                            List<AllPost.Post> post = allPost.getPosts();

                            try {
                                fragmentHomeBinding.slideDot.removeAllViews();
                                sliderImage(post);
                            }catch (Exception e){

                            }

                        }
                    }
                    @Override
                    public void onFailure(Call<AllPost> call, Throwable t) {
                        Log.d("sgfgsdgs", "onFailure: "+t.getMessage());
                    }
                });


    }


    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivity != null;
    }
}