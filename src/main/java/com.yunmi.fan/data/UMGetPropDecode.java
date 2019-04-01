package com.yunmi.fan.data;

import com.yunmi.fan.utils.UMUtils;
import com.yunmi.fan.utils.log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 解析GetProp Json
 * Created by LiGuobin on 2018/8/16.
 */

public class UMGetPropDecode {
    private static final String TAG = UMGetPropDecode.class.getSimpleName();

    public static UMDeviceInfo decode(JSONObject jsonObject) {
        UMDeviceInfo info = new UMDeviceInfo();
        if (jsonObject == null) return null;

        //非正常返回
        int code = jsonObject.optInt("code");
        if(code != 0) return null;

        try {
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if (jsonArray.length() <= 0) return null;

            //开始解析
            int i = 0;
            info.hw_info = jsonArray.optString(i);
            i++;
            info.sw_info = jsonArray.optString(i);
            i++;
            info.wind_speed = isError(i, jsonArray);
            i++;
            info.wind_mode = isError(i, jsonArray);
            i++;
            info.temperature = isError(i, jsonArray);
            i++;
            info.power_state = isError(i, jsonArray);
            i++;
            info.shake_state = isError(i, jsonArray);
            i++;
            info.voice_state = isError(i, jsonArray);
            i++;
            info.led_state = isError(i, jsonArray);
            i++;
            info.work_time = isError(i, jsonArray);
            i++;
            info.volume = isError(i, jsonArray);
            i++;
            info.playmode = jsonArray.optBoolean(i);
            if (info.run_status != -2 && info.run_status != -1) {
                info.statusByte = UMUtils.longToByte(info.run_status);
                info.statusCount = 0;
                for (int j = 21; j < 25; j++) {
                    if (info.statusByte[j] == 1) {
                        info.statusCount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.e(TAG, e.toString());
            return null;
        }
        return info;
    }

    private static int isError(int i, JSONArray jsonArray) {
        if (i >= jsonArray.length()) return -2;
        else if (jsonArray.optString(i).equals("error")) return -1;
        else return jsonArray.optInt(i);
    }
}
