package com.example.email.editEmail;

import android.content.Intent;

import com.example.email.bean.Attachment;

public interface IEditModel {
    boolean checkAddress(String address);
    Attachment getInformationFromIntent(Intent intent, int editMessageId);
    void removeAttachment(int position);
    void sendMessage(String recipient,String cc,String bcc,String subject,String content,long messageId);
}
