package com.anna.lib_base.module.home;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface HomeService extends IProvider {
    void startHomeActivity(Context context);
}
