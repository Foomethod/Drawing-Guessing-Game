package com.example.yumak.fyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Foooooo on 24/04/2017.
 */

public class LoginActivity extends BaseActivity{

    private EditText usernameField;
    private String namefromfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        showActionBar();

        FontReplacer.Replace(this, findViewById(R.id.loginPage), "fonts/KGLifeisMessy.ttf");

        usernameField = (EditText) findViewById(R.id.usernameText);

        db = FirebaseDatabase.getInstance().getReference();
    }

    public void onLoginClick(View view)
    {
        namefromfield = usernameField.getText().toString();
        final String cleansedName = getLowerCaseStringsOnly(namefromfield, false);

        if(!cleansedName.isEmpty() && cleansedName.length() <= 16) {
            name = cleansedName;
            Intent intent = new Intent(LoginActivity.this, Splash.class);
            intent.putExtra("the_name", cleansedName);
            startActivityForResult(intent, 322);
        }
        else
        {
            Toast.makeText(LoginActivity.this,
                    "Invalid name, spaces and special characters are ignored. Please enter a valid name with alphabets and/or numbers only, maximum of 16 characters allowed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 322 && resultCode == 223)
        {
            openLandingPage();
        }
    }

    private void openLandingPage()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //method to remove special characters and spaces, true to keep spaces
    public static String getLowerCaseStringsOnly(String s, Boolean withspaces)
    {
        Pattern pattern = Pattern.compile("[^a-z A-Z 0-9]");
        Matcher matcher = pattern.matcher(s);
        String str = matcher.replaceAll("").toLowerCase();

        if(!withspaces)
        {
            str = str.replaceAll("\\s","");
        }

        return str;
    }
}
