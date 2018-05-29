package com.example.email.editEmail;

import android.app.Activity;
import android.content.Intent;

import com.example.email.bean.Attachment;

import java.util.List;

class EditPresenter implements IEditPresenter {

    private IEditView iEditView;
    private IEditModel iEditModel;

    EditPresenter(IEditView iEditView, List<Attachment> list){
        this.iEditView = iEditView;
        iEditModel = new EditModel(this,(Activity) iEditView,list);
    }

    @Override
    public void checkEmail(String recipient,String cc,String bcc) {
        if (iEditModel.checkAddress(recipient) || iEditModel.checkAddress(cc) || iEditModel.checkAddress(bcc)){
            iEditView.canSend();
        }else {
            iEditView.cannotSend();
        }
    }

    @Override
    public Attachment getEditAttachment(Intent intent) {
        return iEditModel.getInformationFromIntent(intent,iEditView.getEditMessageId());
    }

    @Override
    public void removeAttachment(int position) {
        iEditModel.removeAttachment(position);
    }

    @Override
    public void sendMessage(final String recipient, final String cc, final String bcc, final String subject, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                iEditModel.sendMessage(recipient, cc, bcc, subject, content,iEditView.getEditMessageId());
            }
        }).start();
        sending();
    }

    @Override
    public void sending() {
        iEditView.toastSending();
        iEditView.killActivity();
    }

    @Override
    public void sendSuccess() {
        iEditView.toastSuccess();
    }

    @Override
    public void sendFailed() {
        iEditView.toastFailed();
    }
}
