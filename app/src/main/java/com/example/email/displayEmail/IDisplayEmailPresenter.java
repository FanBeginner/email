package com.example.email.displayEmail;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.email.bean.Attachment;
import com.example.email.bean.MessageBean;

import java.io.File;
import java.util.List;

public interface IDisplayEmailPresenter {
    void getInformation(Activity activity);
    File download(String url,String fileName);
    WebResourceResponse getWebResourceResponse(String url);
    void getInformationSuccess(MessageBean messageBean);
    void getInformationFail();
}
