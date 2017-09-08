package com.example.yumak.fyp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by Foooooo on 03/02/2017.
 */

public class FontReplacer {

    public static void Replace(Context context, View root, String fontName){
        if(root instanceof ViewGroup)
        {
            ViewGroup viewGroup = (ViewGroup) root;
            for(int i = 0; i < viewGroup.getChildCount(); i++)
            {
                Replace(context,viewGroup.getChildAt(i),fontName);
            }
        }
        else if(root instanceof TextView)
        {
            ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
        }
    }
}
