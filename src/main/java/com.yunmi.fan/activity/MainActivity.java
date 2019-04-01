package com.yunmi.fan.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.BaseDevice;
import com.xiaomi.smarthome.device.api.BaseDevice.StateChangedListener;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceUpdateInfo;

import com.yunmi.fan.R;
import com.yunmi.fan.callback.RequestCallback;
import com.yunmi.fan.data.UMDeviceInfo;
import com.yunmi.fan.device.UMFanDevice;
import com.yunmi.fan.utils.log;
import com.yunmi.fan.view.PowerView;
import com.yunmi.fan.view.ShakeView;
import com.yunmi.fan.view.TimerPickerView;
import com.yunmi.fan.view.UMFanSpeedView;
import com.yunmi.fan.view.WindAIView;
import com.yunmi.fan.view.WindNatureView;
import com.yunmi.fan.view.WindStandardView;
import com.yunmi.fan.view.WorkTimeView;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

@SuppressLint("Registered")
public class MainActivity extends UMBaseActivity implements StateChangedListener, View.OnClickListener, View.OnTouchListener, TimerPickerView.onSelectListener {
    View mNewFirmView;

    private static final String TAG = MainActivity.class.getSimpleName();
    private RequestCallback<DeviceUpdateInfo> mCheckUpdateCallback;
    private RequestCallback<JSONObject> mSetPropertyCallback;
    private UMFanDevice mDevice;
    //private SeekBar mSeekBar;
    private UMDeviceInfo info, temp1;

    private ImageView titleBarReturn;   //标题栏返回按钮
    private TextView titleBarTitle;   //标题栏标题文本
    private ImageView titleBarMore;   //标题栏更多按钮

    private TextView roomTempText;   //室内温度显示框
    private TextView displayFrame; //温度下方标识：显示开关机状态、摇头状态、风扇模式、定时情况；根据温度显示室内情况（长期）

    private LinearLayout begin;  //初始开关布局
    private ImageView powerOn;  //打开风扇按钮
    private PowerView powerOnView;   //打开风扇视图
    private ImageView powerOff; //关闭风扇按钮
    private PowerView powerOffView;   //关闭风扇视图

    private LinearLayout manyButtons;

    private ImageView shakeOn;   //摇头开启
    private ShakeView shakeView;  //摇头视图
    private ImageView shakeOff;  //摇头关闭

    private ImageView windStandard;  //标准风
    private WindStandardView windStandardView; //标准风视图
    private ImageView windNature;  //自然风
    private WindNatureView windNatureView; //自然风视图
    private ImageView windAI;  //智能风
    private WindAIView windAIView; //智能风视图
    private TextView windMode;//下方提示框
    private UMFanSpeedView fanSpeed;

    private ImageView timeOn;   //开启定时按钮
    private WorkTimeView timeView;  //定时视图
    private ImageView timeOff;  //关闭定时按钮
    private String temp;
    //private TimerDialog timerDialog;  //定时器-对话框视图
    private TimerPickerView timerPickerView;  //选择定时的视图.Dialog 的布局View
    private TextView timingInfo;   //定时时长提示信息
    private View timerView;
    private MLAlertDialog timerDialog;
    private TextView timerText;

    private RelativeLayout mainLayout;

    private Dialog timer;

    //private MainActivity.TimePickerDialogInterface timePickerDialogInterface;

    private LinearLayout powerAfter;   //下方四个按钮的布局文件

    private TextView tvError;

    private ImageView mRedPointView;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int errorCount = 0, count = 0;// 异常数量, 获取失败计数
    private boolean isSetting = false;// 是否正在设置
    private CharSequence[] error;
    private LocalBroadcastManager mLocalBroadcastManager;
    //private ValueAnimator mValueAnimator;

    private static final int MSG_UPDATE_FIRM = 1;// 检查更新成功
    private static final int SET_POWER_ON = 2; // 开状态
    private static final int SET_POWER_OFF = 3; //关状态
    private static final int SHAKE_STATE_OK = 4;// 摇头状态
    private static final int WIND_SPEED = 5; // 风速大小
    private static final int FAN_PATTERN = 6; // 风扇模式
    private static final int SET_TIMER = 7; //定时
    private static final int MSG_DEVICE_GET_PROP = 8;// GetProp请求
    //private static final int MSG_MOVE_BUBBLE = 3;// SeekBar 移动

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.activity_main;
        super.onCreate(savedInstanceState);
        initView();
        initCallback();
    }

    private void initView() {
        //timerDialog = new TimerDialog(activity());
        mRedPointView = (ImageView) findViewById(R.id.title_bar_red_point);
        titleBarReturn = (ImageView) findViewById(R.id.title_bar_return);   //标题栏返回按钮
        titleBarTitle = ((TextView) findViewById(R.id.title_bar_title));   //标题栏标题内容
        titleBarMore = (ImageView) findViewById(R.id.title_bar_more);   //标题栏更多按钮
        roomTempText = (TextView) findViewById(R.id.room_temp_text);   //室内温度显示文本
        displayFrame = (TextView) findViewById(R.id.diaplay_frame);   //温度下方的模式显示框架

        begin = (LinearLayout) findViewById(R.id.begin);
        powerOn = (ImageView) findViewById(R.id.power_on);
        powerOnView = (PowerView) findViewById(R.id.power_on_view);
        powerOff = (ImageView) findViewById(R.id.power_off);
        powerOffView = (PowerView) findViewById(R.id.power_off_view);

        manyButtons = (LinearLayout) findViewById(R.id.many_buttons);

        shakeOn = (ImageView) findViewById(R.id.shake_on);
        shakeView = (ShakeView) findViewById(R.id.shake_view);
        shakeOff = (ImageView) findViewById(R.id.shake_off);

        windStandard = (ImageView) findViewById(R.id.wind_standard);
        windStandardView = (WindStandardView) findViewById(R.id.wind_standard_view);
        windNature = (ImageView) findViewById(R.id.wind_nature);
        windNatureView = (WindNatureView) findViewById(R.id.wind_nature_view);
        windAI = (ImageView) findViewById(R.id.wind_ai);
        windAIView = (WindAIView) findViewById(R.id.wind_ai_view);
        windMode = (TextView) findViewById(R.id.wind_mode);
        fanSpeed = (UMFanSpeedView) findViewById(R.id.fan_speed);
        fanSpeed.setTouchable(true);

        timeOn = (ImageView) findViewById(R.id.time_on);
        timeView = (WorkTimeView) findViewById(R.id.time_view);
        timeOff = (ImageView) findViewById(R.id.time_off);
        timingInfo = (TextView) findViewById(R.id.timing_info);
        timerText = (TextView) findViewById(R.id.timer_text);

        powerAfter = (LinearLayout) findViewById(R.id.power_after);   //下方四个按钮的布局
        tvError = (TextView) findViewById(R.id.error_status);
        mDevice = UMFanDevice.getDevice(mDeviceStat);   // 初始化device

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        titleBarReturn.setOnClickListener(this);
        titleBarMore.setOnClickListener(this);
        powerOn.setOnClickListener(this);
        powerOff.setOnClickListener(this);
        shakeOn.setOnClickListener(this);
        shakeOff.setOnClickListener(this);
        windStandard.setOnClickListener(this);
        windNature.setOnClickListener(this);
        windAI.setOnClickListener(this);
        timeOn.setOnClickListener(this);
        timeOff.setOnClickListener(this);
        powerAfter.setOnClickListener(this);
    }

    private void initCallback() {
        // 检查更新
        mCheckUpdateCallback = new RequestCallback<DeviceUpdateInfo>() {
            @Override
            public void onSuccess(DeviceUpdateInfo data) {
                if (mHandler != null)
                    Message.obtain(mHandler, MSG_UPDATE_FIRM, data).sendToTarget();
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                log.e(TAG, "checkDeviceUpdateInfo fail,code=" + errorCode + ",msg=" + errorInfo);
            }
        };

        //设置开状态
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, SET_POWER_ON).sendToTarget();
                log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Set property fail, code = " + errorCode + ",msg = " + errorInfo);
            }
        };

        //设置关状态
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, SET_POWER_OFF).sendToTarget();
                log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Set property fail, code = " + errorCode + ",msg = " + errorInfo);
            }
        };

        //设置摇头状态
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, SHAKE_STATE_OK).sendToTarget();
                Log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Set property fail, code = " + errorCode + ",msg = " + errorInfo);
            }
        };

        // 设置风速
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, WIND_SPEED).sendToTarget();
                log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                log.e(TAG, "set property fail,code=" + errorCode + ",msg=" + errorInfo);
            }
        };

        // 设置风扇模式
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, FAN_PATTERN).sendToTarget();
                log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                log.e(TAG, "set property fail,code=" + errorCode + ",msg=" + errorInfo);
            }
        };

        // 设置定时
        mSetPropertyCallback = new RequestCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject data) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_success), Toast.LENGTH_SHORT).show();
                if (mHandler != null) Message.obtain(mHandler, SET_TIMER).sendToTarget();
                log.d(TAG, data.toString());
            }

            @Override
            public void onFailure(int errorCode, String errorInfo) {
                isSetting = false;
                Toast.makeText(activity(), activity().getResources().getString(R.string.um_setting_fail), Toast.LENGTH_SHORT).show();
                log.e(TAG, "set property fail,code=" + errorCode + ",msg=" + errorInfo);
            }
        };
    }

    private void refreshUI() {
        titleBarTitle.setText(mDevice.getName());
        if (mDevice == null) {
            return;
        }

        // 设备离线
        if (mDevice.getErrorInfo() != null && mDevice.getErrorInfo().equals("device offline")) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setEnabled(false);
            tvError.setText(activity().getResources().getString(R.string.um_device_offline));
            return;
        }
        // 获取数据失败，请检查网络连接
        else if (mDevice.getErrorInfo() != null) {
            if (count >= 4) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setEnabled(false);
                tvError.setText(activity().getResources().getString(R.string.um_device_network_unavailable));
            } else {
                count++;
            }
            return;
        }

        count = 0;   // 复位
        //室内温度显示
        roomTempText.setText("" + info.temperature);

        //判断开关状态
        if (info.power_state == 0) {
            displayFrame.setText(activity().getResources().getString(R.string.power_off));  //设置显示框
            begin.setVisibility(View.VISIBLE);
            powerAfter.setVisibility(View.GONE);  //设置下方四个按钮的可视化
            powerOnView.setVisibility(View.GONE);
            powerOnView.stop();
            powerOn.setVisibility(View.VISIBLE);
        } else if (info.power_state == 1) {
            displayFrame.setText(activity().getResources().getString(R.string.power_on));
            begin.setVisibility(View.GONE);
            powerAfter.setVisibility(View.VISIBLE);
            powerOffView.setVisibility(View.GONE);
            powerOffView.stop();
            powerOff.setVisibility(View.VISIBLE);
            if (info.temperature > -20 && info.temperature <= 10) {
                displayFrame.setText(R.string.room_clod);
            } else if (info.temperature > 10 && info.temperature <= 20) {
                displayFrame.setText(R.string.room_comfortable);
            } else if (info.temperature > 20 && info.temperature <= 30) {
                displayFrame.setText(R.string.room_warm);
            } else if (info.temperature > 30 && info.temperature <= 50) {
                displayFrame.setText(R.string.room_hot);
            } else {
                return;
            }
        }
        //判断摇头状态
        if (info.shake_state == 1) {
            shakeOn.setVisibility(View.GONE);
            shakeView.setVisibility(View.GONE);
            shakeView.stop();
            shakeOff.setVisibility(View.VISIBLE);
        } else if (info.shake_state == 0) {
            shakeOff.setVisibility(View.GONE);
            shakeView.setVisibility(View.GONE);
            shakeView.stop();
            shakeOn.setVisibility(View.VISIBLE);
        }
        //判断风扇模式
        switch (info.wind_mode) {
            case 0:  // 标准风模式
                fanSpeed.setVisibility(View.VISIBLE);
                windMode.setText(R.string.standard_wind);
                windNature.setVisibility(View.GONE);
                windAI.setVisibility(View.GONE);
                windAIView.setVisibility(View.GONE);
                windAIView.stop();
                windStandard.setVisibility(View.VISIBLE);
                break;
            case 1:  // 自然风模式
                fanSpeed.setVisibility(View.VISIBLE);
                windMode.setText(R.string.natural_wind);
                windStandard.setVisibility(View.GONE);
                windAI.setVisibility(View.GONE);
                windStandardView.setVisibility(View.GONE);
                windStandardView.stop();
                windNature.setVisibility(View.VISIBLE);
                break;
            case 2:  // AI智能风模式
                fanSpeed.setVisibility(View.GONE);
                windMode.setText(R.string.smart_wind);
                windStandard.setVisibility(View.GONE);
                windNature.setVisibility(View.GONE);
                windNatureView.setVisibility(View.GONE);
                windNatureView.stop();
                windAI.setVisibility(View.VISIBLE);
                break;
        }
        //判断定时情况
        if (info.work_time == 65535) {
            timerText.setText("定时");
        } else {
            timerText.setText("定时");
            timingInfo.setVisibility(View.VISIBLE);
            MainActivity.this.timingInfo.setText("风扇将在  0" + info.work_time / 60 + "小时" + info.work_time % 60 + "分钟  后关闭");
        }
    }

    private void startTimer() {
        stopTimer();    // 先停止Timer
        int period = 3 * 1000;  // 轮询周期
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mDevice != null) {
                    if (!mDevice.isUpdateRunning) {
                        if (mHandler != null) {
                            Message.obtain(mHandler, MSG_DEVICE_GET_PROP).sendToTarget();
                        }
                    }
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, period);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    //设置开关状态
    private void setPower() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.power_state == 0) {
                params[0] = 1;
            } else if (info.power_state == 1) {
                params[0] = 0;
            }
            mDevice.setProperty("set_power", params, mSetPropertyCallback);
        }
    }

    //设置摇头状态
    private void setShake() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.shake_state == 0) {
                params[0] = 1;
            } else if (info.shake_state == 1) {
                params[0] = 0;
            }
            mDevice.setProperty("set_shake", params, mSetPropertyCallback);
        }
    }

    //设置工作模式
    private void setWindMode() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.wind_mode == 0) {
                params[0] = 1;
            } else if (info.wind_mode == 1) {
                params[0] = 2;
            } else if (info.wind_mode == 2) {
                params[0] = 0;
            }
            mDevice.setProperty("set_windmode", params, mSetPropertyCallback);
        }
    }

    //设置风速 分206个等级，1最低转速，206最高转速
    private void setWindSpeed() {
        if (mDevice != null) {
            fanSpeed.setmListner(new UMFanSpeedView.OnSetRevListner() {
                @Override
                public void onsetRev(int rev) {
                }

                @Override
                public void onsetStart() {
                }

                @Override
                public void onSelect() {
                    Object[] params = new Object[1];
                    params[0] = fanSpeed.getPercent() * 206;
                    mDevice.setProperty("set_windspeed", params, mSetPropertyCallback);
                }
            });
        }
    }

    //设置定时时间（单位/min）
    private void setWorkTime(String temp) {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (temp != null) {
                //timerPickerView.getSelected().toString()
                switch (temp) {
                    case "30分钟":
                        params[0] = 30;
                        break;
                    case "1小时00分":
                        params[0] = 60;
                        break;
                    case "1小时30分":
                        params[0] = 90;
                        break;
                    case "2小时00分":
                        params[0] = 120;
                        break;
                    case "2小时30分":
                        params[0] = 150;
                        break;
                    case "3小时00分":
                        params[0] = 180;
                        break;
                    case "3小时30分":
                        params[0] = 210;
                        break;
                    case "4小时00分":
                        params[0] = 240;
                        break;
                    case "4小时30分":
                        params[0] = 270;
                        break;
                    case "5小时00分":
                        params[0] = 300;
                        break;
                    case "5小时30分":
                        params[0] = 330;
                        break;
                    case "6小时00分":
                        params[0] = 360;
                        break;
                    case "不定时了":
                        params[0] = 65535;
                        break;
                }
                mDevice.setProperty("set_worktime", params, mSetPropertyCallback);
            } else {
            }
        }
    }

    //设置LED灯开关
    private void setLed() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.led_state == 0) {
                params[0] = 1;
            } else if (info.led_state == 1) {
                params[0] = 0;
            }
            mDevice.setProperty("set_led", params, mSetPropertyCallback);
        }
    }

    //设置语音识别开关
    private void setVoice() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.voice_state == 0) {
                params[0] = 1;
            } else if (info.voice_state == 1) {
                params[0] = 0;
            }
            mDevice.setProperty("set_voice", params, mSetPropertyCallback);
        }
    }

    //设置语音音量大小
    private void setVolume() {
    }

    //设置播报模式
    private void setPlayMode() {
        if (mDevice != null) {
            UMDeviceInfo info = mDevice.getDeviceInfo();
            Object[] params = new Object[1];
            if (info.playmode == false) {
                params[0] = 1;
            } else if (info.playmode == true) {
                params[0] = 0;
            }
            mDevice.setProperty("set_playmode", params, mSetPropertyCallback);
        }
    }

    //设置风量大小
    private void setWindVolume() {
    }


    /**
     * 创建dialog
     */
    private void showDialog() {
        if (timerDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.timer_picker_dialog, null);
            timerDialog = new MLAlertDialog.Builder(activity()).setView(view).show();
            Button cancelButton = (Button) view.findViewById(R.id.cancel_btn);
            Button certainButton = (Button) view.findViewById(R.id.certain_btn);
            timerPickerView = (TimerPickerView) view.findViewById(R.id.timer_picker);
            timerPickerView.setLeft(40);
            timerPickerView.setRight(40);
            timerPickerView.setData();
            timerDialog.setCancelable(true);   //设置按钮是否可以按返回键取消,false则不可以取消
            timerDialog.setCanceledOnTouchOutside(true);  //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timerDialog != null) {
                        timerDialog.dismiss();
                        timerDialog = null;
                        //timerText.setText("取消定时");
                    }
                }
            });
            certainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timerDialog != null) {
                        timerDialog.dismiss();
                        timerDialog = null;
                    }
                    timeOn.setVisibility(View.GONE);
                    timeView.setVisibility(View.VISIBLE);
                    timeView.start();
                }
            });
            timerPickerView.setOnSelectListener(new TimerPickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    temp = text;
//                    if (timeView.isState()) {
//                        setWorkTime(temp);
//                    }
//                    timeView.setVisibility(View.GONE);
//                    timeView.stop();
//                    timeOff.setVisibility(View.VISIBLE);
                }
            });
            timerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
        } else {
            //  timerDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_bar_return:
                activity().finish();
                break;
            case R.id.title_bar_more:
                intent = new Intent();
                intent.putExtra("scence_enable", false);
                mHostActivity.openMoreMenu2(null, true, 1, intent);
                break;

            case R.id.power_on:
                powerOn.setVisibility(View.GONE);
                powerOnView.setVisibility(View.VISIBLE);
                powerOnView.startPowerView();
                setPower();
                break;
            case R.id.power_off:
                powerOff.setVisibility(View.GONE);
                powerOffView.setVisibility(View.VISIBLE);
                powerOffView.startPowerView();
                setPower();
                break;

            case R.id.shake_on:
                shakeOn.setVisibility(View.GONE);
                shakeView.setVisibility(View.VISIBLE);
                shakeView.startShakeView();
                setShake();
                break;
            case R.id.shake_off:
                shakeOff.setVisibility(View.GONE);
                shakeView.setVisibility(View.VISIBLE);
                shakeView.startShakeView();
                setShake();
                break;

            case R.id.wind_standard:
                windMode.setText(R.string.natural_wind);
                windStandard.setVisibility(View.GONE);
                windStandardView.setVisibility(View.VISIBLE);
                windStandardView.start();
                setWindMode();
                displayFrame.setText(R.string.um_natural_wind);
                break;
            case R.id.wind_nature:
                windMode.setText(R.string.smart_wind);
                windNature.setVisibility(View.GONE);
                windNatureView.setVisibility(View.VISIBLE);
                windNatureView.start();
                setWindMode();
                displayFrame.setText(R.string.um_smart_wind);
                break;
            case R.id.wind_ai:
                windMode.setText(R.string.standard_wind);
                windAI.setVisibility(View.GONE);
                windAIView.setVisibility(View.VISIBLE);
                windAIView.start();
                setWindMode();
                displayFrame.setText(R.string.um_standard_wind);
                break;

            case R.id.time_on:
                //addTimerPickerView();

                showDialog();
                //timerDialog.initTimer();
                //showTimePickerDialog();
                //showDatePickDlg();
                break;
            case R.id.time_off:
                timeOn.setVisibility(View.GONE);
                timeView.setVisibility(View.VISIBLE);
                timeView.start();
                timerText.setText("取消定时");
                setWorkTime("不定时了");
                timeView.setVisibility(View.GONE);
                timeView.stop();
                timeOff.setVisibility(View.VISIBLE);
                timerText.setText("定时");
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (roomTempText.getVisibility() != View.VISIBLE)   //。。。public static final int VISIBLE = 0;
            roomTempText.setVisibility(View.VISIBLE);

        switch (msg.what) {
            case MSG_UPDATE_FIRM:   // 刷新固件升级状态
                DeviceUpdateInfo updateInfo = (DeviceUpdateInfo) msg.obj;
                if (mDevice.isOwner()) {
                    log.d(TAG, "update_status:" + updateInfo.mHasNewFirmware);
                    if (updateInfo.mHasNewFirmware) {
                        mRedPointView.setVisibility(View.VISIBLE);
                    } else {
                        mRedPointView.setVisibility(View.GONE);
                    }
                }
                break;
            case MSG_DEVICE_GET_PROP: // getProp：获取属性
                if (mDevice != null) {
                    mDevice.updateProperty(new String[]{"hw_info", "sw_info", "wind_speed",
                            "wind_mode", "temperature", "power_state", "shake_state", "voice_state", "led_state",
                            "work_time", "volume", "playmode"});
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStateChanged(BaseDevice device) {
        if (temp1 == null || temp1 == mDevice.getDeviceInfo()) {
            temp1 = mDevice.getDeviceInfo();
            log.e("@@@@@", "onStateChanged-info:" + temp1);
        } else if (temp1 != null && temp1 != mDevice.getDeviceInfo()) {
            info = mDevice.getDeviceInfo();
            log.e("@@@@@", "onStateChanged-information:" + info);
            refreshUI();
            log.e("@@@@@", "onStateChanged-refreshUI():");
        }
    }

/*    private void startTempAnim(int setup_tempe) {
        setup_tempe = setup_tempe > 100 ? 100 : setup_tempe;
        if (mValueAnimator != null && mValueAnimator.isRunning())
            mValueAnimator.cancel();
        mValueAnimator = ValueAnimator.ofInt(startTemp, setup_tempe);
        mValueAnimator.setDuration(2000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startTemp = (int) valueAnimator.getAnimatedValue();
//                if (startTemp == 100) mTempTextView.setTextSize(60);
//                else mTempTextView.setTextSize(75);
                String temp = startTemp >= 0 && startTemp < 10 ? "0" + startTemp : String.valueOf(startTemp);
                mTempTextView.setText(temp.equals("00") ? "--" : temp);
            }
        });
        mValueAnimator.start();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (mDevice == null) mDevice = UMFanDevice.getDevice(mDeviceStat);// 初始化device
        mDevice.checkUpdateInfo(mCheckUpdateCallback);
        mDevice.updateDeviceStatus();
        mDevice.addStateChangedListener(this);// 监听设备数据变化
        ((TextView) findViewById(R.id.title_bar_title)).setText(mDevice.getName());
        startTimer();

        // 检测是否有固件更新
        mDevice.checkDeviceUpdateInfo(new Callback<DeviceUpdateInfo>() {
            @Override
            public void onSuccess(DeviceUpdateInfo updateInfo) {
                Message.obtain(mHandler, MSG_UPDATE_FIRM, updateInfo).sendToTarget();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // 取消监听
        if (mDevice != null)
            mDevice.removeStateChangedListener(this);
        stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mCheckUpdateCallback = null;
        mSetPropertyCallback = null;
        mLocalBroadcastManager = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onSelect(String text) {

    }

 /*   @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mHandler != null)
            Message.obtain(mHandler, MSG_MOVE_BUBBLE).sendToTarget();
    }*/

/*    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}*/

/*    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                v.performClick();
                isSetting = true;
//                if (tvTempSetting.getVisibility() != View.VISIBLE)
//                    tvTempSetting.setVisibility(View.VISIBLE);
            case MotionEvent.ACTION_UP:
                if (mHandler != null)
                    Message.obtain(mHandler, MSG_MOVE_BUBBLE).sendToTarget();
                return false;
            case MotionEvent.ACTION_CANCEL:
//                if (tvTempSetting.getVisibility() == View.VISIBLE)
//                    tvTempSetting.setVisibility(View.INVISIBLE);
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }*/
}
