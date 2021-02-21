package com.zhlw.azurereader.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhlw.azurereader.R;

public class RoundImageView extends View {
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mPaint;
    private Integer mStyle = 0,mRadius = 5;
    Matrix matrix = new Matrix();

    public RoundImageView(Context context, @Nullable AttributeSet attrs) throws Exception{
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs) throws Exception{
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.RoundImageView);
        int bitmapStyle = typedArray.getResourceId(R.styleable.RoundImageView_view_src,-1);
        if (bitmapStyle == -1){
            throw new Exception("需要定义Src属性,而且必须是图像");
        }
        mBitmap = BitmapFactory.decodeResource(getResources(),bitmapStyle);
        mStyle = typedArray.getInt(R.styleable.RoundImageView_style,0);
        if (mStyle == 1){
            mRadius = typedArray.getInt(R.styleable.RoundImageView_radius,5);
        }
        typedArray.recycle();
        mPaint = new Paint();
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = (float) getWidth()/mBitmap.getWidth();
        matrix.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(matrix);
        mPaint.setShader(mBitmapShader);
        float half = getWidth() / 2;

        if (mStyle == 0){
            canvas.drawCircle(half,half,getWidth()/2,mPaint);
        }else if (mStyle == 1){
            canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),mRadius,mRadius,mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        setMeasuredDimension((measureWidthMode == MeasureSpec.EXACTLY) ? measureWidth: width, (measureHeightMode == MeasureSpec.EXACTLY) ? measureHeight: height);
    }

}
