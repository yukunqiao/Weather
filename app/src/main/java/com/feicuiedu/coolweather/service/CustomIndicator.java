package com.feicuiedu.coolweather.service;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.feicuiedu.coolweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/2.
 */

public class CustomIndicator extends LinearLayout {
    private Context mContext;
    private int width;
    private int height;
    private int margin;
    private Drawable normalDrawable, selectedDrawable;
    private int count = 0;
    private int currentCount = 0;
    private List<ImageView> views = new ArrayList<ImageView>();

    public CustomIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomIndicator);
        margin = (int) a.getDimension(R.styleable.CustomIndicator_margin, 0);
        width = (int) a.getDimension(R.styleable.CustomIndicator_width, 0);
        height = (int) a.getDimension(R.styleable.CustomIndicator_height, 0);
        count = a.getInteger(R.styleable.CustomIndicator_count, 0);
        normalDrawable = a.getDrawable(R.styleable.CustomIndicator_normal_icon);
        selectedDrawable = a.getDrawable(R.styleable.CustomIndicator_selected_icon);
        a.recycle();
        initViews();
    }

    public CustomIndicator(Context context) {
        super(context);
    }

    public void setCurrentPosition(int pos) {
        currentCount = pos;
        if(currentCount < 0) {
            currentCount = 0;
        }
        if(currentCount > count-1) {
            currentCount = count-1;
        }

        for(int i = 0; i < count; i++) {
            views.get(i).setBackgroundDrawable(normalDrawable);
        }
        views.get(currentCount).setBackgroundDrawable(selectedDrawable);
    }

    public void next() {
        setCurrentPosition(currentCount+1);
    }

    public void previous() {
        setCurrentPosition(currentCount-1);
    }

    public void setCount(int count) {
        this.count = count;
        this.currentCount = 0;
    }

    private void initViews() {
        views.clear();
        for(int i = 0; i < count; i++) {
            ImageView view = new ImageView(mContext);
            views.add(view);
            LayoutParams params = new LayoutParams(width == 0 ? LayoutParams.WRAP_CONTENT : width,
                    height == 0 ? LayoutParams.WRAP_CONTENT : height);
            if(i != count-1) {
                params.rightMargin = margin;
            }
            view.setLayoutParams(params);
            view.setBackgroundDrawable(normalDrawable);
            this.addView(view);
        }
        setCurrentPosition(0);
    }
}
