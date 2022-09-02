package com.anna.ft_home.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anna.ft_home.view.home.HomeActivity;
import com.anna.lib_base.module.home.HomeService;

@Route(path = "/home/home_service")
public class HomeServiceImpl implements HomeService {
    @Override
    public void startHomeActivity(Context context) {
        HomeActivity.start(context);
    }

    @Override
    public void init(Context context) {

    }
}
