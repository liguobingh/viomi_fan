package com.yunmi.fan.data;

/**
 * 风扇的属性
 * Created by LiGuobin on 2018/8/15.
 */

public class UMDeviceInfo {
    public String hw_info;   //string  "xx.xx.xx"  Major ver.Minor ver.Bug ver，仅作查询使用
    public String sw_info;   //string  "xx.xx.xx"  Major ver.Minor ver.Bug ver，仅作查询使用
    public int wind_speed;   //UINT8  [1, 206]  当前设定的风速
    public int wind_mode;   //UINT8  [0, 2]  当前工作模式（0-标准风；1-自然风；2-智能风）
    public int temperature;   //INT8  [-20, 50]  当前室内温度
    public int power_state;   //BOOL  [0, 1]  设备工作状态（0-设备关闭；1-设备开启）
    public int shake_state;   //BOOL  [0, 1]  摇头功能开启标识（0-关闭摇头功能；1-开启摇头功能）
    public int voice_state;   //BOOL  [0, 1]  语音功能开启标识（0-关闭语音识别功能；1-开启语音识别功能）
    public int led_state;   //BOOL  [0, 1]  LED持续显示功能开启标识（0-关闭led持续显示功能；1-开启led持续显示功能）
    public int work_time;   //UINT16  [0, 360]  定时剩余时间（单位/min）（65535，全部置1表示当前无定时）
    public int volume;   //UINT8  [0, 100]  当前语音音量百分比
    public boolean playmode;   //BOOL  [0, 1]  播报模式开启标识（0-关闭正常播报，开启简洁模式；1-开启正常播报，关闭简洁模式）

    public byte[] statusByte;             // 异常状态
    public int statusCount = 0; // 异常统计
    public int run_status = 0;

    //！！！没有“超时提示”这个属性

    @Override
    public String toString() {
        return "UMDeviceInfo {" +
                "hw_info:" + hw_info + "," +
                "sw_info" + sw_info + "," +
                "wind_speed" + wind_speed + "," +
                "wind_mode" + wind_mode + "," +
                "temperature" + temperature + "," +
                "power_state" + power_state + "," +
                "shake_state" + shake_state + "," +
                "voice_state" + voice_state + "," +
                "led_state" + led_state + "," +
                "work_time" + work_time + "," +
                "volume" + volume + "," +
                "playmode" + playmode;
    }
}
