package com.aftersapp.utils.qbutils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;


import com.aftersapp.AftersAppApplication;
import com.aftersapp.R;

import java.util.Random;

public class UiUtils {

    private static final int RANDOM_COLOR_START_RANGE = 0;
    private static final int RANDOM_COLOR_END_RANGE = 9;

    private static final Random random = new Random();
    private static int previousColor;

    private UiUtils() {
    }

    public static Drawable getGreyCircleDrawable() {
        return getColoredCircleDrawable(AftersAppApplication.getInstance().getResources().getColor(R.color.gridBorder));
    }

    public static Drawable getRandomColorCircleDrawable() {
        return getColoredCircleDrawable(getRandomCircleColor());
    }

    public static Drawable getColorCircleDrawable(int colorPosition) {
        return getColoredCircleDrawable(getCircleColor(colorPosition % RANDOM_COLOR_END_RANGE));
    }

    private static Drawable getColoredCircleDrawable(@ColorInt int color) {
        GradientDrawable drawable = (GradientDrawable) AftersAppApplication.getInstance().getResources().getDrawable(R.drawable.shape_circle);
        drawable.setColor(color);
        return drawable;
    }

    public static int getRandomCircleColor() {
        int randomNumber = random.nextInt(RANDOM_COLOR_END_RANGE) + 1;

        int generatedColor = getCircleColor(randomNumber);
        if (generatedColor != previousColor) {
            previousColor = generatedColor;
            return generatedColor;
        } else {
            do {
                generatedColor = getRandomCircleColor();
            } while (generatedColor != previousColor);
        }
        return previousColor;
    }

    public static int getCircleColor(@IntRange(from = RANDOM_COLOR_START_RANGE, to = RANDOM_COLOR_END_RANGE)
                                     int colorPosition) {
        String colorIdName = String.format("random_color_%d", colorPosition + 1);
        int colorId = AftersAppApplication.getInstance().getResources()
                .getIdentifier(colorIdName, "color", AftersAppApplication.getInstance().getPackageName());

        return AftersAppApplication.getInstance().getResources().getColor(colorId);
    }
}
