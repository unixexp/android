package com.example.tscalculator.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;
import com.example.tscalculator.ts.Driver;

public class Logo extends View {

    protected float logoMarginHorizonalPercent = 10;
    protected float logoMarginVerticalPercent = 25;
    private Drawable LOGO_DRAWABLE = getContext().getDrawable(R.drawable.logo);
    private Drawable drawable = null;

    public Logo(Context context) {
        super(context);
        init();
    }

    public Logo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Logo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Logo(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.addOnLayoutChangeListener(new OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                configureDrawBounds();
            }});
    }

    protected void configureDrawBounds() {
        final float logoDrawableRatio = getDrawableRatio(LOGO_DRAWABLE);
        final int logoMarginHorizonal = Math.round((getWidth() * logoMarginHorizonalPercent) / 100);
        final int logoMarginVertical = Math.round((getHeight() * logoMarginVerticalPercent) / 100);
        final int logoDrawableWidth = Math.round(getWidth() - logoMarginHorizonal);
        final int logoDrawableHeight = Math.round((logoDrawableWidth - logoMarginHorizonal)
                / logoDrawableRatio) + logoMarginVertical;
        final int logoDrawableX = logoMarginHorizonal;
        final int logoDrawableY = logoMarginVertical;
        LOGO_DRAWABLE.setBounds(logoDrawableX, logoDrawableY,
                logoDrawableWidth, logoDrawableHeight);
    }

    private float getDrawableRatio(Drawable drawable) {
        final float w = drawable.getIntrinsicWidth();
        final float h = drawable.getIntrinsicHeight();
        try {
            return w / h;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        LOGO_DRAWABLE.draw(canvas);
    }

}
