package com.aftersapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by akshay on 01-10-2016.
 */
public class NetworkRoundImageView extends NetworkImageView {

    public NetworkRoundImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NetworkRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap originalImage = ((BitmapDrawable) drawable).getBitmap();

        if (originalImage == null) {
            return;
        }
        Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        int vertex = (int) Math.sqrt(w * w + h * h);
        Bitmap roundBitmap = getCroppedBitmap(bitmap, (int) (h / 1.3));
        canvas.drawBitmap(roundBitmap, 0, 0, null);

        //originalImage.recycle();
        roundBitmap.recycle();
        bitmap.recycle();
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#7F83D3"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        return output;
    }
}
