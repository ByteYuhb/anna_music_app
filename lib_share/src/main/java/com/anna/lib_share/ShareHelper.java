package com.anna.lib_share;

import com.mob.MobSDK;

public class ShareHelper {
    /**
     * 统一隐私政策
     */
    public static void submitPolicy(){
        MobSDK.submitPolicyGrantResult(true);
    }
}
