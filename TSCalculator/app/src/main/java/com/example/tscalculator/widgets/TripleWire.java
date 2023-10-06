package com.example.tscalculator.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;

public class TripleWire extends View {

    private int MINUS = 0;
    private int PLUS = 1;

    private Paint paint;
    private boolean end;
    private int singleVertical;

    public TripleWire(Context context) {
        super(context);
        init();
    }

    public TripleWire(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

        init();
    }

    public TripleWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init();
    }

    public TripleWire(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Wire, 0, 0);
        end = a.getBoolean(R.styleable.Wire_end, false);
        singleVertical = a.getInt(R.styleable.Wire_singleVertical, -1);

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
        float y = getHeight() / 3.0f;

        paint.setColor(getContext().getColor(R.color.colorEightV1));
        if (end) {
            canvas.drawLine(0, y,
                    getWidth() - (stepX), y, paint);
        } else {
            canvas.drawLine(0, y, getWidth(), y, paint);
        }

        if (singleVertical == -1 || singleVertical == PLUS) {
            if (end)
                canvas.drawLine(getWidth() - (stepX) - (paint.getStrokeWidth()/2.0f), 0,
                    getWidth() - (stepX) - (paint.getStrokeWidth()/2.0f), y, paint);
            else
                canvas.drawLine(paint.getStrokeWidth(), 0,
                        paint.getStrokeWidth(), y, paint);
        }

        paint.setColor(getContext().getColor(R.color.colorThreeV1));
        canvas.drawLine(0, y + y, getWidth(), y + y, paint);

        if (end) {
            x = getWidth() - (paint.getStrokeWidth() / 2.0f);
        } else {
            x += stepX;
        }

        if (singleVertical == -1 || singleVertical == MINUS)
            canvas.drawLine(x , 0, x, y + y, paint);
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
