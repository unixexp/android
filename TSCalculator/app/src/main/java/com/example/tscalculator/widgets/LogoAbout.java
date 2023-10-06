package com.example.tscalculator.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;

public class LogoAbout extends View {

    protected float logoMarginHorizonalPercent = 10;
    protected float logoMarginVerticalPercent = 20;
    private Drawable LOGO_DRAWABLE = getContext().getDrawable(R.drawable.logo);
    private Drawable drawable = null;

    float logoDrawableRatio;
    int logoMarginHorizonal;
    int logoMarginVertical;
    int logoDrawableWidth;
    int logoDrawableHeight;
    int logoDrawableX;
    int logoDrawableY;

    private Paint paint;
    private float textSize;

    public LogoAbout(Context context) {
        super(context);
        init();
    }

    public LogoAbout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LogoAbout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LogoAbout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);

        this.addOnLayoutChangeListener(new OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                configureDrawBounds();
            }});
    }

    protected void configureDrawBounds() {
        logoDrawableRatio = getDrawableRatio(LOGO_DRAWABLE);
        logoMarginHorizonal = Math.round((getWidth() * logoMarginHorizonalPercent) / 100);
        logoMarginVertical = Math.round((getHeight() * logoMarginVerticalPercent) / 100);
        logoDrawableWidth = Math.round(getWidth() - logoMarginHorizonal);
        logoDrawableHeight = Math.round((logoDrawableWidth - logoMarginHorizonal)
                / logoDrawableRatio) + logoMarginVertical;
        logoDrawableX = logoMarginHorizonal;
        logoDrawableY = logoMarginVertical;
        LOGO_DRAWABLE.setBounds(logoDrawableX, logoDrawableY,
                logoDrawableWidth, logoDrawableHeight);

        textSize = ((getWidth() * 4) / 100);
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
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getContext().getColor(R.color.colorThreeV1));
        paint.setTextSize(textSize);

        float y = logoDrawableHeight + (textSize * 2);
        canvas.drawText(getContext().getString(R.string.version), getWidth() / 2, y, paint);
        y += (textSize * 3);
        canvas.drawText(getContext().getString(R.string.about_line1), getWidth() / 2, y, paint);
        y += textSize;
        canvas.drawText(getContext().getString(R.string.about_line2), getWidth() / 2, y, paint);
        y += (textSize * 3);
        canvas.drawText(getContext().getString(R.string.copyright), getWidth() / 2, y, paint);
    }

}
