package com.anna.lib_base.module.home;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;


public class HomeImpl {

    @Autowired(name = "/home/home_service")
    public HomeService mHomeService;
    private static HomeImpl mLoginImpl = null;
    public static HomeImpl getInstance() {
        if (mLoginImpl == null) {
            synchronized (HomeImpl.class) {
                if (mLoginImpl == null) {
                    mLoginImpl = new HomeImpl();
                }
                return mLoginImpl;
            }
        }
        return mLoginImpl;
    }

    private HomeImpl(){
        ARouter.getInstance().inject(this);
    }

    public void startHomeActivity(Context context){
        mHomeService.startHomeActivity(context);
    }

}
