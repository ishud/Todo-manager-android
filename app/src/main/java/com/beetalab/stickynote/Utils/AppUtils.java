package com.beetalab.stickynote.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppUtils {

    public static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("sticky_preference", Context.MODE_PRIVATE);
        return preferences;
    }


    public static String formateDate(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE dd MMM yyyy hh : mm a");
        return simpleDateFormat.format(date);
    }

    public static String getDateStringFormStringSource(String source) {

        if (source != null && !source.equals("")) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd hh : mm");
            try {
                final Date parse = simpleDateFormat.parse(source);

                if (parse != null)
                    return formateDate(parse);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static long getDateasLong(String source, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            final Date parse = dateFormat.parse(source);
            if (parse != null)
                return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static String getDateFromString(String source, String pattern,String toPattern) {
        if (source != null && !source.equals("")) {

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            try {
                final Date parse = simpleDateFormat.parse(source);

                if (parse != null)
                {
                    SimpleDateFormat f = new SimpleDateFormat(toPattern);
                    return f.format(parse);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getTimeString(boolean is24Hours, long source) {

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(source);

        SimpleDateFormat s;
        if (is24Hours) {
            s = new SimpleDateFormat("HH : mm");
        } else {
            s = new SimpleDateFormat("hh : mm a");
        }

        return s.format(calendar.getTime());

    }

    public static String getformateDate(Date date, String pattern) {

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }


    public static Drawable generateIcon(Context context, int resID, int color, boolean includeBackground) {
        Drawable dummyDrawable = ContextCompat.getDrawable(context, resID);  //context.getResources().getDrawable(resID);
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), resID);
        Bitmap colorBitmap = Bitmap.createBitmap(
                dummyDrawable.getIntrinsicWidth(),
                dummyDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        colorBitmap.eraseColor(color);

        Bitmap result = Bitmap.createBitmap(dummyDrawable.getIntrinsicWidth(), dummyDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(Color.BLACK);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(colorBitmap, 0, 0, paint1);
        canvas.drawBitmap(mask, 0, 0, paint);

        Drawable resultDrawable = new BitmapDrawable(context.getResources(), result);
//        result.recycle();
//        colorBitmap.recycle();
//        mask.recycle();

//        if (includeBackground) {
//            ColorDrawable backgroundLayer = new ColorDrawable(predictionBarColor);
//            backgroundLayer.setBounds(0, 0, resultDrawable.getIntrinsicWidth(), resultDrawable.getIntrinsicHeight());
//            return new LayerDrawable(new Drawable[]{backgroundLayer, resultDrawable});
//        } else {
        return resultDrawable;
        // }
        // return resultDrawable;
    }

    public static int getDensity(Context context){

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) displayMetrics.density;
    }

    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager)context.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        int width;
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

}
