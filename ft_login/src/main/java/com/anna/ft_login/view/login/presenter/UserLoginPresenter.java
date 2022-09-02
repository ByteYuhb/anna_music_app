package com.anna.ft_login.view.login.presenter;



import com.anna.ft_login.view.login.api.MockData;
import com.anna.ft_login.view.login.api.RequestCenter;
import com.anna.ft_login.view.login.inter.IUserLoginPresenter;
import com.anna.ft_login.view.login.inter.IUserLoginView;
import com.anna.ft_login.view.login.model.LoginEvent;
import com.anna.ft_login.view.login.model.user.User;
import com.anna.ft_login.view.login.utils.UserManager;
import com.anna.lib.network.listener.DisposeDataListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

/**
 * 登陆页面对应Presenter
 */
public class UserLoginPresenter implements IUserLoginPresenter, DisposeDataListener {

    private IUserLoginView mIView;

    public UserLoginPresenter(IUserLoginView iView) {
        mIView = iView;
    }

    @Override
    public void login(String username, String password) {
        mIView.showLoadingView();
        RequestCenter.login( this);
    }

    @Override
    public void onSuccess(Object responseObj) {
        mIView.hideLoadingView();
        User user = (User) responseObj;
        UserManager.getInstance().setUser(user);
        //发送登陆Event
        EventBus.getDefault().post(new LoginEvent());
        mIView.finishActivity();
    }

    @Override
    public void onFailure(Object reasonObj) {
        mIView.hideLoadingView();
        onSuccess(new Gson().fromJson(MockData.LOGIN_DATA, User.class));
        mIView.showLoginFailedView();
    }
}
