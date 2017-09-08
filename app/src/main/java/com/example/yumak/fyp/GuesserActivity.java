package com.example.yumak.fyp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Foooooo on 03/03/2017.
 */

public class GuesserActivity extends BaseActivity{

    final long FIVE_MEGABYTES = 1024 * 1024 * 5;
    final long countDownTime = 60000;
    final long countDownInterval = 1000;
    VideoView drawing_vid;
    TextView timer;
    Button submitButton;
    EditText user_answer;
    ImageView times_up;
    ImageView yes;
    ImageView no;
    TextView hint_text;

    FirebaseStorage storage;
    StorageReference jsonRef;
    List<String> video_name = new ArrayList<String>();
    List<String> video_link = new ArrayList<String>();
    List<String> video_sequence = new ArrayList<String>();
    FileDownloadTask downloadTask;
    CountDownTimer countDownTimer;

    String vidname;
    String vidlink;
    AtomicLong atomicLong = new AtomicLong(0);
    int stopPosition;

    AnimationActivity animationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        video_name = Collections.synchronizedList(video_name);
        video_link = Collections.synchronizedList(video_link);
        video_sequence = Collections.synchronizedList(video_sequence);
        animationActivity = new AnimationActivity(GuesserActivity.this);

        setContentView(R.layout.game);

        showActionBar();

        FontReplacer.Replace(this, findViewById(R.id.game), "fonts/KGLifeisMessy.ttf");

        drawing_vid = (VideoView) findViewById(R.id.drawing_video);
        timer = (TextView) findViewById(R.id.timerView);
        submitButton = (Button) findViewById(R.id.submitButton);
        user_answer = (EditText) findViewById(R.id.answerField);
        times_up = (ImageView) findViewById(R.id.times_up_image);
        yes = (ImageView) findViewById(R.id.yes_image);
        no = (ImageView) findViewById(R.id.no_image);
        hint_text = (TextView) findViewById(R.id.hint_text);

        //replay
        drawing_vid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!drawing_vid.isPlaying()) {
                    drawing_vid.start();
                }
                return false;
            }
        });

        obtainVideo();
    }

    public void obtainVideo()
    {
        FirebaseOptions firebaseOptions = FirebaseApp.getInstance().getOptions();
        System.out.println("Bucket = " + firebaseOptions.getStorageBucket());

        storage = FirebaseStorage.getInstance();
        jsonRef = storage.getReferenceFromUrl(
                "https://firebasestorage.googleapis.com/v0/b/drawing-game.appspot.com/o/Video%20List%2Fvideolink.json?alt=media&token=c65c287a-50af-4e11-acef-690217e6c839");
        try {
            final File file = File.createTempFile("videolink","json");
            final StringBuilder text = new StringBuilder();
            downloadTask = jsonRef.getFile(file);
            downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            text.append(line);
                        }
                        br.close();

                        JSONObject jsonObject = new JSONObject(text.toString());
                        JSONArray videos = jsonObject.getJSONArray("videos");
                        for(int i = 0; i < videos.length(); i++)
                        {
                            JSONObject obj = videos.getJSONObject(i);
                            String vidname = obj.getString("name");
                            String link = obj.getString("link");

                            video_name.add(i,vidname);
                            video_sequence.add(i,vidname);
                            video_link.add(i,link);
                        }

                        Collections.shuffle(video_sequence);

                        downloadThenRemoveFromIndex();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadThenRemoveFromIndex()
    {
        if(video_sequence.isEmpty())//Exit game if all video is played, reset round if there is unplayed videos
        {
            new AlertDialog.Builder(this)
                    .setTitle("Complete!")
                    .setMessage("Congratulations! You have completed the game!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
        else {
            String s = video_sequence.get(0);
            vidname = video_name.get(video_name.indexOf(s));
            vidlink = video_link.get(video_name.indexOf(s));

            StorageReference httpsRef = storage.getReferenceFromUrl(vidlink);

            httpsRef.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(new File(getCacheDir(), vidname + ".mp4"));
                        output.write(bytes);
                        output.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    drawing_vid.setVideoPath(getCacheDir() + "/" + vidname + ".mp4");
                    drawing_vid.start();
                    video_sequence.remove(0); //remove first index
                    setHints();
                    runTimer();
                }
            });
        }
    }

    public void runTimer()
    {
        final StringBuilder hintbuilder = new StringBuilder();
        countDownTimer = new CountDownTimer(countDownTime, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf((int) (millisUntilFinished/countDownInterval)));
                atomicLong.set(millisUntilFinished/countDownInterval);
                if(atomicLong.intValue() == 30)
                {
                    for(int i = 0; i < vidname.length() - 1; i++)
                    {
                        hintbuilder.append("_");
                    }
                    hint_text.setText(vidname.charAt(0) + hintbuilder.toString());
                }
                else if(atomicLong.intValue() == 15 && vidname.length() > 3)
                {
                    hintbuilder.deleteCharAt(0);
                    hint_text.setText(vidname.substring(0,2) + hintbuilder.toString());
                }
            }

            @Override
            public void onFinish() {
                animationActivity.runAnimSet(times_up, 3);
                resetRound();
            }
        }.start();
    }

    public void onSubmitButtonClick(View view)
    {
        final String placeholder = vidname;
        final String s = LoginActivity.getLowerCaseStringsOnly(user_answer.getText().toString(), true);
        if(s.equals(vidname))
        {
            countDownTimer.cancel();
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(name).hasChild(placeholder))
                    {
                        if(dataSnapshot.child(name).child(placeholder).getValue(Integer.class) < atomicLong.intValue())
                                {
                                    db.child(name).child(placeholder).setValue((int)atomicLong.get());
                                    Toast.makeText(GuesserActivity.this,"New high score for " + placeholder + " is " + atomicLong.get(), Toast.LENGTH_SHORT).show();
                                }
                        else
                        {
                        }
                    }
                    else
                    {
                        db.child(name).child(placeholder).setValue(atomicLong.intValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            animationActivity.runAnimSet(yes, 1);


            resetRound();
        }
        else
        {
            animationActivity.runAnimSet(no, 2);
            user_answer.setError("Answer didn't match");
            user_answer.selectAll();
        }
    }

    public void setHints()
    {
        StringBuilder underscores = new StringBuilder();
        final int getHintLength = vidname.length();
        for(int i = 0; i < getHintLength; i++)
        {
            underscores.append("_");
        }
        hint_text.setText(underscores.toString());

    }

    public void resetRound()
    {
        user_answer.setText("");
        drawing_vid.stopPlayback();
        downloadThenRemoveFromIndex();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = drawing_vid.getCurrentPosition();
        drawing_vid.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        drawing_vid.seekTo(stopPosition);
        drawing_vid.start();
    }
}