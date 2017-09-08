package com.example.yumak.fyp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Foooooo on 01/06/2017.
 */

public class GameRecordActivity extends BaseActivity{

    final String NAME_KEY = "column_name";
    final String SCORE_KEY = "column_score";
    final String[] FROM = {NAME_KEY, SCORE_KEY};
    final int[] TO = {R.id.column_name, R.id.column_score};
    List<HashMap<String, String>> theList = new ArrayList<HashMap<String, String>>();
    ListView high_score;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.high_score);

        showActionBar();

        FontReplacer.Replace(this, findViewById(R.id.high_score), "fonts/KGLifeisMessy.ttf");

        high_score = (ListView) findViewById(R.id.highscoreList);

        db.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    if (!child.getKey().equals("Registered")) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put(NAME_KEY, child.getKey());
                        hashMap.put(SCORE_KEY, child.getValue().toString() + " s");
                        theList.add(hashMap);
                    }
                }
                simpleAdapter = new SimpleAdapter(GameRecordActivity.this, theList, R.layout.listview_custom, FROM, TO);
                high_score.setAdapter(simpleAdapter);
                System.out.println(theList.toString());
                System.out.println(simpleAdapter.getCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
