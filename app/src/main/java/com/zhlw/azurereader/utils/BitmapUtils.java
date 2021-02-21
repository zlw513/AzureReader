package com.zhlw.azurereader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class BitmapUtils {
    private static Bitmap DrawableToBitmap(Context context, int vectorDrawableId){
        //解析vector矢量图为bitmap
        Bitmap bitmap = null;
        Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
        bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
