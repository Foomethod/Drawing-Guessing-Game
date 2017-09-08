package com.example.yumak.fyp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Foooooo on 10/02/2017.
 */

public class Splash extends BaseActivity{

    String the_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        fullScreenMode();

        hideActionBar();

        Bundle bundle = getIntent().getExtras();
        the_username = bundle.getString("the_name");

        ImageView image = (ImageView) findViewById(R.id.loading);
        Animation rotator = AnimationUtils.loadAnimation(this,R.anim.rotation);
        image.startAnimation(rotator);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(the_username))
                {
                    Toast.makeText(Splash.this, "Welcome back " + the_username, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.child(the_username).child("Registered").setValue("Yes");
                    Toast.makeText(Splash.this, "Welcome " + the_username, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                setResult(223, intent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Splash.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
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
