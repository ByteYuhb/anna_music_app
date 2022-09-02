package com.anna.ft_login.view.login.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anna.ft_login.view.login.LoginActivity;
import com.anna.ft_login.view.login.utils.UserManager;
import com.anna.lib_base.module.login.LoginService;

@Route(path = "/login/login_service")
public class LoginServiceImpl implements LoginService {
    Context context;
    @Override
    public boolean hasLogin() {
        return UserManager.getInstance().hasLogined();
    }

    @Override
    public void login(Context context) {
        LoginActivity.start(context);
    }

    @Override
    public void init(Context context) {
        Log.d("TAG","LoginServiceImpl is init");
    }
}
