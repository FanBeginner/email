package com.example.email.displayEmail;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.email.bean.Attachment;

import java.io.File;
import java.util.List;

public interface IDisplayEmailModel {

    void getMessageInformation(int emailId, int messageId, Activity activity);
    File downloadFile(String url,String fileName);
    WebResourceResponse getWebResourceResponse(String url);
    List<Attachment> getAttachmentInformation();
}
