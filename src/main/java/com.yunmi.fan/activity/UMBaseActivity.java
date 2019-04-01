package com.yunmi.fan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;
import com.yunmi.fan.R;


public class UMBaseActivity extends XmPluginBaseActivity {
    protected int layoutId = R.layout.activity_main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        View titleBar = findViewById(R.id.title_bar);
        if (titleBar != null)
            mHostActivity.setTitleBarPadding(titleBar); // 设置 titleBar 在顶部透明显示时的顶部 padding
        Log.d("mimi","AAAAAAAAAAAAAA");
    }
}
