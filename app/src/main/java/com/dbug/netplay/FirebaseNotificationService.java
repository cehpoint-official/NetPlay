package com.dbug.netplay;

import static com.dbug.netplay.fragment.SettingsFragment.MYPREFERENCE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dbug.netplay.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10101";
    SharedPreferences sharedPreferences ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        sharedPreferences = getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("message");
            String hisID = map.get("id");

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title, message, hisID);
            else
                createNormalNotification(title, message, hisID);
        } else Log.d("TAG", "onMessageReceived: no data ");


        super.onMessageReceived(remoteMessage);
    }

    private void createNormalNotification(String title, String message, String hisID) {

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_primary, null))
                .setSound(uri);

        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("hisID", hisID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 112, intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85 - 65), builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOreoNotification(String title, String message, String hisID) {
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("hisID", hisID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 112, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ResourcesCompat.getColor(getResources(), R.color.design_default_color_primary, null))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(100, notification);
    }
}
