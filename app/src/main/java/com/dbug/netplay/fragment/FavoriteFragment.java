package com.dbug.netplay.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbug.netplay.activity.MainActivity;
import com.dbug.netplay.databinding.FragmentFavoriteBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dbug.netplay.R;
import com.dbug.netplay.adapter.FavoriteItemAdapter;

import com.dbug.netplay.model.AllPost;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.dbug.netplay.SplashActivity.MYPREFERENCE;

public class FavoriteFragment extends Fragment {


    FragmentFavoriteBinding fragmentFavoriteBinding;
    FavoriteItemAdapter favoriteItemAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<AllPost.Post> post;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentFavoriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        View view = fragmentFavoriteBinding.getRoot();


        fragmentFavoriteBinding.swiperefresh.setRefreshing(true);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Fragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.navigation_frame_layout, homeFragment); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();

                MainActivity.getInstance().navIconColorChanged("home");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);

        fragmentFavoriteBinding.swiperefresh.setOnRefreshListener(() -> {
            fragmentFavoriteBinding.swiperefresh.setRefreshing(true);
            loadData();
        });

        loadData();

        fragmentFavoriteBinding.swiperefresh.setRefreshing(false);
        return view;
    }


    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("test", null);
        Type type = new TypeToken<ArrayList<AllPost.Post>>() {
        }.getType();
        post = gson.fromJson(json, type);

        if (post == null) {
            post = new ArrayList<>();

        } else {
            itemRecyclerview(post);

        }
        if (!post.isEmpty()) {
            fragmentFavoriteBinding.itemRecyclerview.setVisibility(View.VISIBLE);
            fragmentFavoriteBinding.noData.setVisibility(View.GONE);
        }

    }

    public void itemRecyclerview(List<AllPost.Post> itamlist) {
        favoriteItemAdapter = new FavoriteItemAdapter(getContext(), itamlist);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        fragmentFavoriteBinding.itemRecyclerview.setLayoutManager(linearLayoutManager);
        fragmentFavoriteBinding.itemRecyclerview.setAdapter(favoriteItemAdapter);

        fragmentFavoriteBinding.swiperefresh.setRefreshing(false);


    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();

    }
}