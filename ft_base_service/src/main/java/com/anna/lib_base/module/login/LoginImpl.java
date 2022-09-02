package com.anna.lib_base.module.login;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;

public class LoginImpl {

    @Autowired(name = "/login/login_service")
    public LoginService mLoginService;
    private static LoginImpl mLoginImpl = null;
    public static LoginImpl getInstance() {
        if (mLoginImpl == null) {
            synchronized (LoginImpl.class) {
                if (mLoginImpl == null) {
                    mLoginImpl = new LoginImpl();
                }
                return mLoginImpl;
            }
        }
        return mLoginImpl;
    }

    private LoginImpl(){
        ARouter.getInstance().inject(this);
    }
    public boolean hasLogin(){
        return mLoginService.hasLogin();
    }
    public void login(Context context){
        mLoginService.login(context);
    }

}
