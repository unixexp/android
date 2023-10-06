package com.example.tscalculator.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;

public class ZigzagWire extends View {

    private Paint paint;

    public ZigzagWire(Context context) {
        super(context);
        init();
    }

    public ZigzagWire(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ZigzagWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init();
    }

    public ZigzagWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(3.0f);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0)
            return;

        float x;
        x = paint.getStrokeWidth();
        paint.setColor(getContext().getColor(R.color.colorEightV1));
        canvas.drawLine(x, getHeight(), x, getHeight() / 2.0f, paint);
        canvas.drawLine(x, getHeight() / 2.0f,
                getWidth()-x, getHeight() / 2.0f, paint);
        canvas.drawLine(getWidth()-x, getHeight() / 2.0f,
                getWidth()-x, 0, paint);

    }

}
