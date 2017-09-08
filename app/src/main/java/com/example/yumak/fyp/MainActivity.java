package com.example.yumak.fyp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fullscreen mode
        fullScreenMode();

        //Action Bar
        showActionBar();

        //Font Replacing
        FontReplacer.Replace(this, findViewById(R.id.activity_main), "fonts/KGLifeisMessy.ttf");

        //set the username on the bar above
        setNameonBar();
    }

    //Return to fullscreen mode after lockscreen/multitasking
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //Open SettingsActivity page
    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    //Open Game Record
    public void openGameRecord(View view)
    {
        Intent intent = new Intent(MainActivity.this, GameRecordActivity.class);
        startActivity(intent);
    }

    //Open Game
    public void openDrawer(View view)
    {
        Intent intent = new Intent(MainActivity.this, GuesserActivity.class);
        startActivity(intent);
    }

    public void setNameonBar()
    {
        TextView usernameField = (TextView) findViewById(R.id.displayUsername);
        usernameField.setText(name);
    }
}
