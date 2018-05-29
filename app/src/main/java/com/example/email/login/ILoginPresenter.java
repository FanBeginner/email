package com.example.email.login;

import android.app.Activity;

public interface ILoginPresenter {

    void checkEmail(String address,String password);
    void login(String address, String password, String protocol,String logo,String host,String smtpHost, Activity activity);
    void loginSuccess(int email_id);
    void loginFail();
}
