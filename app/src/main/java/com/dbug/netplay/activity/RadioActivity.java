package com.dbug.netplay.activity;

import static com.dbug.netplay.SplashActivity.MYPREFERENCE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dbug.netplay.R;
import com.dbug.netplay.fragment.RadioFragment;

public class RadioActivity extends AppCompatActivity {

    String collapsingToolbarFragmentTag = "collapsing_toolbar";
    String selectedTag = "selected_index";
    int selectedIndex;
    String url;
    String videoType;
    String radioTitle;
    SharedPreferences sharedpreferences;
    boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        isDark = sharedpreferences.getBoolean("darkMode", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLightRadio);
        }
        setContentView(R.layout.activity_radio);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent callingIntent = getIntent();
        videoType = callingIntent.getStringExtra("video_type");
        url = callingIntent.getStringExtra("url");
        radioTitle = callingIntent.getStringExtra("radio_title");

        RadioFragment fragment = new RadioFragment();
        Bundle args = new Bundle();
        args.putString("video_type", videoType);
        args.putString("url", url);
        args.putString("radio_title", radioTitle);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, collapsingToolbarFragmentTag)
                .commit();
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new RadioFragment(), collapsingToolbarFragmentTag).commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(selectedTag, selectedIndex);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}