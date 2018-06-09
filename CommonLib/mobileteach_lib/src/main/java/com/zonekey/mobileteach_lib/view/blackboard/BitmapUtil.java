package com.zonekey.mobileteach_lib.view.blackboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.zonekey.mobileteach_lib.util.LogUtil;

import java.io.IOException;

/**
 * Created by xu.wang
 * Date on 2017/4/18 17:28
 */
public class BitmapUtil {
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static int getBitmapDegree(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.d("TAG", "cannot read exif" + ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    public static Bitmap drawable2BitmapOnMainUi(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void drawableToBitmap(final Drawable drawable, Context context, final OnDrawableToBitmapListener listener) {
        if (listener == null) {
            LogUtil.e("BitmapUtil", " OnDrawableToBitmapListener == null");
            return;
        }
        final Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Handler handler = new Handler(context.getApplicationContext().getMainLooper()); //draw方法在一些手机上必须在主线程中执行
        handler.post(new Runnable() {
            @Override
            public void run() {
                drawable.draw(canvas);
                listener.drawableConvertBitmapSuccess(bitmap);
            }
        });

    }

    public interface OnDrawableToBitmapListener {
        void drawableConvertBitmapSuccess(Bitmap bitmap);
    }
}
