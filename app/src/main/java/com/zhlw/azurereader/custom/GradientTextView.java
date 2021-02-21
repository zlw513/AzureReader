package com.zhlw.azurereader.custom;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class GradientTextView extends TextView {

    private Paint mPaint;
    private int mDx;
    private Matrix matrix;
    private LinearGradient mLinearGradient;

    public GradientTextView(Context context){
        super(context);
        initView();
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GradientTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mPaint = getPaint();//这里获取的是text view里面定义的那根画笔
        matrix = new Matrix();
    }

    /*
    onLayout方法是ViewGroup中子View的布局方法，用于放置子View的位置。放置子View很简单，
    只需在重写onLayout方法，然后获取子View的实例，调用子View的layout方法实现布局。
    在实际开发中，一般要配合onMeasure测量方法一起使用。
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ValueAnimator animator = ValueAnimator.ofInt(0,2 * getMeasuredWidth());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDx = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setDuration(3500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
        mLinearGradient = new LinearGradient(-getMeasuredWidth(), 0, 0, 0, new int[]{
                getCurrentTextColor(),0xffff9569,0xffe92758,0xffff9569,getCurrentTextColor()
        },new float[]{0f,0.20f,0.5f,0.80f,1f},Shader.TileMode.CLAMP);
        //CLAMP的作用是如果渲染器超出原始边界范围，则会复制边缘颜色对超出范围的区域进行着色
    }

    /*
    super.onDraw(canvas);写在前还是后的差别

    如果我们自定义View ，是通过直接继承 View 类,然后重写它的 onDraw() 方法,那么绘制代码写在 super.onDraw() 的上面还是下面都无所谓。
    甚至，你把 super.onDraw() 这行代码删掉都没关系，效果都是一样的。
    因为在 View 这个类里，onDraw() 本来就是空实现，就是要给子类实现的。

    但是如果继承自具有某种功能的控件，去重写它的 onDraw() ，在里面添加一些绘制代码，做出一个「进化版」的控件，
    就要考虑你的绘制代码是应该写在 super.onDraw() 的上面还是下面了。

    1.1 写在 super.onDraw() 的下面
    把绘制代码写在 super.onDraw() 的下面，由于绘制代码会在原有内容绘制结束之后才执行，所以绘制内容就会盖住控件原来的内容。

    1.2 写在 super.onDraw() 的上面
    由于绘制代码会执行在原有内容的绘制之前，所以绘制的内容会被控件的原内容盖住。

    所以我们这里super.onDraw(canvas);要写在底下
     */
    @Override
    protected void onDraw(Canvas canvas) {
        matrix.setTranslate(mDx, 0);
        mLinearGradient.setLocalMatrix(matrix);//很多视图都可以通过矩阵改变自己的位置
        mPaint.setShader(mLinearGradient);
        super.onDraw(canvas);
    }
}
