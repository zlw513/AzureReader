package com.zhlw.azurereader.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.zhlw.azurereader.utils.SPUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * author:zlw
 * 2021-03-01
 */
public class SelectColorView extends View {

    private Paint cyclePaint;
    private Paint checkPaint;
    private Paint shadowPaint;
    private int color = Color.BLACK;
    private boolean isChecked = false;
    private int width;
    private int height;
    private int offset = 10;
    private Context mContext;
    private Path path;
    private Path pathCycle;

    public SelectColorView(Context context) {
        super(context);
        init(context);
    }

    public SelectColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private int curColor;

    private void init(Context context){
        setLayerType(LAYER_TYPE_SOFTWARE, null);
//        curColor = (int) SPUtils.get(context.getApplicationContext(), "colorselect", Integer.valueOf(Color.BLACK));
        SharedPreferences preferences = context.getSharedPreferences("colorselect", MODE_PRIVATE);
        curColor = preferences.getInt("colorselect",Color.WHITE);

        mContext = context;
        checkPaint = new Paint();
        checkPaint.setStrokeWidth(6);
        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setColor(Color.WHITE);
        checkPaint.setAntiAlias(true);

        cyclePaint = new Paint();
        cyclePaint.setStyle(Paint.Style.FILL);
        cyclePaint.setAntiAlias(true);
        cyclePaint.setColor(color);

        shadowPaint = new Paint();
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER));
        shadowPaint.setShadowLayer(20,0,0 ,Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width/2f,height/2f,(width/2f)-offset,cyclePaint);
        if (isChecked) {
            if (pathCycle == null){
                pathCycle = new Path();
                pathCycle.addCircle(width/2f,height/2f,(width/2f)-offset, Path.Direction.CCW);
            }
            canvas.clipPath(pathCycle);
            canvas.drawPath(pathCycle, shadowPaint);
            drawOk(canvas, width/2, height/2);
        }
    }

    private void drawOk(Canvas canvas,int centerx,int centery){
        if (path == null){
            path = new Path();
            path.moveTo(centerx*0.52f,centery*0.95f);
            path.lineTo(centerx*0.82f, centery*1.34f);
            path.lineTo(centerx*1.58f, centery*0.60f);
        }
        canvas.drawPath(path, checkPaint);
    }

    public void setColor(int color){
        this.color = color;
        cyclePaint.setColor(color);
        if (curColor == color) {
            isChecked = true;
        }
    }

    public void setChecked(boolean check) {
        isChecked = check;
        if (check)
            saveColor(color);
        invalidate();
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void saveColor(int color){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("colorselect", MODE_PRIVATE).edit();
        editor.putInt("colorselect",color);
        editor.apply();//我这里需要立刻修正数据
//        SPUtils.put(mContext.getApplicationContext(), "colorselect",Integer.valueOf(color));
    }

}
