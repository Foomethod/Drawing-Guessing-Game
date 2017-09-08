package com.example.yumak.fyp;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Foooooo on 28/05/2017.
 */

public class AnimationActivity{

    Context context;
    Animation animation_appear;
    Animation animation_disappear;
    MediaPlayer mediaPlayer;

    public AnimationActivity(Context context)
    {
        animation_appear = AnimationUtils.loadAnimation(context, R.anim.appear_in);
        animation_disappear = AnimationUtils.loadAnimation(context, R.anim.appear_out);
        this.context = context;
    }

    public void runAnimSet(final ImageView image, final int code)
    {
        //1 = correct guess
        //2 = wrong guess
        //3 = times up
        image.setVisibility(View.VISIBLE);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animation_appear);
        animationSet.addAnimation(animation_disappear);
        image.startAnimation(animationSet);

        animation_disappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(code == 1) {
                    mediaPlayer = MediaPlayer.create(context, R.raw.tada);
                }
                else if(code == 2) {
                    mediaPlayer = MediaPlayer.create(context, R.raw.error);
                }
                else if(code == 3) {
                    mediaPlayer = MediaPlayer.create(context, R.raw.cuckoo);
                }
                mediaPlayer.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

}