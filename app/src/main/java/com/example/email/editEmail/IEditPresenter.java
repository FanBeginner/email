package com.example.email.editEmail;

import android.content.Intent;

import com.example.email.bean.Attachment;

public interface IEditPresenter {
    void checkEmail(String recipient,String cc,String bcc);
    Attachment getEditAttachment(Intent intent);
    void removeAttachment(int position);
    void sendMessage(String recipient,String cc,String bcc,String subject,String content);
    void sending();
    void sendSuccess();
    void sendFailed();
}
