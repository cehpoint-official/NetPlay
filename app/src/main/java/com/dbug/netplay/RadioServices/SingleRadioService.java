package com.dbug.netplay.RadioServices;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.dbug.netplay.R;
import com.dbug.netplay.RadioServices.metadata.MetadataListener;
import com.dbug.netplay.RadioServices.metadata.RMetadata;
import com.dbug.netplay.activity.RadioActivity;
import com.dbug.netplay.utils.SingleTools;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class SingleRadioService extends Service implements Player.Listener, AudioManager.OnAudioFocusChangeListener, MetadataListener {

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";
    public static final String ACTION_STOP = "ACTION_STOP";
    private final IBinder iBinder = new LocalBinder();

    public static ExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    private RadioNotificationManager notificationManager;

    String status;
    String strAppName;
    String strLiveBroadcast;
    String streamUrl;

    public class LocalBinder extends Binder {
        public SingleRadioService getService() {
            return SingleRadioService.this;
        }
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }

    };

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
                if (!isPlaying()) return;
                onGoingCall = true;
                stop();

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return;
                onGoingCall = false;
                resume();
            }
        }

    };

    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            resume();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        strAppName = getResources().getString(R.string.app_name);
        strLiveBroadcast = getResources().getString(R.string.notification_playing);

        onGoingCall = false;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        notificationManager = new RadioNotificationManager(this,this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ComponentName mediaButtonReceiver = new ComponentName(this, MediaButtonReceiver.class);
            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            mediaButtonIntent.setComponent(mediaButtonReceiver);
            PendingIntent pi;
            pi = PendingIntent.getActivity(this, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE);
            mediaSession = new MediaSessionCompat(this, getClass().getSimpleName(),
                    null, pi);
        } else {
            mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        }

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

//        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
                .build());
        mediaSession.setCallback(mediasSessionCallback);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        @SuppressWarnings("deprecation") AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(SingleRadioService.this, videoTrackSelectionFactory);
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer = new ExoPlayer.Builder(SingleRadioService.this)
                .setTrackSelector(trackSelector).build();
        exoPlayer.addListener(this);
        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
                                           @Override
                                           public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {

                                               //Empty Method
                                           }

                                           @Override
                                           public void onTimelineChanged(EventTime eventTime, int reason) {
//Empty Method
                                           }

                                           @Override
                                           public void onPositionDiscontinuity(EventTime eventTime, int reason) {
//Empty Method
                                           }

                                           @Override
                                           public void onSeekStarted(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onSeekProcessed(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
//Empty Method
                                           }

                                           @Override
                                           public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
//Empty Method
                                           }

                                           @Override
                                           public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
//Empty Method
                                           }

                                           @Override
                                           public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
//Empty Method
                                           }

//                                           @Override
                                           public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
//Empty Method
                                           }

//                                           @Override
//                                           public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onMediaPeriodCreated(EventTime eventTime) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onMediaPeriodReleased(EventTime eventTime) {
////Empty Method
//                                           }
//
//                                           @Override
//                                           public void onReadingStarted(EventTime eventTime) {
////Empty Method
//                                           }

                                           @Override
                                           public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
//Empty Method
                                           }

                                           @Override
                                           public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
//Empty Method
                                           }

                                           @Override
                                           public void onMetadata(EventTime eventTime, com.google.android.exoplayer2.metadata.Metadata metadata) {
//Empty Method
                                           }

                                           @Override
                                           public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
//Empty Method
                                           }

                                           @Override
                                           public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
//Empty Method
                                           }

                                           @Override
                                           public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
//Empty Method
                                           }

                                           @Override
                                           public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {
//Empty Method
                                           }

//                                           @Override
                                           public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
                                               SingleTools.onAudioSessionId(getAudioSessionId());
                                           }

                                           @Override
                                           public void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes) {
//Empty Method
                                           }

                                           @Override
                                           public void onVolumeChanged(EventTime eventTime, float volume) {
//Empty Method
                                           }

                                           @Override
                                           public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
//Empty Method
                                           }

                                           @Override
                                           public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
//Empty Method
                                           }

                                           @Override
                                           public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//Empty Method
                                           }

//                                           @Override
                                           public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmSessionAcquired(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmKeysLoaded(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmSessionManagerError(EventTime eventTime, Exception error) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmKeysRestored(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmKeysRemoved(EventTime eventTime) {
//Empty Method
                                           }

                                           @Override
                                           public void onDrmSessionReleased(EventTime eventTime) {
//Empty Method
                                           }
                                       }
        );

        exoPlayer.setPlayWhenReady(true);
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        status = RadioPlaybackStatus.IDLE;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (TextUtils.isEmpty(action))
            return START_NOT_STICKY;

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop();
            return START_NOT_STICKY;
        }

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (action.equalsIgnoreCase(ACTION_RESUME)) {
            Intent meaw = new Intent(getApplicationContext(), RadioActivity.class);
            meaw.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(meaw);


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                transportControls.pause();
                new Handler(Looper.getMainLooper()).postDelayed(() -> transportControls.play(), 10);

            }, 250);


        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if (status.equals(RadioPlaybackStatus.IDLE))
            stopSelf();
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {

        pause();
        exoPlayer.release();
        exoPlayer.removeListener(this);

        if (telephonyManager != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

        notificationManager.cancelNotify();
        mediaSession.release();
        unregisterReceiver(becomingNoisyReceiver);

        super.onDestroy();

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                exoPlayer.setVolume(0.8f);
                resume();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isPlaying()) pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying())
                    exoPlayer.setVolume(0.1f);
                break;
        }

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = RadioPlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = RadioPlaybackStatus.STOPPED;
                break;
            case Player.STATE_IDLE:
                status = RadioPlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? RadioPlaybackStatus.PLAYING : RadioPlaybackStatus.PAUSED;
                break;
            default:
                status = RadioPlaybackStatus.IDLE;
                break;
        }

        if (!status.equals(RadioPlaybackStatus.IDLE))
            notificationManager.startNotify(status);

        SingleTools.onEvent(status);
    }



    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//Empty Method
    }

//    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
//Empty Method
    }

//    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//Empty Method
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
//Empty Method
    }

//    @Override
    public void onPlayerError(ExoPlaybackException error) {
        SingleTools.onEvent(RadioPlaybackStatus.ERROR);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
//Empty Method
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//Empty Method
    }

    @Override
    public void onSeekProcessed() {
//Empty Method
    }


    public String getStreamUrl() {
        return streamUrl;
    }


    public void play(String streamUrl) {

        this.streamUrl = streamUrl;
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                SingleRadioService.this,
                Util.getUserAgent(SingleRadioService.this, getString(R.string.app_name)));

        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, new DefaultExtractorsFactory())
                .createMediaSource(MediaItem.fromUri(streamUrl));
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    private String getUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("Dalvik/");
        result.append(System.getProperty("java.vm.version"));
        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE;
        result.append(version.length() > 0 ? version : "1.0");

        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }

        String id = Build.ID;

        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }

        result.append(")");
        return result.toString();
    }

    public static int getAudioSessionId() {
        return exoPlayer.getAudioSessionId();
    }

    public void resume() {
        if (streamUrl != null)
            play(streamUrl);
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);


    }

    public void stop() {
        exoPlayer.stop();

    }

    public void playOrPause(String url) {

        if (streamUrl != null && streamUrl.equals(url)) {
            if (!isPlaying()) {
                play(streamUrl);
            } else {
                pause();
            }
        } else {
            if (isPlaying()) {
                pause();
            }
            play(url);
        }

    }

    public String getStatus() {

        return status;
    }

    @Override
    public void onMetadataReceived(final RMetadata data) {
      //Empty Method
    }

    public RMetadata getMetaData() {
        return notificationManager.getMetaData();
    }

    public Bitmap getAlbumArt() {
        return notificationManager.getAlbumArt();
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public boolean isPlaying() {
        return this.status.equals(RadioPlaybackStatus.PLAYING);
    }


}
