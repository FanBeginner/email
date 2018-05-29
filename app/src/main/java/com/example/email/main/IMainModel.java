package com.example.email.main;

import android.app.Activity;

import com.example.email.bean.MessageBean;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

public interface IMainModel {
    void getMessageList(int emailId,Activity activity);
    void getListFromServer();
    void refreshData(int emailId);
    void deleteEmail(List<Integer> list);
    void updateSubscript();
}
