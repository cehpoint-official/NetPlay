package com.dbug.netplay.fragment;

import static android.content.Context.ALARM_SERVICE;

import static com.dbug.netplay.SplashActivity.MYPREFERENCE;
import static com.dbug.netplay.ads.MyApplication.getDisplaySize;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dbug.netplay.R;
import com.dbug.netplay.RadioServices.RadioPlaybackStatus;
import com.dbug.netplay.RadioServices.SingleRadioManager;
import com.dbug.netplay.RadioServices.metadata.RMetadata;
import com.dbug.netplay.databinding.DialogSelectTimeBinding;
import com.dbug.netplay.databinding.DialogTimeBinding;
import com.dbug.netplay.utils.SingleSharedPref;
import com.dbug.netplay.utils.SingleTools;
import com.dbug.netplay.utils.SleepTimeReceiver;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class RadioFragment extends Fragment implements OnClickListener, SingleTools.EventListener {

    SingleRadioManager singleRadioManager;
    String radioUrl,radioTitle;
    Activity activity;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    LinearLayout buttonPlayPause,linTimer,linVolume,linBack;
    ImageView imgPlayPause,imgVolume;
    SingleSharedPref singleSharedPref;
    EqualizerView equalizerView;
    SingleTools singleTools;

    private BarVisualizer audioVisualization;
    boolean bool = true;
    boolean rePlay = true;
    boolean fClick = false;
    private Dialog dialog;
    ValueAnimator animator;
    ObjectAnimator standAnimator;
    ImageView viewImage,standImage;
    float currentAngle = 0f;
    boolean checkOneTime = false;
    TextView radioName;
    SharedPreferences sharedPreferences;

    public RadioFragment() {
        // Required empty public constructor
    }
    private Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable runnable = new Runnable() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void run() {
            if (audioVisualization != null) {
                audioVisualization.release();
            }

            try {
                int audioSessionId = SingleRadioManager.getService().getAudioSessionId();
                if (audioSessionId != -1) {
                    try {
                        audioVisualization.setAudioSessionId(audioSessionId);
                    } catch (Exception e) {
                        Log.d("fcgvhb", "run: "+e.getMessage());
                        buttonPlayPause.performClick();
                    }
                }
            } catch (Exception e) {
                Log.d("TAG", "run: " + e.getMessage());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_radio, container, false);
        activity = requireActivity();
        sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCE,Context.MODE_PRIVATE);
        if (getArguments() != null) {
            radioUrl = getArguments().getString("url");
            radioTitle = getArguments().getString("radio_title");
        }
        setHasOptionsMenu(true);

        singleSharedPref = new SingleSharedPref(getActivity());
        singleSharedPref.setCheckSleepTime();
        singleTools = new SingleTools();

        initializeUIElements();

        return relativeLayout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SingleTools.isOnlineShowDialog(activity);


        singleRadioManager = SingleRadioManager.with();

        progressBar.setVisibility(View.VISIBLE);

        AsyncTask.execute(() -> activity.runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
//            updateButtons();
        }));

        if (isPlaying()) {
            onAudioSessionId(SingleRadioManager.getService().getAudioSessionId());
            Log.d("fcgv", "onActivityCreated: "+SingleRadioManager.getService().getAudioSessionId());
        }

    }

    @Override
    public void onEvent(String status) {
        if (RadioPlaybackStatus.LOADING.equals(status)) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (!status.equals(RadioPlaybackStatus.LOADING))
            progressBar.setVisibility(View.INVISIBLE);
        updateButtons();

    }

    @Override
    public void onAudioSessionId(Integer i) {


        //Empty
    }

    @Override
    public void onStart() {
        super.onStart();
        SingleTools.registerAsListener(this);

    }

    @Override
    public void onStop() {
        SingleTools.unregisterAsListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (!singleRadioManager.isPlaying())
            singleRadioManager.unbind(getContext());
        super.onDestroy();
    }

    private void initializeUIElements() {
        radioName = relativeLayout.findViewById(R.id.radio_name);
        radioName.setText(radioTitle);
        progressBar = relativeLayout.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        audioVisualization = relativeLayout.findViewById(R.id.audio_visualize_view);

        viewImage = relativeLayout.findViewById(R.id.image_anim);
        boolean darkMode= sharedPreferences.getBoolean("darkMode", false);
        if (darkMode){
            Glide.with(requireActivity())
                    .load(R.drawable.radio_img_dark)
                    .into(viewImage);
        }
        standImage = relativeLayout.findViewById(R.id.stand_image_anim);
        animator = ValueAnimator.ofFloat(currentAngle, 360f);
        animator.setDuration(15000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        standAnimator = ObjectAnimator.ofFloat(standImage, "rotation", 0f, -50f);

        linVolume = relativeLayout.findViewById(R.id.lin_volume);
        imgVolume = relativeLayout.findViewById(R.id.volume_img);
        linVolume.setOnClickListener(view -> changeVolume());
        final AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volumeLevel==0){
            imgVolume.setImageResource(R.drawable.ic_volume_off);
        }else if (volumeLevel>=1 && volumeLevel<10){
            imgVolume.setImageResource(R.drawable.ic_volume_medium);
        }else if (volumeLevel>10){
            imgVolume.setImageResource(R.drawable.ic_volume);
        }

        linTimer = relativeLayout.findViewById(R.id.lin_timer);
        linTimer.setOnClickListener(view -> {
           if (singleSharedPref.getIsSleepTimeOn()) {
                openTimeDialog();
            } else {
                openTimeSelectDialog();
            }
        });

        buttonPlayPause = relativeLayout.findViewById(R.id.lin_play_pause);
        imgPlayPause = relativeLayout.findViewById(R.id.img_play_pause);
        buttonPlayPause.setOnClickListener(this);

        linBack = relativeLayout.findViewById(R.id.lin_back);
        linBack.setOnClickListener(v -> activity.onBackPressed());
    }


    private void updateButtons() {
        if (isPlaying() || progressBar.getVisibility() == View.VISIBLE) {
            //If another stream is playing, show this in the layout
            if (SingleRadioManager.getService() != null && radioUrl != null && !radioUrl.equals(SingleRadioManager.getService().getStreamUrl())) {
                imgPlayPause.setImageResource(R.drawable.ic_play);
                //If this stream is playing, adjust the buttons accordingly
            } else {
                if (SingleRadioManager.getService() != null && SingleRadioManager.getService().getMetaData() != null) {
                    onMetaDataReceived(SingleRadioManager.getService().getMetaData(), SingleRadioManager.getService().getAlbumArt());
                }
                imgPlayPause.setImageResource(R.drawable.ic_pause);
            }
        } else {
            //If this stream is paused, adjust the buttons accordingly
            imgPlayPause.setImageResource(R.drawable.ic_play);
        }
        if (!isPlaying()){
            animator.pause();
            if (checkOneTime){
                standAnimation();
            }
        }else {
            startAnimation();
            standAnimation();
        }
    }

    private void standAnimation() {
        if (checkOneTime){
            standAnimator = ObjectAnimator.ofFloat(standImage, "rotation", -50f, 0f);
            standAnimator.setDuration(500);
        }else {
            standAnimator = ObjectAnimator.ofFloat(standImage, "rotation", 0f, -50f);
            standAnimator.setDuration(1000);
        }
        standAnimator.setInterpolator(new LinearInterpolator());
        standAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                float newAngle = (float) standAnimator.getAnimatedValue();
                if (newAngle==0f) {
                    standImage.setRotation(0f);
                    checkOneTime = false;
                }else {
                    standImage.setRotation(-50f);
                    checkOneTime = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        standAnimator.start();
    }

    private void startAnimation() {
        animator = ValueAnimator.ofFloat(currentAngle, 360f+currentAngle);
        animator.setDuration(15000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                viewImage.setRotation(currentAngle);
            }
        });
        animator.start();
    }

    private void pauseAnimation() {
        // store the current angle in a variable
    }


    @Override
    public void onClick(View v) {
        requestStoragePermission();
        if (!fClick){
            fClick = true;
            rePlay = false;

        }else {
            fClick = false;
            if (!rePlay){
                handler.postDelayed(runnable,0);
            }

        }


    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    private void startStopPlaying() {
        singleRadioManager.playOrPause(radioUrl);
        updateButtons();
    }





    @Override
    public void onMetaDataReceived(RMetadata meta, Bitmap image) {
     //Empty
    }

    private boolean isPlaying() {
        return (null != singleRadioManager && null != SingleRadioManager.getService() && SingleRadioManager.getService().isPlaying());
    }




    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (!isPlaying()) {
                                if (radioUrl != null) {
                                    startStopPlaying();
                                    AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                                    int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    if (volumeLevel < 2) {
                                        Toast.makeText(activity, getActivity().getResources().getString(R.string.volume_low), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(activity, getActivity().getResources().getString(R.string.error_retry_later), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                startStopPlaying();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Log.d("TAG", "requestStoragePermission: "+error))
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void changeVolume() {
        final RelativePopupWindow popupWindow = new RelativePopupWindow(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.volume, null);
        VerticalSeekBar seekBar = view.findViewById(R.id.seek_bar_volume);

        final AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volumeLevel);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                if (i==0){
                    imgVolume.setImageResource(R.drawable.ic_volume_off);
                }else if (i>=1 && i<10){
                    imgVolume.setImageResource(R.drawable.ic_volume_medium);
                }else if (i>10){
                    imgVolume.setImageResource(R.drawable.ic_volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Empty

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Empty

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(view);
        popupWindow.showOnAnchor(linVolume, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

    public void openTimeSelectDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogSelectTimeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_select_time, null, false);
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.8f;
        window.setAttributes(wlp);
        int width = (getDisplaySize(getActivity(),"w") / 10) * 8;
        dialog.getWindow().setLayout(width,ViewGroup.LayoutParams.WRAP_CONTENT);

        final IndicatorSeekBar seekbar = IndicatorSeekBar
                .with(getActivity())
                .min(1)
                .max(120)
                .progress(1)
                .thumbColor(singleSharedPref.getSecondColor())
                .indicatorColor(singleSharedPref.getFirstColor())
                .indicatorTextColor(singleSharedPref.getIndicateTextColor())
                .trackProgressColor(singleSharedPref.getFirstColor())
                .trackBackgroundColor(singleSharedPref.getSecondColor())
                .build();

        seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                binding.txtMinutes.setText(seekParams.progress + " " + getString(R.string.min));
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                //Empty method
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                //Empty method

            }
        });

        binding.frameLayout.addView(seekbar);

        binding.linCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        binding.linOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String hours = String.valueOf(seekbar.getProgress() / 60);
                String minute = String.valueOf(seekbar.getProgress() % 60);

                if (hours.length() == 1) {
                    hours = "0" + hours;
                }

                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                String totalTime = hours + ":" + minute;
                long totalTimer = singleTools.convertToMilliSeconds(totalTime) + System.currentTimeMillis();

                Random random = new Random();
                int id = random.nextInt(100);

                singleSharedPref.setSleepTime(true, totalTimer, id);

                Intent intent = new Intent(getActivity(), SleepTimeReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_MUTABLE);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, totalTimer, pendingIntent);
                dialog.dismiss();
            }
        });
        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    public void openTimeDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogTimeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_time, null, false);
        dialog.setContentView(binding.getRoot());

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.8f;
        window.setAttributes(wlp);
        int width = (getDisplaySize(getActivity(),"w") / 10) * 8;
        dialog.getWindow().setLayout(width,ViewGroup.LayoutParams.WRAP_CONTENT);

        binding.linCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        binding.linOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SleepTimeReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), singleSharedPref.getSleepID(), i, PendingIntent.FLAG_MUTABLE);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
                singleSharedPref.setSleepTime(false, 0, 0);
                dialog.dismiss();
            }
        });
        updateTimer(binding.txtTime, singleSharedPref.getSleepTime());
        if (!getActivity().isFinishing()) {
            dialog.show();
        }
    }

    private void updateTimer(final TextView textView, long time) {
        long timeleft = time - System.currentTimeMillis();
        if (timeleft > 0) {
            @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeleft),
                    TimeUnit.MILLISECONDS.toMinutes(timeleft) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeleft) % TimeUnit.MINUTES.toSeconds(1));

            textView.setText(hms);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (singleSharedPref.getIsSleepTimeOn()) {
                    updateTimer(textView, singleSharedPref.getSleepTime());
                }

            }, 1000);


        }
    }

    @Override
    public void onPause() {
        if (!bool){
            if (audioVisualization != null) {
                audioVisualization.release();
            }
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtons();
        singleRadioManager.bind(getContext());

        if (!isPlaying()){
            if (isOnline()){
                bool = false;
                fClick = true;
                handler.postDelayed(runnable,0);

            }

        }else {
            bool = true;
            handler.postDelayed(runnable,0);
        }
    }
    @Override
    public void onDestroyView() {
        if (!bool){
            if (audioVisualization != null) {
                audioVisualization.release();
            }
        }

        super.onDestroyView();
    }
}