package com.anna.lib_update.app;

import android.content.Context;

import com.anna.lib.network.CommonOkHttpClient;
import com.anna.lib.network.listener.DisposeDataHandle;
import com.anna.lib.network.listener.DisposeDataListener;
import com.anna.lib.network.request.CommonRequest;
import com.anna.lib_common_ui.CommonDialog;
import com.anna.lib_update.R;
import com.anna.lib_update.update.UpdateService;
import com.anna.lib_update.update.constant.Constants;
import com.anna.lib_update.update.model.UpdateModel;
import com.anna.lib_update.update.utils.Utils;

/**
 * 提供对app的接口调用
 */
public class UpdateHelper {
    public static String UPDATE_ACTION;
    public static final String UPDATE_FILE_KEY = "apk";

    public static void init(Context context){
        UPDATE_ACTION = context.getPackageName() + ".INSTALL";
    }
    /**检查当前是否需要进行更新
     * @param context
     */
    public static void checkUpdate(Context context){
        //1.调用network去检测，传入一个url和Listener
        DisposeDataHandle handle = new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                UpdateModel info = (UpdateModel)responseObj;
                //获取成功：比较versionCode
                if(Utils.getVersionCode(context) < info.data.currentVersion){
                    //进入下载流程
                    CommonDialog dialog = new CommonDialog(context,
                            context.getString(R.string.update_new_version),
                            context.getString(R.string.update_title),
                            context.getString(R.string.update_install),
                            context.getString(R.string.cancel),
                            () -> UpdateService.start(context));
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                //Toast一个错误出去，或者不处理
            }
        },UpdateModel.class);
        CommonOkHttpClient.get(Constants.CHECK_UPDATE,null,handle);
    }

}
