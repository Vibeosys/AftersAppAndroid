package com.aftersapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by shrinivas on 23-09-2016.
 */
public class OpenSansBoldTextView extends TextView {
    public OpenSansBoldTextView(Context context) {
        super(context);
    }

    public OpenSansBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Bold.ttf"));
    }

    public OpenSansBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Bold.ttf"));
    }
}
