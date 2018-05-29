package com.example.email.login;

public interface ILoginView {

    void showCarryOut();
    void hideCarryout();
    void showProgressDialog();
    void hideProgressDialog();
    void showErrorDialog();
    void finishActivity();
    void jumpInterface(int email_id);
}
