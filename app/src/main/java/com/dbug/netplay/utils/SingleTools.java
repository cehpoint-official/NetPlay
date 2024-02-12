package com.dbug.netplay.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dbug.netplay.R;
import com.dbug.netplay.RadioServices.metadata.RMetadata;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleTools {

    private static boolean displayDebug = true;
    private static ArrayList<EventListener> listeners;


    public static void noConnection(final Activity context, String message) {

        AlertDialog.Builder ab = new AlertDialog.Builder(context);

        if (isOnline(context)) {
            String messageText = "";
            if (message != null && displayDebug) {
                messageText = "\n\n" + message;
            }

            ab.setMessage(context.getResources().getString(R.string.dialog_connection_description) + messageText);
            ab.setPositiveButton(context.getResources().getString(R.string.ok), null);
            ab.setTitle(context.getResources().getString(R.string.dialog_connection_title));
        } else {
            ab.setMessage(context.getResources().getString(R.string.dialog_internet_description));
            ab.setPositiveButton(context.getResources().getString(R.string.ok), null);
            ab.setTitle(context.getResources().getString(R.string.dialog_internet_title));
        }

        if (!context.isFinishing()) {
            ab.show();
        }
    }

    public static void noConnection(final Activity context) {
        noConnection(context, null);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnected();
    }

    public static boolean isOnlineShowDialog(Activity c) {
        if (isOnline(c))
            return true;
        else
            noConnection(c);
        return false;
    }


    public static void registerAsListener(EventListener listener) {
        if (listeners == null) listeners = new ArrayList<>();

        listeners.add(listener);
    }

    public static void unregisterAsListener(EventListener listener) {
        listeners.remove(listener);
    }

    public static void onEvent(String status) {
        if (listeners == null) return;

        for (EventListener listener : listeners) {
            listener.onEvent(status);
        }
    }

    public static void onAudioSessionId(Integer id) {
        if (listeners == null) return;

        for (EventListener listener : listeners) {
            listener.onAudioSessionId(id);
        }
    }


    public static interface EventListener {
        public void onEvent(String status);

        public void onAudioSessionId(Integer i);

        public void onMetaDataReceived(RMetadata meta, Bitmap image);
    }

    public long convertToMilliSeconds(String s) {

        long ms = 0;
        Pattern p;
        if (s.contains(("\\:"))) {
            p = Pattern.compile("(\\d+):(\\d+)");
        } else {
            p = Pattern.compile("(\\d+).(\\d+)");
        }
        Matcher m = p.matcher(s);
        if (m.matches()) {
            int h = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            ms = (long) h * 60 * 60 * 1000 + min * 60 * 1000;
        }
        return ms;
    }

}
