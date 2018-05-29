package com.example.email.main;

import android.app.Activity;

import com.example.email.bean.MessageBean;

import java.util.List;

public interface IMainPresenter {

    void getMessageList(Activity activity);
    void getListSuccess(List<MessageBean> list);
    void getListFail();

    void refreshData(int emailId);
    void refreshSuccess(List<MessageBean> list);
    void refreshFail();

    void deleteEmail(List<Integer> list);
}
