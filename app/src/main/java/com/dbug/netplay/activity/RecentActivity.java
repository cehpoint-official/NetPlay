package com.dbug.netplay.activity;

import static com.dbug.netplay.SplashActivity.MYPREFERENCE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.dbug.netplay.R;
import com.dbug.netplay.adapter.ItemAdapter;
import com.dbug.netplay.api.ApiInter;
import com.dbug.netplay.databinding.ActivityRecentBinding;
import com.dbug.netplay.model.AllPost;
import com.dbug.netplay.retofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class RecentActivity extends AppCompatActivity {


    private ActivityRecentBinding activityRecentBinding ;

    private ItemAdapter itemAdapter;
    private List<AllPost.Post> post = new ArrayList<>();
    int pageCount = 1;
    // Linear Layout Manager
    LinearLayoutManager linearLayoutManager;
    private boolean isDark,isListGrid;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        isDark = sharedpreferences.getBoolean("darkMode", false);
        isListGrid = sharedpreferences.getBoolean("listGrid", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        activityRecentBinding = DataBindingUtil.setContentView(this,R.layout.activity_recent);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        itemAdapter = new ItemAdapter(this,post, isListGrid);
        if (isListGrid){
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            activityRecentBinding.itemRecyclerview.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            activityRecentBinding.itemRecyclerview.setLayoutManager(linearLayoutManager);
        }
        activityRecentBinding.itemRecyclerview.setAdapter(itemAdapter);
        activityRecentBinding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY==v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                getData();
            }
        });
        getData();

        activityRecentBinding.backButton.setOnClickListener(v -> onBackPressed());
    }

    public void itemRecyclerview(List<AllPost.Post> itamlist){
        itemAdapter = new ItemAdapter(this,itamlist, isListGrid);
        if (isListGrid){
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            activityRecentBinding.itemRecyclerview.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            activityRecentBinding.itemRecyclerview.setLayoutManager(linearLayoutManager);
        }
        activityRecentBinding.itemRecyclerview.setAdapter(itemAdapter);
        activityRecentBinding.itemRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Check if reached the end of the list
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Perform pagination request
                    getData();
                }
            }
        });

        itemAdapter.notifyDataSetChanged();
    }

    private void getData() {
        ApiInter apiInter = RetrofitClient.getRetrofit()
                .create(ApiInter.class);

        apiInter.getPostBody(10,pageCount)
                .enqueue(new Callback<AllPost>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<AllPost> call, Response<AllPost> response) {
                        if (response.isSuccessful()){
                            AllPost allPost = response.body();
                            List<AllPost.Post> posts = allPost.getPosts();

                            post.addAll(posts);

                            itemAdapter.notifyDataSetChanged();
                            pageCount++;
                        }
                    }
                    @Override
                    public void onFailure(Call<AllPost> call, Throwable t) {
                        Log.d("sgfgsdgs", "onFailure: "+t.getMessage());
                    }
                });
    }
}