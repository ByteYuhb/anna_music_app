package com.anna.lib_base.module.login;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface LoginService extends IProvider {
    boolean hasLogin();
    void login(Context context);
}
