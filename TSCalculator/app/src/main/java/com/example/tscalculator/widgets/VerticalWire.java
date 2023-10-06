package com.example.tscalculator.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;

public class VerticalWire extends View {

    private int MINUS = 0;
    private int PLUS = 1;

    private Paint paint;
    private int single;

    public VerticalWire(Context context) {
        super(context);
        init();
    }

    public VerticalWire(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public VerticalWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init();
    }

    public VerticalWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Wire, 0, 0);
        single = a.getInt(R.styleable.Wire_single, -1);

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

        float stepX = paint.getStrokeWidth() * 8;
        float x = paint.getStrokeWidth();

        if (single == -1 || single == PLUS) {
            paint.setColor(getContext().getColor(R.color.colorEightV1));
            canvas.drawLine(x, 0, x, getHeight(), paint);
        }

        if (single == -1 || single == MINUS) {
            paint.setColor(getContext().getColor(R.color.colorThreeV1));
            canvas.drawLine(x + stepX, 0, x + stepX, getHeight(), paint);
        }

    }

    public void setSingle(int single) {
        this.single = single;
    }

}
