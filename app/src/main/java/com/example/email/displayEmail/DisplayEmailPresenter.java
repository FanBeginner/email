package com.example.email.displayEmail;

import android.app.Activity;
import android.webkit.WebResourceResponse;

import com.example.email.bean.Attachment;
import com.example.email.bean.MessageBean;

import java.io.File;
import java.util.List;

public class DisplayEmailPresenter implements IDisplayEmailPresenter {

    private IDisplayEmailView iDisplayEmailView;
    private IDisplayEmailModel iDisplayEmailModel;

    DisplayEmailPresenter(IDisplayEmailView iDisplayEmailView){
        this.iDisplayEmailView = iDisplayEmailView;
        iDisplayEmailModel = new DisplayEmailModel(this);
    }

    @Override
    public void getInformation(Activity activity) {
        int emailId = iDisplayEmailView.getEmailId();
        int messageId = iDisplayEmailView.getMessageId();
        iDisplayEmailModel.getMessageInformation(emailId, messageId,activity);
    }

    @Override
    public File download(String url, String fileName) {
        return iDisplayEmailModel.downloadFile(url, fileName);
    }

    @Override
    public WebResourceResponse getWebResourceResponse(String url) {
        return iDisplayEmailModel.getWebResourceResponse(url);
    }


    @Override
    public void getInformationSuccess(MessageBean messageBean) {
        iDisplayEmailView.initializeInterface(messageBean);
        if (messageBean.isAttachment()){
            iDisplayEmailView.setRecyclerView(iDisplayEmailModel.getAttachmentInformation());
            iDisplayEmailView.showAttachmentLayout();
        }
    }

    @Override
    public void getInformationFail() {
        iDisplayEmailView.showErrorMessage();
    }


}
