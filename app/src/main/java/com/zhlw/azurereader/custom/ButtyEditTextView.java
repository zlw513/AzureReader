package com.zhlw.azurereader.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhlw.azurereader.R;

/**
 * author:zlw
 * 2020-11-28
 */
public class ButtyEditTextView extends ViewGroup {

    private static final int TYPETEXT = 0;
    private static final int TYPEEDIT = 1;

    private static final int COLOR_DEFAULT = Color.parseColor("#ffffff");
    private static final int BGCOLOR_GRAY = Color.parseColor("#ffd7d7d7");

    private String TAG = "zlww";
    private Paint mBgPaint;//背景画笔
    private int indicatorInitRadius;
    private int mStrokeWidth = 8;
    private int height;
    private int width;
    private boolean drawdefault;
    private RectF rectF;
    private Path path;
    private EditText editText;
    private ButtyEditextOnclickListener listener;

    public void setListener(ButtyEditextOnclickListener listener) {
        this.listener = listener;
    }

    private int curtype = 0;

    public ButtyEditTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ButtyEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtyEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View et = this.getChildAt(0);
        LayoutParams lp = et.getLayoutParams();
        et.layout(0,0,lp.width,lp.height);
        if (drawable == null){
            initdrawable(l,t,r,b);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (TYPETEXT == curtype) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    private enum InputType {
        text(0x00000001),
        textPassword(0x00000081);

        private final int type;

        InputType(int num){
            type = num;
        }

        public int getType() {
            return type;
        }

    }

    private void init(final Context context, AttributeSet attrs) {
        initBackGround();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ButtyEditTextView);
        String displayname = array.getString(R.styleable.ButtyEditTextView_text_display);
        String hiltName = array.getString(R.styleable.ButtyEditTextView_hilt_name);
        String inputtype = array.getString(R.styleable.ButtyEditTextView_cur_inputtype);
        array.recycle();

        editText = new EditText(context);
        editText.setTextSize(20);
        editText.setWidth(LayoutParams.WRAP_CONTENT);
        editText.setHeight(LayoutParams.WRAP_CONTENT);
        editText.setBackground(null);
        editText.setSingleLine(true);
        if (!TextUtils.isEmpty(hiltName)) editText.setHint(hiltName);
        if (!TextUtils.isEmpty(inputtype)) editText.setInputType(InputType.valueOf(inputtype).getType());
        if (TextUtils.isEmpty(displayname)){
            curtype = TYPEEDIT;
            editText.setTextColor(Color.BLACK);
            editText.setFocusable(true);
            editText.setOnFocusChangeListener(new OnFocus());
        } else {
            curtype = TYPETEXT;
            editText.setTextColor(Color.WHITE);
            editText.setText(displayname);
            editText.setFocusable(false);
            editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        addView(editText);
    }

    Drawable drawable;

    private void initBackGround() {
//        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(mStrokeWidth);

        rectF = new RectF();//默认的背景
        path  = new Path();

        setBackgroundColor(Color.parseColor("#00ffffff"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawdefault) {
            Log.d(TAG, "onDraw: ");
            editText.setBackground(null);
            drawselect(canvas);//其实这就像个中间透明的遮罩
        } else {
            editText.setBackground(drawable);
        }
    }

    private void drawselect(Canvas canvas){
        mBgPaint.setShadowLayer(indicatorInitRadius>>2, 0, 3, BGCOLOR_GRAY);//画阴影
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setColor(COLOR_DEFAULT);//边框的颜色
        canvas.clipPath(path);
        canvas.drawPath(path, mBgPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        LayoutParams params = editText.getLayoutParams();
        params.width = width;
        params.height = height;
        editText.setLayoutParams(params);
        indicatorInitRadius = height >> 2;

        rectF.set(0, 0, width, height);
        path.addRoundRect(rectF, height>>2, height>>2, Path.Direction.CW);//将默认背景改为圆角矩形
    }

    private class OnFocus implements OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange: ");
            if (!hasFocus){
                drawdefault = false;
                invalidate();
            } else {
                drawdefault = true;
                invalidate();
            }
        }
    }
    
    private void initdrawable(final int l, final int t, final int r, final int b){
        drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(4);
                p.setAntiAlias(true);
                if (TYPETEXT == curtype){
                    p.setColor(Color.parseColor("#dfffff"));
                    p.setShadowLayer(16, 0, -10, Color.WHITE);
                } else {
                    p.setColor(Color.parseColor("#1f777777"));
                    p.setShadowLayer(7, 0, 5, Color.parseColor("#ff777777"));
                }
                canvas.drawLine(0, (float) ((b-t)*0.85), r-l,(float) ((b-t)*0.85), p);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        };
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void cancelFocus(){
        if (editText.isFocused()) editText.clearFocus();
    }

    public interface ButtyEditextOnclickListener{
        void onclick();
    }

    public EditText getEditText() {
        return editText;
    }

}
