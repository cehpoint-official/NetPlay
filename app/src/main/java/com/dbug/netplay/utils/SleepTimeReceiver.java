package com.dbug.netplay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dbug.netplay.RadioServices.SingleRadioService;

public class SleepTimeReceiver extends BroadcastReceiver {

    SingleSharedPref singleSharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {

        singleSharedPref = new SingleSharedPref(context);

        if (singleSharedPref.getIsSleepTimeOn()) {
            singleSharedPref.setSleepTime(false, 0,0);
        }

        Intent intentClose = new Intent(context, SingleRadioService.class);
        intentClose.setAction(SingleRadioService.ACTION_STOP);
        context.startService(intentClose);
    }
}