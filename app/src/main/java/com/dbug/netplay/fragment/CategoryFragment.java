package com.dbug.netplay.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbug.netplay.R;
import com.dbug.netplay.activity.MainActivity;
import com.dbug.netplay.adapter.CategoryAdapterMain;
import com.dbug.netplay.api.ApiInter;

import com.dbug.netplay.databinding.FragmentCategoryBinding;
import com.dbug.netplay.model.CategoryMain;
import com.dbug.netplay.retofit.RetrofitClient;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.dbug.netplay.fragment.HomeFragment.isConnectingToInternet;

public class CategoryFragment extends Fragment {

    FragmentCategoryBinding fragmentCategoryBinding;


    CategoryAdapterMain categoryAdapterMain;
    LinearLayoutManager linearLayoutManager;

    String onBackPresVisible;

    public CategoryFragment(String onBackPresVisible) {
        this.onBackPresVisible = onBackPresVisible;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCategoryBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_category,container,false);
        View view = fragmentCategoryBinding.getRoot();


        if (onBackPresVisible.equals("visible")){
            fragmentCategoryBinding.backPressOnCategory.setVisibility(View.VISIBLE);
        }
        fragmentCategoryBinding.backPressOnCategory.setOnClickListener(v -> {
            Fragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_frame_layout, homeFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
            MainActivity.getInstance().navIconColorChanged("home");
        });

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Fragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.navigation_frame_layout, homeFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
                MainActivity.getInstance().navIconColorChanged("home");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        fragmentCategoryBinding.swiperefresh.setRefreshing(true);

        if(isConnectingToInternet(getContext()))
        {
            loadData();
        }
        else {
            new AlertDialog.Builder(getContext())
                    .setMessage("No internet Connection ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> loadData())
                    .show();

        }

        fragmentCategoryBinding.swiperefresh.setOnRefreshListener(() -> {
            fragmentCategoryBinding.swiperefresh.setRefreshing(true);
            loadData();

        });




        // Inflate the layout for this fragment
        return view;
    }

    public void setCategoryRecyclerView(List<CategoryMain.Category> categoryMList ){

        categoryAdapterMain = new CategoryAdapterMain(getActivity(),categoryMList);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        fragmentCategoryBinding.categoryFragmentRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),2));
        fragmentCategoryBinding.categoryFragmentRecyclerview.setAdapter(categoryAdapterMain);

    }

    public  void  loadData(){

        //category
        ApiInter category = RetrofitClient.getRetrofit()
                .create(ApiInter.class);

        category.getCategoryBody(100)
                .enqueue(new Callback<CategoryMain>() {
                    @Override
                    public void onResponse(Call<CategoryMain> call, Response<CategoryMain> response) {

                        try {
                            if(response.isSuccessful()){

                                CategoryMain categoryMain = response.body();

                                List<CategoryMain.Category> postList = categoryMain.getCategories();
                                setCategoryRecyclerView(postList);

                                fragmentCategoryBinding.swiperefresh.setRefreshing(false);

                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryMain> call, Throwable t) {
                        Log.d("fdsfdsf", "onFailure: "+t.getMessage());
                    }
                });



    }

}