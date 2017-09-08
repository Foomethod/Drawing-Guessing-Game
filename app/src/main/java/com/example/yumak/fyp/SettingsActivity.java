package com.example.yumak.fyp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Foooooo on 13/02/2017.
 */

public class SettingsActivity extends BaseActivity{

    TextView brightness_percent;
    TextView volume_percent;
    CheckBox checkBox;
    SeekBar sneakbar;
    SeekBar volumebar;
    AudioManager audio;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        showActionBar();

        fullScreenMode();

        FontReplacer.Replace(this, findViewById(R.id.settings), "fonts/KGLifeisMessy.ttf");

        //Check if Modify Settings is allowed in this app
        if(android.provider.Settings.System.canWrite(this.getApplicationContext()))
        {
            sneakbar = (SeekBar) findViewById(R.id.seekBar);
            brightness_percent = (TextView) findViewById(R.id.percentageView);
            volume_percent = (TextView) findViewById(R.id.textView4);

            sneakbar.setMax(255);
            float brightness = 0;
            try {
                brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            int screenBrightness = (int) brightness;
            brightness_percent.setText(toBrightnessPercentage(screenBrightness) + "%");
            sneakbar.setProgress(screenBrightness);

            sneakbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int progressValue = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressValue = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progressValue);
                    brightness_percent.setText(toBrightnessPercentage(progressValue) + "%");
                }
            });

            checkBox = (CheckBox) findViewById(R.id.ab_CheckBox);

            try {
                if(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1)
                {
                    checkBox.setChecked(true);
                }
                else
                {
                    checkBox.setChecked(false);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                    }
                    else
                    {
                        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    }
                }
            });

            volumebar = (SeekBar) findViewById(R.id.volume_bar);
            audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumebar.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumebar.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
            volume_percent.setText(toVolumePercentage(volumebar.getProgress())+ "%");
            volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int progressValue = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    progressValue = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    volume_percent.setText(toVolumePercentage(progressValue) + "%");
                }
            });


        }
        else //If not provided, start activity to allow permission
        {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    public void backButton(View view)
    {
        this.finish();
    }

    //Return to onCreate() Activity
    public void onExitButtonClick(View view)
    {
        finishAffinity();
    }

    private int toBrightnessPercentage(int brightness_value)
    {
        float a = (float) brightness_value/255 * 100;
        int b = (int) a;
        return b;
    }

    private int toVolumePercentage(int volume_value)
    {
        float a = (float) volume_value/15 * 100;
        int b = (int) a;
        return b;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
