package com.yunmi.fan.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.yunmi.fan.R;
import com.yunmi.fan.utils.UMUtils;
import com.yunmi.fan.utils.log;

/**
 * 数值选择器 - 定时时长
 * Created by liguobin on 2018/9/20.
 */

public class TimerPickerView extends View {
    public static final String TAG = TimerPickerView.class.getSimpleName();
    /**
     * text之间间距和minTextSize之比
     * text之间间距 / minTextSize = 2.8f, 即text之间间距 = 39.2
     */
    public static final float MARGIN_ALPHA = 2.8f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 2;

    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint;
    private Paint mPaintUint;
    private float mMaxTextSize = 26;
    private float mMinTextSize = 10;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 20;

    private int mColorText = 0x333333;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;
    private Context context;

    public TimerPickerView(Context context) {
        this(context, null);
        this.context = context;
    }

    public TimerPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public TimerPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else{
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            }
            invalidate();
        }
    };

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null) {
            if (!mDataList.get(mCurrentSelected).isEmpty()) {
                mSelectListener.onSelect(mDataList.get(mCurrentSelected));
            }
        }
    }

    public void setData() {
        mDataList = new ArrayList<String>();
        mDataList.add("30分钟");
        mDataList.add("1小时00分");
        mDataList.add("1小时30分");
        mDataList.add("2小时00分");
        mDataList.add("2小时30分");
        mDataList.add("3小时00分");
        mDataList.add("3小时30分");
        mDataList.add("4小时00分");
        mDataList.add("4小时30分");
        mDataList.add("5小时00分");
        mDataList.add("5小时30分");
        mDataList.add("6小时00分");
//        mCurrentSelected = mDataList.size() / 2;
        invalidate();
    }

    /**
     * 选择选中的item 的index
     *
     * @param selected
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        Log.e("@@@@@", "setSelected-mCurrentSelected: " + mCurrentSelected);
        int distance = mDataList.size() / 2 - mCurrentSelected;
        Log.e("@@@@@", "setSelected-distance: " + distance);
        if (distance < 0)
            for (int i = 0; i < -distance; i++) {
                moveHeadToTail();
                mCurrentSelected--;
            }
        else if (distance > 0)
            for (int i = 0; i < distance; i++) {
                moveTailToHead();
                mCurrentSelected++;
            }
        invalidate();
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++) {
//            Log.e("@@@@@", "mDataList.size(): " + mDataList.size());
            if (mDataList.get(i).equals(mSelectItem)) {
//                Log.e("@@@@@", "mDataList.get(i): " + mDataList.get(i));
                setSelected(i);
                break;
            }
        }
    }

    /**
     * 读取选中的内容
     *
     * @param
     */
    public String getSelected() {
        return mDataList.get(mCurrentSelected);
    }

    private void moveHeadToTail() {
        if (!mDataList.isEmpty()) {
            String head = mDataList.get(0);
            Log.e("@@@@@", "moveHeadToTail-head: " + head);
            mDataList.remove(0);
            Log.e("@@@@@", "moveHeadToTail-mDataList: " + mDataList);
            mDataList.add(head);
            Log.e("@@@@@", "moveHeadToTail-mDataList: " + mDataList);
        }
    }

    private void moveTailToHead() {
        if (! mDataList.isEmpty()) {
            String tail = mDataList.get(mDataList.size() - 1);
            Log.e("@@@@@", "moveTailToHead-tail: " + tail);
            mDataList.remove(mDataList.size() - 1);
            Log.e("@@@@@", "moveTailToHead-mDataList: " + mDataList);
            mDataList.add(0, tail);
            Log.e("@@@@@", "moveTailToHead-mDataList: " + mDataList);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        Log.e("@@@@@", "onMeasure-mViewHeight: " + mViewHeight);
        mViewWidth = getMeasuredWidth();
        Log.e("@@@@@", "onMeasure-mViewWidth: " + mViewWidth);
        // 按照View的高度计算字体大小
        mMaxTextSize = 50;
//        mMaxTextSize = mViewHeight / 6.0f;
        Log.e("@@@@@", "onMeasure-mMaxTextSize: " + mMaxTextSize);
        mMinTextSize = 50;
//        mMinTextSize = mMaxTextSize / 2f;
        Log.e("@@@@@", "onMeasure-mMinTextSize: " + mMinTextSize);
        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<String>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        //mColorText=context.getResources().getColor(R.color.hight_light_color);
        mPaint.setColor(mColorText);

        mPaintUint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintUint.setStyle(Style.FILL);
        mPaintUint.setTextAlign(Align.CENTER);
        mPaintUint.setTextSize(UMUtils.dp2px(6, context));
        //mPaintUint.setColor(context.getResources().getColor(R.color.mediumturquoise));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit){
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setColor(mColorText);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);
        float textWidth = mPaint.measureText(mDataList.get(mCurrentSelected));
        canvas.drawText(context.getResources().getString(R.string.text_hour_uint), x + textWidth / 2 + 6, baseline - mPaintUint.getTextSize()*2, mPaintUint);

        //mPaint.setColor(context.getResources().getColor(R.color.black));
        // 绘制上方data
        for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param position
     *            距离mCurrentSelected的差值
     * @param type
     *            1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen);
        float sizeScale = parabola(mViewHeight /8.0f, d);
        float alphaScale = getLineScale(mViewHeight /2.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * sizeScale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * alphaScale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelected + type * position),
                (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x 偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    /**
     * @param height 最大距离
     * @param x
     * @return
     */
    private float getLineScale(float height, float x){
        if(height==0){
            return 0;
        }
        return (1-x / height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {
        mMoveLen += (event.getY() - mLastDownY);
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
        }
        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    private void doAgain(MotionEvent event) {
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;
        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public interface onSelectListener {
        void onSelect(String text);
    }
}

