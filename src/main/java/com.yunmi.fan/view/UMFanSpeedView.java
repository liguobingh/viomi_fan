package com.yunmi.fan.view;

import android.graphics.PathMeasure;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.yunmi.fan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * 风扇速度设置 扇形View
 * Created by liguobin on 2018/10/11.
 */
public class UMFanSpeedView extends View {
    final String TAG = "UMFanSpeedView";

    private Typeface TypefaceCache;
    private Paint mPaint;
    private Paint mPaintX;
    private PorterDuffXfermode xfermode;
    private DisplayMetrics metrics;
    private int r0, r1, r2;
    private int width0;   // 圆弧宽度：width0(透明)
    private float mStartAngle = 210;  //圆弧开始角度
    private float mSweepAngle = 120;  //圆弧扫过角度

    /**
     * 中心圆，外围圆
     */
    private int c1;
    private float percent = 0f;
    private int temp;

    private int rev;
    private int maxLenght, minLenght;
    private int waveWidth, waveHight;
    private int dotMargin;
    private int waveBaseline, waveBaselineMIN, waveBaselineMAX;
    private int dot1, dot2;   // 实心滑动按钮小圈圈
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];

    private int dotSize = 72;
    private DisplayMetrics displayMetrics;
    private int mode;
    private int dp10;
    private int min_rev;
    private int width = getWidth();  //控件的宽度和高度
    private int height = getHeight();
    int circleSize = 8;
    private double drawAngle;
    private boolean touchable;
    private double currentAngle;
    private float offset;
    private ValueAnimator valueAnimator, mAnimatorF, mAnimatorFinish;
    private boolean circleEnd;
    private Bitmap mBitmap;
    private List<Dot> dots = new ArrayList<>();
    private List<Dot> dotsVer = new ArrayList<>();
    private int verDotSize;
    private FanSpeedTask mTask;
    private OnSetRevListner mListner;

    // 圆弧起始点
    private float[] START_POINT = new float[] {20, height -20};
    // 圆弧终点
    private float[] END_POINT = new float[] {width - 20, height -20};

    public UMFanSpeedView(Context context) {
        this(context, null);
    }

    public UMFanSpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UMFanSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaintX = new Paint();
        mPaintX.setColor(Color.WHITE);
        mPaintX.setAntiAlias(true);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mPaintX.setXfermode(xfermode);  //设置图形重叠时的处理方式，如合并，取交集或并集， 经常用来制作橡皮的擦除效果！
        mPaintX.setColor(Color.BLACK);

        metrics = getResources().getDisplayMetrics();    // 声明方式：DisplayMetrics metrics;
        //applyDimension(int unit, float value, DisplayMetrics metrics) 是TypedValue的一个静态方法，主要用来将其他尺寸单位（例如dp，sp）转换为像素单位px。
        // 从另一方面说：给尺寸数字加上了单位（java中，尺寸单位一般为px）。
        // 第一个参数为TypedValue.COMPLEX_UNIT_DIP，padding为:4dip，返回值是4dip转换为px单位后的值。
        r0 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, metrics);  //r0: 540
        r1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 102, metrics);  //r1: 306
        r2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 115, metrics);  //r2: 345
        width0 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, metrics);  //width0: 18

        minLenght = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);  //minLenght: 30
        maxLenght = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, metrics);  //maxLenght: 180

        waveWidth = r0 * 2;  //waveWidth: 558
        waveHight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);  //waveHight: 45
        dotMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);  //dotMargin: 30
        dot1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics);  //dot1: 60
        dot2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, metrics);  //dot2: 105
        dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);  //dp10: 30
        displayMetrics = getResources().getDisplayMetrics();
    }

    /**
     * 转速
     */
    public void setMin_rev(int min_rev) {
        this.min_rev = min_rev;
        if (rev < min_rev)
            rev = min_rev;
//        Log.e("@@@@@", "rev: " + rev);
    }

    public void setRev(int rev) {
        if (rev < min_rev) return;
        this.rev = rev;
//        Log.e("@@@@@", "rev: " + rev);
        percent = rev * 1f / 8f;
        invalidate();
    }

    public void setTemp(int temp) {
        this.temp = temp;
        invalidate();
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    public float getPercent() {
        return percent;
    }

    public void setmListner(OnSetRevListner mListner) {
        this.mListner = mListner;
    }

    //在控件大小发生改变时调用。所以这里初始化会被调用一次。作用：获取控件的宽和高度
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();  //onSizeChanged-width: 1080
//        Log.e("@@@@@", "onSizeChanged-width: " + width );
        height = getHeight();  //height: 600
//        Log.e("@@@@@", "onSizeChanged-height: " + height);
        waveBaseline = height / 2 + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, metrics);  //waveBaseline: 450
        waveBaselineMIN = height / 2 - r0;  //waveBaselineMIN: 21
        waveBaselineMAX = waveBaseline;  //waveBaselineMAX: 450
//        showFinishing();
    }

    public void setMode(int mode) {
        this.mode = mode;
        invalidate();
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mode) {
            case 0:
                valueAnimator = null;
                mAnimatorF = null;
                mAnimatorFinish = null;
                custom(canvas);
                break;
            case 1:
                valueAnimator = null;
                mAnimatorF = null;
                mAnimatorFinish = null;
                diandong(canvas);
                break;
            case 2:
                valueAnimator = null;
                mAnimatorF = null;
                mAnimatorFinish = null;
                appointing(canvas);
                break;
            case 3:
                valueAnimator = null;
                mAnimatorF = null;
                mAnimatorFinish = null;
                heating(canvas);
                break;
            case 4:
                mAnimatorFinish = null;
                smashing(canvas);
                showSmash();
                break;
            case 5:
                valueAnimator = null;
                mAnimatorF = null;
                mAnimatorFinish = null;
                keeping(canvas);
                break;
            case 6:
                valueAnimator = null;
                mAnimatorF = null;
                waveBaseline = 0;
                finishing(canvas);
//                showFinishing();
                break;
        }
    }

    public void custom(Canvas canvas) {
        mPaint.setAlpha(100);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
//        风扇模糊速度条（固定）
        RectF rectF = new RectF(0, 100, width, height + 200);  // custom-rectF: RectF(0.0, 100.0, 1080.0, 500.0)
        Log.e("@@@@@", "custom-rectF: " + rectF);
//        canvas.drawArc(rectF, mStartAngle, mSweepAngle, true, mPaint);
        Path path = new Path();
        path.addArc(rectF, mStartAngle, mSweepAngle );
        canvas.drawPath(path, mPaint);

        if (mode == 0 && touchable) {
           canvas.save();
            /**
             * 实心滑动按钮小圈圈
             */
            mPaint.setAlpha(255);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);

            canvas.drawCircle(width / 2 - r0, height / 2, dot1 / 2, mPaint);
            canvas.restore();

        } else {
            RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
            mPaint.setAlpha(204);
            mPaint.setStrokeWidth(width0);
            canvas.drawArc(rectF1, mStartAngle, mSweepAngle, false, mPaint);
        }

        String text = rev + "";
        Rect rectF2 = new Rect();
//        mPaint.setTypeface(TypefaceCache.getFaceText(getContext(), 1));
//        mPaint.setTextSize(textsizeCenter);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
        int textHeight = rectF2.height();

        mPaint.setAlpha(255);
        canvas.drawText(text, width / 2, height / 2 + textHeight / 2 + dp10, mPaint);

        text = getContext().getString(R.string.juicer_rev);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
    }

    public void diandong(Canvas canvas) {
        percent = rev * 1f / 7f;
        if (percent > 1) {
            percent = 1;
        }
    }

    public void appointing(Canvas canvas) {
        RectF rectF = new RectF(width / 2 - r0, height / 2 - r0, width / 2 + r0, height / 2 + r0);
        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
        float[] positions = {0f, 1};
        int[] colors = {Color.parseColor("#51ffffff"), Color.WHITE};
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, positions);
        Matrix matrix = new Matrix();
        matrix.setRotate(percent - 90, width / 2, height / 2);
        sweepGradient.setLocalMatrix(matrix);
        mPaint.setShader(sweepGradient);
        canvas.drawOval(rectF, mPaint);
        mPaint.setShader(null);

        RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
        mPaint.setAlpha(204);
        mPaint.setStrokeWidth(width0);
        canvas.drawOval(rectF1, mPaint);

        circleSize = 10;
        mPaint.setAlpha(76);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < circleSize; i++) {
            canvas.save();
            canvas.rotate(-20 + i * (360f / (circleSize - 1)), width / 2, height / 2);
            canvas.drawCircle(width / 2 - r2, height / 2, c1, mPaint);
            canvas.restore();
        }
    }

    public void heating(Canvas canvas) {
        percent = temp * 1f / 100f;
        RectF rectF = new RectF(width / 2 - r0, height / 2 - r0, width / 2 + r0, height / 2 + r0);
        mPaint.setAlpha(76);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mPaint);

        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(width0);
        float[] positions = {0f, 0.6f * percent};
        int[] colors = {Color.parseColor("#00ffffff"), Color.WHITE};
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, positions);
        Matrix matrix = new Matrix();
        matrix.setRotate(157, width / 2, height / 2);
        sweepGradient.setLocalMatrix(matrix);
        mPaint.setShader(sweepGradient);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle * percent, false, mPaint);
        mPaint.setShader(null);

        RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
        mPaint.setAlpha(204);
        mPaint.setStrokeWidth(width0);
        canvas.drawArc(rectF1, mStartAngle, mSweepAngle, false, mPaint);

//        mPaint.setTypeface(TypefaceCache.getFaceText(getContext(), 1));
        String text = temp + "";
        Rect rectF2 = new Rect();
//        mPaint.setTextSize(textsizeCenter);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
        int textWith = rectF2.width();
        int textHeight = rectF2.height();

        mPaint.setAlpha(255);
        canvas.drawText(text, width / 2, height / 2 + textHeight / 2 + dp10, mPaint);

//        mPaint.setTypeface(TypefaceCache.getFaceText(getContext(), 0));
        text = getContext().getString(R.string.juicer_du);
//        mPaint.setTextSize(textsizeSub);
        mPaint.setAlpha(76);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
//        int textWith2 = rectF2.width();
//        int textHeight2 = rectF2.height();
//        canvas.drawText(text, width / 2 + textWith / 2 + textWith2, height / 2 - textHeight / 2 + textHeight2, mPaint);
    }

    public void smashing(Canvas canvas) {
        waveBaseline = (int) (waveBaselineMIN + (waveBaselineMAX - waveBaselineMIN) * percent);
        drawWave(canvas);
        RectF rectF = new RectF(width / 2 - r0, height / 2 - r0, width / 2 + r0, height / 2 + r0);
        mPaint.setAlpha(153);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(rectF, mPaint);
        mPaint.setShader(null);

        RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
        mPaint.setAlpha(204);
        mPaint.setStrokeWidth(width0);
        canvas.drawOval(rectF1, mPaint);
        if (circleEnd) {
            creatFinishDots();
            mPaint.setAlpha(153);
            mPaint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < dots.size(); i++) {
                canvas.save();
                canvas.rotate(90 - i * (360f / dotSize), width / 2, height / 2);
                if (i > 0)
                    canvas.drawCircle(width / 2 - r2 - dots.get(i).lenght, height / 2, c1, mPaint);
                canvas.restore();
            }
            if (dots.size() > 0) {
                for (int i = 0; i < dotsVer.size(); i++) {
                    int y = height / 2 - r2 - minLenght + dotMargin * (i + 1);
                    if (y < waveBaseline) {
                        canvas.drawCircle(width / 2, y, c1, mPaint);
                    }
                }
            }
        } else {
            mPaint.setAlpha(153);
            mPaint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < dots.size(); i++) {
                canvas.save();
                canvas.rotate(i * (360f / dots.size()), width / 2, height / 2);
                canvas.drawCircle(width / 2 - r2 - dots.get(i).currentLenght, height / 2, c1, mPaint);
                canvas.restore();
            }
        }
    }

    public void keeping(Canvas canvas) {
        percent = (temp - 50) * 1f / 30f;
        if (percent > 1)
            percent = 1;
        if (percent < 0)
            percent = 0;
        RectF rectF = new RectF(width / 2 - r0, height / 2 - r0, width / 2 + r0, height / 2 + r0);
        mPaint.setAlpha(76);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mPaint);

        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(width0);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle * percent, false, mPaint);

        RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
        mPaint.setAlpha(204);
        mPaint.setStrokeWidth(width0);
        canvas.drawArc(rectF1, mStartAngle, mSweepAngle, false, mPaint);

        String text = temp + "";
        Rect rectF2 = new Rect();
//        mPaint.setTypeface(TypefaceCache.getFaceText(getContext(), 1));
//        mPaint.setTextSize(textsizeCenter);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
        int textWith = rectF2.width();
        int textHeight = rectF2.height();

        mPaint.setAlpha(255);
        canvas.drawText(text, width / 2, height / 2 + textHeight / 2 + dp10, mPaint);

//        mPaint.setTypeface(TypefaceCache.getFaceText(getContext(), 0));
        text = getContext().getString(R.string.juicer_du);
//        mPaint.setTextSize(textsizeSub);
        mPaint.setAlpha(76);
        mPaint.getTextBounds(text, 0, text.length(), rectF2);
        int textWith2 = rectF2.width();
        int textHeight2 = rectF2.height();
        canvas.drawText(text, width / 2 + textWith / 2 + textWith2, height / 2 - textHeight / 2 + textHeight2, mPaint);
    }

    public void finishing(Canvas canvas) {
        drawWave(canvas);
        RectF rectF = new RectF(width / 2 - r0, height / 2 - r0, width / 2 + r0, height / 2 + r0);
        mPaint.setAlpha(153);
        mPaint.setStrokeWidth(width0);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(rectF, mPaint);
        mPaint.setShader(null);

        RectF rectF1 = new RectF(width / 2 - r1, height / 2 - r1, width / 2 + r1, height / 2 + r1);
        mPaint.setAlpha(204);
        mPaint.setStrokeWidth(width0);
        canvas.drawOval(rectF1, mPaint);

        if (waveBaseline == 0) {
//            drawNike(canvas);
        }
    }
// ==============================================================================================
    public void drawWave(Canvas canvas) {
        mPaint.setAlpha(255);
        mPaint.setStyle(Paint.Style.FILL);
        int save1 = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        RectF rectF = new RectF(width / 2 - r0 + width0 / 2, height / 2 - r0 + width0 / 2, width / 2 + r0 - width0 / 2, height / 2 + r0 - width0 / 2);
        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c2 = new Canvas(bitmap2);
        Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        p2.setColor(0xffffffff);
        p2.setAlpha(255);
        c2.drawOval(rectF, p2);
        canvas.drawBitmap(bitmap2, 0, 0, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Path path = getPath();
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c1 = new Canvas(bitmap1);
        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        p1.setColor(0xffffffff);
        p1.setAlpha(102);
        c1.drawPath(path, p1);
        canvas.drawBitmap(bitmap1, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(save1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mode == 0 && touchable)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mListner != null) {
                        mListner.onsetStart();
                    }
                case MotionEvent.ACTION_MOVE:
                    float downX = event.getX();
                    float downY = event.getY();
                    currentAngle = getAngle(downX, downY);
//                    LogUtil.v(TAG, "currentAngle:" + currentAngle);
                    if (currentAngle < Math.PI / 6) {
                        currentAngle = Math.PI / 6;
                    } else if (currentAngle > Math.PI * 5 / 6) {
                        currentAngle = Math.PI * 5 / 6;
                    } else if (currentAngle >= Math.PI / 6 && currentAngle <= Math.PI * 5 / 6) {
                        currentAngle = currentAngle - Math.PI / 6;
                    }


                    if (downY > height / 2) {
                        if (downX > width / 2) {
                            drawAngle = 0 + (180 - currentAngle);
                        } else {
                            drawAngle = -180 - currentAngle;
                        }
                    } else {
                        drawAngle = -210 + 20 + currentAngle;
                    }
                    if (drawAngle < -210) {
                        break;
                    }
                    if (drawAngle > 20) {
                        break;
                    }

                    if (mode == 0) {
                        percent = ((float) drawAngle + 210) / 120f;
//                        Log.e("@@@@@", "onTouchEvent-percent: " + percent);
                        rev = Math.round(8f * percent);  //四舍五入取整，算法为Math.floor(x+0.5),即将原来的数字加上0.5后再向下取整，所以，Math.round(11.5)的结果是12，Math.round(-11.5)的结果为-11.
//                        Log.e("@@@@@", "onTouchEvent-rev: " + rev);
                        if (rev < min_rev) {
                            rev = min_rev;
//                            Log.e("@@@@@", "onTouchEvent-rev12: " + rev);
                        }
                        percent = rev * 1f / 8f;
                        postInvalidate();
                    }
//                    Log.e("@@@@@", "drawAngle:" + drawAngle + " percent:" + percent + " rev:" + rev);
                    break;
                case MotionEvent.ACTION_UP:
                    mTask = new UMFanSpeedView.FanSpeedTask(updateHandler);
//                    performSelect();
                case MotionEvent.ACTION_CANCEL:
                    if (mListner != null) {
                        mListner.onsetRev(rev);
                    }
                    break;
            }
        return true;
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            performSelect();
        }
    };

    public double getAngle(float targetX, float targetY) {
        float centerY = height + 200 + 100 / 2;
        PointF mCenterPoint = new PointF(width / 2, centerY);
        PointF mPreMovePointF = new PointF(0, centerY);
        PointF mCurMovePointF = new PointF(targetX, targetY);
        double a = distance4PointF(mCurMovePointF, mPreMovePointF);
        double b = distance4PointF(mPreMovePointF, mCenterPoint);
        double c = distance4PointF(mCenterPoint, mCurMovePointF);
        double cosA = (b * b + c * c - a * a) / (2 * b * c);
        if (cosA >= 1) {
            cosA = 1f;
        } else if (cosA <= -1) {
            cosA = -1f;
        }
        double radian = Math.acos(cosA);
        return radian * 180 / Math.PI;
    }

    private double distance4PointF(PointF pf1, PointF pf2) {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        return Math.sqrt(disX * disX + disY * disY);
    }

    private Path getPath() {
        int itemWidth = waveWidth / 2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(0, waveBaseline);//起始坐标
        //核心的代码就是这里
        for (int i = 0; i < 7; i++) {
            int startX = i * itemWidth;
            int x1 = (int) (startX + itemWidth / 2 - offset);  //控制点的X,（起始点X + itemWidth/2 + offset);
            int x2 = (int) (startX + itemWidth - offset);   //结束点的X
            mPath.quadTo(x1,
                    getWaveHeigh(i),//控制点的Y
                    x2,
                    waveBaseline//结束点的Y
            );             //只需要处理完半个波长，剩下的有for循环自已就添加了。
        }
        //下面这三句话很重要，它是形成了一封闭区间，让曲线以下的面积填充一种颜色，大家可以把这3句话注释了看看效果。
        mPath.lineTo(6 * itemWidth, height);
        mPath.lineTo(0, height);
        mPath.close();
        return mPath;
    }

    //奇数峰值是正的，偶数峰值是负数
    private int getWaveHeigh(int num) {
        if (num % 2 == 0) {
            return waveBaseline + waveHight;
        }
        return waveBaseline - waveHight;
    }

    public void showSmash() {
        if (valueAnimator != null)
            return;
        creatDots();
        circleEnd = false;
        valueAnimator = ValueAnimator.ofFloat(0, 1f, 0f);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float process = (float) animation.getAnimatedValue();
                for (int i = 0; i < dots.size(); i++) {
                    dots.get(i).currentLenght = (int) (dots.get(i).lenght * process);
                }
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dotSize = dots.size();
                circleEnd = true;
                ValueAnimator animator = ValueAnimator.ofInt(0, dotSize);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        verDotSize = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
                animator.setDuration(3000);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
//                creatDots();
            }
        });
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();

        mAnimatorF = ValueAnimator.ofFloat(0, waveWidth);
        mAnimatorF.setInterpolator(new LinearInterpolator());
        mAnimatorF.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimatorF.setDuration(2000);
        mAnimatorF.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorF.start();
    }

//    void drawNike(Canvas canvas) {
//        if (mBitmap == null)
//            mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.nike)).getBitmap();
//        canvas.drawBitmap(mBitmap, width / 2 - mBitmap.getWidth() / 2, height / 2 - mBitmap.getHeight() / 2, mPaint);
//    }

    public void showFinishing() {
        if (mAnimatorFinish != null)
            return;
        ValueAnimator mAnimator = ValueAnimator.ofInt(waveBaseline, height / 2 - r0);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                waveBaseline = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.setDuration(3000);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                waveBaseline = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mAnimator.start();

        mAnimatorFinish = ValueAnimator.ofFloat(0, waveWidth);
        mAnimatorFinish.setInterpolator(new LinearInterpolator());
        mAnimatorFinish.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimatorFinish.setDuration(2000);
        mAnimatorFinish.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorFinish.start();
    }

    public void creatDots() {
        dots.clear();
        for (int i = 0; i < dotSize; i++) {
            Dot dot = new Dot(minLenght + (int) ((maxLenght - minLenght) * Math.random()));
            dots.add(dot);
        }
    }

    public void creatFinishDots() {
        dotsVer.clear();
        dots.clear();
        for (int i = 0; i < verDotSize; i++) {
            Dot dot = new Dot(minLenght);
            dotsVer.add(dot);
        }
        for (int i = 0; i < dotSize - verDotSize; i++) {
            Dot dot = new Dot(minLenght);
            dots.add(dot);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimatorF != null)
            mAnimatorF.cancel();
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    private void performSelect() {
        if (mListner != null) {
                mListner.onSelect();
        }
    }

    public class Dot {
        int lenght;  //弹射最大距离
        public int currentLenght;  //当前弹射最大距离

        public Dot(int lenght) {
            this.lenght = lenght;
        }
    }

    public class FanSpeedTask {
        Handler handler;
        public FanSpeedTask(Handler handler) {
            this.handler = handler;
        }
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public interface OnSetRevListner {
        void onsetRev(int rev);
        void onsetStart();
        void onSelect();
    }
}
