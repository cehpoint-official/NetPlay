package com.dbug.netplay.RadioServices;


import static com.dbug.netplay.RadioServices.SingleRadioService.ACTION_PAUSE;
import static com.dbug.netplay.RadioServices.SingleRadioService.ACTION_PLAY;
import static com.dbug.netplay.RadioServices.SingleRadioService.ACTION_STOP;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dbug.netplay.R;
import com.dbug.netplay.RadioServices.metadata.RMetadata;
import com.dbug.netplay.activity.RadioActivity;
import com.dbug.netplay.fragment.RadioFragment;

public class RadioNotificationManager {

    public static final int NOTIFICATION_ID = 555;
    public static final String NOTIFICATION_CHANNEL_ID = "single_radio_channel";
    private SingleRadioService service;
    private RMetadata meta;
    private String strAppName;
    String strLiveBroadcast;
    private Bitmap notifyIcon;
    private String playbackStatus;
    private Resources resources;
    @SuppressLint("StaticFieldLeak")
    static private Context context;
    static NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    RemoteViews bigViews, smallViews;

    public RadioNotificationManager(SingleRadioService service,Context context) {
        this.service = service;
        this.resources = service.getResources();
        strAppName = resources.getString(R.string.app_name);
        strLiveBroadcast = resources.getString(R.string.notification_playing);
        RadioNotificationManager.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startNotify(String playbackStatus) {
        this.playbackStatus = playbackStatus;

//        startNotify();
        createNotification();
    }
    @SuppressLint("RemoteViewLayout")
    private void createNotification() {
        Intent notificationIntent = new Intent(context, RadioActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pi = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        int icon = R.drawable.ic_pause_white;
        Intent playIntent = new Intent(service, SingleRadioService.class);
        playIntent.setAction(ACTION_PAUSE);
        PendingIntent pplayIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pplayIntent = PendingIntent.getService(service, 1, playIntent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pplayIntent = PendingIntent.getService(service, 1, playIntent, 0);
        }

        if (playbackStatus.equals(RadioPlaybackStatus.PAUSED)) {
            icon = R.drawable.ic_play_white;
            playIntent.setAction(ACTION_PLAY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pplayIntent = PendingIntent.getService(service, 2, playIntent, PendingIntent.FLAG_IMMUTABLE);
            }else {
                pplayIntent = PendingIntent.getService(service, 2, playIntent, 0);
            }
        }

        Intent closeIntent = new Intent(context, SingleRadioService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pcloseIntent = PendingIntent.getService(context, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pcloseIntent = PendingIntent.getService(context, 0, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
//        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.radio_img);
        Drawable drawable = resources.getDrawable(R.drawable.radio_img);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap largeBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, false);
        Bitmap largeIcon = createCircularBitmap(largeBitmap);
        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(strAppName)
                .setContentTitle(strLiveBroadcast)
                .setContentText(strAppName)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.splash_icon)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setOnlyAlertOnce(true)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeBitmap));


        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = resources.getString(R.string.app_name);
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);

            MediaSessionCompat mMediaSession;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                PendingIntent mediaButtonPendingIntent = PendingIntent.getBroadcast(
//                        context,
//                        0,
//                        new Intent(Intent.ACTION_MEDIA_BUTTON),
//                        PendingIntent.FLAG_IMMUTABLE
//                );

                mMediaSession = new MediaSessionCompat(context, resources.getString(R.string.app_name), null, pi
                );
            } else {
                mMediaSession = new MediaSessionCompat(context, resources.getString(R.string.app_name));
            }
//            MediaSessionCompat mMediaSession;
//            mMediaSession = new MediaSessionCompat(context, resources.getString(R.string.app_name));
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


            mBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowCancelButton(true)
                            .setShowActionsInCompactView(0, 1)
                            .setCancelButtonIntent(pcloseIntent))
                    .addAction(new NotificationCompat.Action(
                            icon, ACTION_PAUSE,
                            pplayIntent))
                    .addAction(new NotificationCompat.Action(
                            R.drawable.ic_stop, ACTION_STOP,
                            pcloseIntent));
        } else {
            bigViews = new RemoteViews(context.getPackageName(), R.layout.radio_notification_large);
            bigViews.setOnClickPendingIntent(R.id.img_notification_play, pplayIntent);

            bigViews.setOnClickPendingIntent(R.id.img_notification_close, pcloseIntent);

            bigViews.setImageViewResource(R.id.img_notification_play, icon);

            bigViews.setTextViewText(R.id.txt_notification_name, strLiveBroadcast);
            bigViews.setTextViewText(R.id.txt_notification_category, strAppName);

            bigViews.setImageViewResource(R.id.img_notification, R.drawable.splash_icon);
            mBuilder.setCustomBigContentView(bigViews);
        }

        service.startForeground(NOTIFICATION_ID, mBuilder.build());
    }
    private Bitmap createCircularBitmap(Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);

        return output;
    }

    @SuppressLint("NotificationTrampoline")
    private void startNotify() {
        if (playbackStatus == null) return;

        if (notifyIcon == null)
            notifyIcon = BitmapFactory.decodeResource(resources, R.drawable.splash_icon);

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, service.getString(R.string.audio_notification), NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }


        int icon = R.drawable.ic_pause_white;
        Intent playbackAction = new Intent(service, SingleRadioService.class);
        playbackAction.setAction(ACTION_PAUSE);
        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0);

        if (playbackStatus.equals(RadioPlaybackStatus.PAUSED)) {
            icon = R.drawable.ic_play_white;
            playbackAction.setAction(ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, 0);
        }

        Intent stopIntent = new Intent(service, SingleRadioService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);

        Intent intent = new Intent(service, RadioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("_data", new String[]{service.getStreamUrl()});
        bundle.putSerializable("_target", RadioFragment.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);
        NotificationManagerCompat.from(service).cancel(NOTIFICATION_ID);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID);

        String title = meta != null && meta.getArtist() != null ?
                meta.getArtist() : strLiveBroadcast;
        String subTitle = meta != null && meta.getSong() != null ?
                meta.getSong() : strAppName;

        builder.setColor(resources.getColor(R.color.color_primary))
                .setContentTitle(title)
                .setContentText(subTitle)
                .setLargeIcon(notifyIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.splash_icon)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.ic_stop, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]{0L})
                .setWhen(System.currentTimeMillis())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction));

        Notification notification = builder.build();
        service.startForeground(NOTIFICATION_ID, notification);

    }

    public RMetadata getMetaData() {
        return meta;
    }

    public Bitmap getAlbumArt() {
        notifyIcon = BitmapFactory.decodeResource(resources, R.drawable.radio_img);
        return notifyIcon;
    }

    public void cancelNotify() {
        service.stopForeground(true);

    }

}
