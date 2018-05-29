package com.example.email.editEmail;

public interface IEditView {
    int getEditMessageId();
    void canSend();
    void cannotSend();
    void toastSending();
    void toastSuccess();
    void toastFailed();
    void killActivity();
}
