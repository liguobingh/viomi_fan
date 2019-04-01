package com.yunmi.fan.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yunmi.fan.R;

import org.jetbrains.annotations.Nullable;


import static android.content.ContentValues.TAG;

/**
 * Created by liguobin on 2018/10/19.
 */

public class WindNatureView extends View {
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private int mWidth;
    private int mHeight;
    private int width;  // 位图的宽度
    private int height;  // 位图的高度
    private float scaleWight;  // 位图的宽度要缩放的倍数
    private float scaleHeight;  // 位图的高度要缩放的倍数
    private float scale = 0.5f;  // 根据框的大小，图标缩放的比例
    private float angle = 0;
    private RectF rectFHead;
    private Handler handler;
    private boolean state;
    private SweepGradient mSweepGradient;
    private Matrix matrix;
    private Bitmap res;

    public WindNatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        handler = new Handler();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);
        matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ym_button_model_ai_white);
        rectFHead = new RectF(8 + (mWidth - 16 - (mHeight - 16)) / 2, 8, mWidth - 8 - (mWidth - 16 - (mHeight - 16)) / 2, mHeight - 8);
        mSweepGradient = new SweepGradient(mWidth / 2, mHeight / 2, new int[]{Color.TRANSPARENT, Color.WHITE}, null);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(resizeBitmap(bitmap, mWidth, mHeight), (mWidth - mHeight * scale) / 2, (mHeight - mHeight * scale) / 2, paint);
        if (state) {
            matrix.setRotate(angle - 90, mWidth / 2, mHeight / 2);
            mSweepGradient.setLocalMatrix(matrix);
            paint.setShader(mSweepGradient);
            canvas.drawArc(rectFHead, -90, angle, false, paint);
            Log.d(TAG, "onDraw:自然风模式运行了吗");
            angle = angle + 10;
            paint.setShader(null);
        }
        if (angle == Integer.MAX_VALUE) {
            angle = 0;
        }
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            if (newHeight < newWidth) {
                scaleWight = ((float) newHeight * scale) / width;
                scaleHeight = ((float) newHeight * scale) / height;
            } else {
                scaleWight = ((float) newWidth * scale) / width;
                scaleHeight = ((float) newWidth * scale) / height;
            }
            Matrix mMatrix = new Matrix();
            mMatrix.postScale(scaleWight, scaleHeight);
            res = Bitmap.createBitmap(bitmap, 0, 0, width, height, mMatrix, true);
            mMatrix = null;
            return res;
        } else {
            return null;
        }
    }

    public void start() {
        state = true;
        angle = 0;
        invalidate();
        handler.postDelayed(runnable, 50);
    }

    public void stop() {
        state = false;
        angle = 0;
        invalidate();
        handler.removeCallbacks(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, 50);
        }
    };
}
