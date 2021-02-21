package com.zhlw.azurereader.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.zhlw.azurereader.utils.SuperConfig;

/**
 * Created by Chu on 2017/8/22.
 */

public class ShapeTextView extends AppCompatTextView {

    public ShapeTextView(Context context) {
        super(context);
    }

    public ShapeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSuperShapeView(attrs);
    }

    public ShapeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSuperShapeView(attrs);
    }

    private void initSuperShapeView(AttributeSet attrs) {
        new SuperConfig().beSuperView(attrs, this);
    }
}
