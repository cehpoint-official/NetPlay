package com.dbug.netplay.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.dbug.netplay.FirebaseNotificationService.NOTIFICATION_CHANNEL_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbug.netplay.R;
import com.dbug.netplay.activity.MainActivity;
import com.dbug.netplay.config.Constant;
import com.dbug.netplay.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding fragmentSettingsBinding;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    public static final String MYPREFERENCE = "mypref";
    AlertDialog.Builder builder;
    private boolean notificationEnable,vibrator,notifi,darkMode,listGrid;

    private int trackColor,thumbColor,trackColorActive,thumbColorActive;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences(MYPREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        vibrator= sharedPreferences.getBoolean("vibrate", false);
        notifi= sharedPreferences.getBoolean("notification", true);
        darkMode= sharedPreferences.getBoolean("darkMode", false);
        listGrid= sharedPreferences.getBoolean("listGrid", false);
        if (darkMode){
            thumbColor = ContextCompat.getColor(getContext(),R.color.thumb_tint_dark);
            trackColor = ContextCompat.getColor(getContext(),R.color.track_tint_dark);
            thumbColorActive = ContextCompat.getColor(getContext(),R.color.thumb_tint_active_dark);
            trackColorActive = ContextCompat.getColor(getContext(),R.color.track_tint_active_dark);
        }else {
            thumbColor = ContextCompat.getColor(getContext(),R.color.thumb_tint);
            trackColor = ContextCompat.getColor(getContext(),R.color.track_tint);
            thumbColorActive = ContextCompat.getColor(getContext(),R.color.thumb_tint_active);
            trackColorActive = ContextCompat.getColor(getContext(),R.color.track_tint_active);
        }

        builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        fragmentSettingsBinding.switchNotification.setOnClickListener(view1 -> {
            if (fragmentSettingsBinding.switchNotification.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchNotification.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                    fragmentSettingsBinding.switchNotification.setTrackTintList(ColorStateList.valueOf(trackColorActive));
                }
                notificationEnable = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();
                if (!notificationEnable){
                    getNotificationPermisssion();
                }else {
                    fragmentSettingsBinding.switchNotification.setChecked(true);
                    editor.putBoolean("notification", true);
                    editor.apply();
                }
                // The toggle is enabled
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchNotification.setThumbTintList(ColorStateList.valueOf(thumbColor));
                    fragmentSettingsBinding.switchNotification.setTrackTintList(ColorStateList.valueOf(trackColor));
                }
                fragmentSettingsBinding.switchNotification.setChecked(false);
                editor.putBoolean("notification", false);
                editor.apply();
                // The toggle is disabled
            }
        });
        fragmentSettingsBinding.switchVibrate.setOnClickListener(view1 -> {
            if (fragmentSettingsBinding.switchVibrate.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchVibrate.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                    fragmentSettingsBinding.switchVibrate.setTrackTintList(ColorStateList.valueOf(trackColorActive));
                }
                fragmentSettingsBinding.switchVibrate.setChecked(true);
                editor.putBoolean("vibrate", true);
                editor.apply();
                // The toggle is enabled
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchVibrate.setThumbTintList(ColorStateList.valueOf(thumbColor));
                    fragmentSettingsBinding.switchVibrate.setTrackTintList(ColorStateList.valueOf(trackColor));
                }
                fragmentSettingsBinding.switchVibrate.setChecked(false);
                editor.putBoolean("vibrate", false);
                editor.apply();
                // The toggle is disabled
            }
        });
        fragmentSettingsBinding.switchDark.setOnClickListener(view1 -> {
            if (fragmentSettingsBinding.switchDark.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchDark.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                    fragmentSettingsBinding.switchDark.setTrackTintList(ColorStateList.valueOf(trackColorActive));
                }
                fragmentSettingsBinding.switchDark.setChecked(true);
                editor.putBoolean("darkMode", true);
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                // The toggle is enabled
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchDark.setThumbTintList(ColorStateList.valueOf(thumbColor));
                    fragmentSettingsBinding.switchDark.setTrackTintList(ColorStateList.valueOf(trackColor));
                }
                fragmentSettingsBinding.switchDark.setChecked(false);
                editor.putBoolean("darkMode", false);
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                // The toggle is disabled
            }
        });
        fragmentSettingsBinding.switchCardType.setOnClickListener(view1 -> {
            if (fragmentSettingsBinding.switchCardType.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchCardType.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                    fragmentSettingsBinding.switchCardType.setTrackTintList(ColorStateList.valueOf(trackColorActive));
                }
                fragmentSettingsBinding.cardTypeMsg.setText(getResources().getString(R.string.grid));
                fragmentSettingsBinding.switchCardType.setChecked(true);
                editor.putBoolean("listGrid", true);
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                // The toggle is enabled
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentSettingsBinding.switchCardType.setThumbTintList(ColorStateList.valueOf(thumbColor));
                    fragmentSettingsBinding.switchCardType.setTrackTintList(ColorStateList.valueOf(trackColor));
                }
                fragmentSettingsBinding.cardTypeMsg.setText(getResources().getString(R.string.list));
                fragmentSettingsBinding.switchCardType.setChecked(false);
                editor.putBoolean("listGrid", false);
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
                // The toggle is disabled
            }
        });

        fragmentSettingsBinding.feedbackButton.setOnClickListener(view1 -> openWebPage(Constant.FEEDBACK_URL));
        fragmentSettingsBinding.faqButton.setOnClickListener(view1 -> openWebPage(Constant.FAQ_URL));
        fragmentSettingsBinding.privacy.setOnClickListener(view1 -> openWebPage(Constant.PRIVACY_URL));
        fragmentSettingsBinding.terms.setOnClickListener(view1 -> openWebPage(Constant.Terms_URL));

        if(notifi){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchNotification.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                fragmentSettingsBinding.switchNotification.setTrackTintList(ColorStateList.valueOf(trackColorActive));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchNotification.setThumbTintList(ColorStateList.valueOf(thumbColor));
                fragmentSettingsBinding.switchNotification.setTrackTintList(ColorStateList.valueOf(trackColor));
            }
        }
        if(vibrator){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchVibrate.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                fragmentSettingsBinding.switchVibrate.setTrackTintList(ColorStateList.valueOf(trackColorActive));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchVibrate.setThumbTintList(ColorStateList.valueOf(thumbColor));
                fragmentSettingsBinding.switchVibrate.setTrackTintList(ColorStateList.valueOf(trackColor));
            }
        }
        if(darkMode){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchDark.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                fragmentSettingsBinding.switchDark.setTrackTintList(ColorStateList.valueOf(trackColorActive));
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchDark.setThumbTintList(ColorStateList.valueOf(thumbColor));
                fragmentSettingsBinding.switchDark.setTrackTintList(ColorStateList.valueOf(trackColor));
            }
        }
        if(listGrid){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchCardType.setThumbTintList(ColorStateList.valueOf(thumbColorActive));
                fragmentSettingsBinding.switchCardType.setTrackTintList(ColorStateList.valueOf(trackColorActive));
            }
            fragmentSettingsBinding.cardTypeMsg.setText(getResources().getString(R.string.grid));
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragmentSettingsBinding.switchCardType.setThumbTintList(ColorStateList.valueOf(thumbColor));
                fragmentSettingsBinding.switchCardType.setTrackTintList(ColorStateList.valueOf(trackColor));
            }
            fragmentSettingsBinding.cardTypeMsg.setText(getResources().getString(R.string.list));
        }

        fragmentSettingsBinding.switchVibrate.setChecked(vibrator);
        fragmentSettingsBinding.switchNotification.setChecked(notifi);
        fragmentSettingsBinding.switchDark.setChecked(darkMode);
        fragmentSettingsBinding.switchCardType.setChecked(listGrid);

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
    }

    private void getNotificationPermisssion() {
        builder.setMessage("Do you want to get notification? need to enable notification.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName())
                            .putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID);
                    startActivity(settingsIntent);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Allow notification ");
        alert.show();

        return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View view = fragmentSettingsBinding.getRoot();
        return view;
    }

    public void openWebPage(String url) {

        Uri webpage = Uri.parse(url);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webpage = Uri.parse("http://" + url);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}