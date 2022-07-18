package com.anna.lib_keepalive.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

/**
 *
 */
public class Utils {
    /**判断是否在电池优化的白名单中
     * @param context
     * @return
     */
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnore = false;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isIgnore = pm.isIgnoringBatteryOptimizations(context.getPackageName());
            }
        }
        return isIgnore;

    }

    /**将应用添加到电池优化白名单中
     * @param context
     */
    public static void requestIgnoreBatteryOptimizations(Context context) {
        if(isIgnoringBatteryOptimizations(context)){
            return;
        }
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
