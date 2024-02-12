package com.dbug.netplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.dbug.netplay.R;

public class SingleSharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SingleSharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int getFirstColor() {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.main_text_color, typedValue, true);
        return sharedPreferences.getInt("first", ContextCompat.getColor(context, typedValue.resourceId));
    }
    public int getIndicateTextColor() {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.radio_background_color, typedValue, true);
        return sharedPreferences.getInt("IndicateText", ContextCompat.getColor(context, typedValue.resourceId));
    }

    public int getSecondColor() {
        return sharedPreferences.getInt("second", ContextCompat.getColor(context, R.color.text_color_light));
    }

    public void setCheckSleepTime() {
        if (getSleepTime() <= System.currentTimeMillis()) {
            setSleepTime(false, 0, 0);
        }
    }

    public void setSleepTime(Boolean isTimerOn, long sleepTime, int id) {
        editor.putBoolean("isTimerOn", isTimerOn);
        editor.putLong("sleepTime", sleepTime);
        editor.putInt("sleepTimeID", id);
        editor.apply();
    }

    public Boolean getIsSleepTimeOn() {
        return sharedPreferences.getBoolean("isTimerOn", false);
    }

    public long getSleepTime() {
        return sharedPreferences.getLong("sleepTime", 0);
    }

    public int getSleepID() {
        return sharedPreferences.getInt("sleepTimeID", 0);
    }

    public String getRadioName() {
        return sharedPreferences.getString("radio_name", context.getResources().getString(R.string.app_name));
    }


}