package com.dbug.netplay.RadioServices;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.dbug.netplay.utils.SingleTools;


public class SingleRadioManager {

    private static SingleRadioManager instance = null;
    private static SingleRadioService service;
    private boolean serviceBound;

    private SingleRadioManager() {
        serviceBound = false;
    }

    public static SingleRadioManager with() {
        if (instance == null)
            instance = new SingleRadioManager();
        return instance;
    }

    public static SingleRadioService getService() {
        return service;
    }

    public void playOrPause(String streamUrl) {
        if (streamUrl == null)
            service.stop();
        else
            service.playOrPause(streamUrl);
    }

    public boolean isPlaying() {
        return service.isPlaying();
    }

    public void bind(Context context) {
        if (!serviceBound) {
            Intent intent = new Intent(context, SingleRadioService.class);
            context.startService(intent);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            if (service != null)
                SingleTools.onEvent(service.getStatus());
        }
    }

    public void unbind(Context context) {
        if (serviceBound) {
            try {
                service.stop();
                context.unbindService(serviceConnection);
                context.stopService(new Intent(context, SingleRadioService.class));
                serviceBound = false;
            } catch (IllegalArgumentException e) {
//                Toast.makeText(context,e.getMessage()+ "", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isBound() {
        return serviceBound;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = ((SingleRadioService.LocalBinder) binder).getService();
            serviceBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

}
