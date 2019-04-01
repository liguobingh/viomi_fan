package com.yunmi.fan.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import com.yunmi.fan.R;

/**
 * Created by mayn on 2018/11/13.
 */
public class TempCircle extends View {

    private Paint paint;
    private float mWidth;
    private float mHeight;
    private RectF rectF;

    public TempCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setAlpha(10);
        paint.setAntiAlias(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        rectF = new RectF(2, 2, mWidth - 2, mHeight - 2);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i <= 9; i++) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth /2 - i * 8, paint);
        }
    }
}
