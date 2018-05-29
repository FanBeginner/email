package com.example.email.displayEmail;

import com.example.email.bean.Attachment;
import com.example.email.bean.MessageBean;

import java.util.List;

public interface IDisplayEmailView {

    int getEmailId();
    int getMessageId();
    void initializeInterface(MessageBean messageBean);
    void showAttachmentLayout();
    void setRecyclerView(List<Attachment> list);
    void showErrorMessage();
}
