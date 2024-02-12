package com.dbug.netplay.activity;

import static com.dbug.netplay.SplashActivity.MYPREFERENCE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.dbug.netplay.R;
import com.dbug.netplay.adapter.ItemAdapter;
import com.dbug.netplay.api.ApiInter;

import com.dbug.netplay.databinding.ActivityCategoryItemListBinding;
import com.dbug.netplay.model.AllPost;
import com.dbug.netplay.retofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class CategoryItemList extends AppCompatActivity {


    private ItemAdapter itemAdapter;

    private LinearLayoutManager linearLayoutManager;

    private ActivityCategoryItemListBinding activityCategoryItemListBinding;

    private String cid;
    private String categoryName;
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
        activityCategoryItemListBinding = DataBindingUtil.setContentView(this,R.layout.activity_category_item_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent callingIntent = getIntent();

        cid = callingIntent.getStringExtra("cid");
        categoryName = callingIntent.getStringExtra("category_name");

        activityCategoryItemListBinding.categoryListName.setText(categoryName);



        activityCategoryItemListBinding.swiperefresh.setRefreshing(true);

        if(isConnectingToInternet(CategoryItemList.this))
        {
            loadData();
        }
        else {
            new AlertDialog.Builder(CategoryItemList.this)
                    .setMessage("No internet Connection ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> loadData())
                    .show();

        }
        activityCategoryItemListBinding.swiperefresh.setOnRefreshListener(() -> {
            activityCategoryItemListBinding.swiperefresh.setRefreshing(true);
            loadData();

        });

        activityCategoryItemListBinding.backButton.setOnClickListener(v -> onBackPressed());

    }

    public void itemRecyclerview(List<AllPost.Post> itamlist){
        itemAdapter = new ItemAdapter(this,itamlist, isListGrid);
        if (isListGrid){
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            activityCategoryItemListBinding.allItemViewCategory.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            activityCategoryItemListBinding.allItemViewCategory.setLayoutManager(linearLayoutManager);
        }
        activityCategoryItemListBinding.allItemViewCategory.setAdapter(itemAdapter);

        activityCategoryItemListBinding.swiperefresh.setRefreshing(false);

    }

    public void loadData(){
        ApiInter category = RetrofitClient.getRetrofit()
                .create(ApiInter.class);
        category.getCategoryPostBody(cid)
                .enqueue(new Callback<AllPost>() {
                    @Override
                    public void onResponse(Call<AllPost> call, Response<AllPost> response) {
                        Log.d("ssfs", "onResponse: "+response.body());
                        if (response.isSuccessful()){
                            AllPost allPost = response.body();
                            List<AllPost.Post> post =allPost.getPosts();
                            itemRecyclerview(post);
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