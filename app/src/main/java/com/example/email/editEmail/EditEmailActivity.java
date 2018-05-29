package com.example.email.editEmail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.email.BaseActivity;
import com.example.email.R;
import com.example.email.adapter.EditAttachmentAdapter;
import com.example.email.bean.Attachment;
import com.example.email.bean.MessageBean;
import com.example.email.fragment.SaveDialog;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditEmailActivity extends BaseActivity implements View.OnClickListener,IEditView {

    private ImageView attachment;
    private ImageView send;
    private EditText recipient;
    private ImageView clearRecipient;
    private EditText cc;
    private ImageView clearCC;
    private EditText bcc;
    private ImageView clearBCC;
    private EditText subject;
    private RecyclerView recyclerView;
    private EditText content;

    private EditPresenter editPresenter;

    private EditAttachmentAdapter adapter;
    private List<Attachment> attachmentList;
    private int messageId;
    private MessageBean messageBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        init();
        initData();
    }

    private void init(){

        ImageView back = findViewById(R.id.back_edit);
        attachment = findViewById(R.id.attachment_edit);
        send = findViewById(R.id.send_edit);
        recipient = findViewById(R.id.recipient_edit);
        clearRecipient = findViewById(R.id.clear_recipient_edit);
        cc = findViewById(R.id.CC_edit);
        clearCC = findViewById(R.id.clear_CC_edit);
        bcc = findViewById(R.id.BCC_edit);
        clearBCC = findViewById(R.id.clear_BCC_edit);
        subject = findViewById(R.id.subject_edit);

        recyclerView = findViewById(R.id.recycler_edit);

        content = findViewById(R.id.content_edit);


        back.setOnClickListener(this);
        attachment.setOnClickListener(this);
        send.setOnClickListener(this);
        clearRecipient.setOnClickListener(this);
        clearCC.setOnClickListener(this);
        clearBCC.setOnClickListener(this);

        send.setClickable(false);     //禁止点击
    }

    private void initData(){
        attachmentList = new ArrayList<>();
        messageId = getMessageId();
        if (messageId == 0){
            messageBean = new MessageBean();
            messageBean.setEmailId(getEmailId());
            messageBean.save();             //保存到数据库，为了得到id

            Intent intent = getIntent();
            String mark = intent.getStringExtra("mark");
            switch (mark) {
                case "reply": {
                    String to = intent.getStringExtra("to");
                    if (to.contains(",")) {
                        to = to.substring(0, to.indexOf(",") - 1);
                    }
                    recipient.setText(to);
                    break;
                }
                case "replyAll": {
                    String to = intent.getStringExtra("to");
                    recipient.setText(to);
                    break;
                }
                case "forward":
                    int otherId = intent.getIntExtra("otherId", 0);
                    messageBean = getMessage(otherId);
                    subject.setText(messageBean.getSubject());
                    content.setText(messageBean.getText());
                    break;
            }
        }else {
            messageBean = DataSupport.find(MessageBean.class,messageId);
            recipient.setText(messageBean.getTo());
            cc.setText(messageBean.getCc());
            bcc.setText(messageBean.getBcc());
            subject.setText(messageBean.getSubject());
            content.setText(messageBean.getPlain());

            if (messageBean.isAttachment()){
                attachmentList = DataSupport.where("messageId = ?",String.valueOf(messageId)).find(Attachment.class);
                showRecyclerView(attachmentList);
            }
        }

        editPresenter = new EditPresenter(this,attachmentList);

        recipient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")){
                    showClear(clearRecipient);
                }else{
                    hideClear(clearRecipient);
                }
                editPresenter.checkEmail(getText(recipient),getText(cc),getText(bcc));
            }
        });

        cc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")){
                    showClear(clearCC);
                }else{
                    hideClear(clearCC);
                }
                editPresenter.checkEmail(getText(recipient),getText(cc),getText(bcc));
            }
        });

        bcc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")){
                    showClear(clearBCC);
                }else{
                    hideClear(clearBCC);
                }
                editPresenter.checkEmail(getText(recipient),getText(cc),getText(bcc));
            }
        });
    }

    private String getText(EditText editText){
        return editText.getText().toString();
    }

    private void clearText(EditText editText){
        editText.setText("");
    }

    private void showClear(View view){
        view.setVisibility(View.VISIBLE);
    }

    private void hideClear(View view){
        view.setVisibility(View.GONE);
    }

    private void showRecyclerView(final List<Attachment> list){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EditAttachmentAdapter(list);
        //监听回调
        adapter.setEditAttachmentListener(new EditAttachmentAdapter.EditAttachmentListener() {
            @Override
            public void removeItem(int position) {
                //editPresenter.removeAttachment(position);
                Attachment attachment = attachmentList.get(position);
                attachment = DataSupport.find(Attachment.class,attachment.getId());
                if (attachment != null){
                    DataSupport.delete(Attachment.class,attachment.getId());
                }
                attachmentList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private int getEmailId(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("email_id",0);
    }

    private MessageBean getMessage(int otherId){
        List<MessageBean> list =  DataSupport.where("otherId = ?",String.valueOf(otherId)).find(MessageBean.class);
        return list.get(0);
    }

    private int getMessageId(){
        Intent intent = getIntent();
        return intent.getIntExtra("messageId",0);
    }

    // 显示底部按钮
    private void showBottomMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_attachment, null);
        TextView gallery = view.findViewById(R.id.gallery);
        TextView fileManagement = view.findViewById(R.id.file_management);

        final PopupWindow popWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable(0));
        popWindow.setAnimationStyle(R.style.bottom_menu_anim_style);
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.6f);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditEmailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditEmailActivity.this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                } else {
                    openGallery();
                }
                popWindow.dismiss();
            }
        });

        fileManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditEmailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditEmailActivity.this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openFileManagement();
                }
                popWindow.dismiss();
            }
        });
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);      无用
        startActivityForResult(intent,0);
    }

    private void openFileManagement(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showIsSaveDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        final SaveDialog dialog = new SaveDialog();
        dialog.show(fragmentManager,"saveDialog");
        dialog.setSaveDialogListener(new SaveDialog.onSaveDialogListener() {
            @Override
            public void isSave(boolean isSave) {
                if (isSave){      //保存到草稿箱
                    messageBean.setLocal(true);
                    messageBean.setSend(false);
                    messageBean.setRead(true);
                    messageBean.setDate(getDate());
                    messageBean.setTo(getText(recipient));
                    messageBean.setCc(getText(cc));
                    messageBean.setBcc(getText(bcc));
                    messageBean.setSubject(getText(subject));
                    messageBean.setPlain(getText(content));
                    messageBean.setText(getText(content));

                    if (attachmentList.size()> 0){
                        long size = 0;
                        for (Attachment attachment : attachmentList){
                            attachment.save();
                            size += attachment.getSize();
                        }
                        messageBean.setAttachment(true);
                        messageBean.setAttachmentNum(attachmentList.size());
                        messageBean.setAttachmentSize(Formatter.formatFileSize(EditEmailActivity.this,size));
                    }else {
                        messageBean.setAttachment(false);
                    }
                    messageBean.update(getEditMessageId());
                }else {              //不保存
                    if (messageBean.isAttachment()){
                        List<Attachment> attachments = DataSupport.where("messageId = ?"
                                ,String.valueOf(messageBean.getId())).find(Attachment.class);
                        if (attachments.size()>0){
                            DataSupport.deleteAll(Attachment.class,"messageId = ?",String.valueOf(messageBean.getId()));
                        }
                    }
                    DataSupport.delete(MessageBean.class,messageBean.getId());
                }
                setResult(RESULT_OK);
                dialog.dismiss();
                finish();
            }
        });
    }

    /**
     * 获得邮件发送日期
     */
    @SuppressLint("SimpleDateFormat")
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(date);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_in, R.anim.bottom_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_edit:
                finish();
                break;
            case R.id.attachment_edit:
                hideSoftKeyboard(v);
                showBottomMenu(attachment);
                break;
            case R.id.clear_recipient_edit:
                clearText(recipient);
                break;
            case R.id.clear_CC_edit:
                clearText(cc);
                break;
            case R.id.clear_BCC_edit:
                clearText(bcc);
                break;
            case R.id.send_edit:
                editPresenter.sendMessage(getText(recipient),getText(cc),getText(bcc),getText(subject),getText(content));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openFileManagement();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    Attachment attachment = editPresenter.getEditAttachment(data);
                    if (attachmentList.size() == 0){
                        attachmentList.add(attachment);
                        showRecyclerView(attachmentList);
                    }else {
                        attachmentList.add(attachment);
                        adapter.notifyItemInserted(adapter.getItemCount());
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK){
                    Attachment attachment = editPresenter.getEditAttachment(data);
                    if (attachmentList.size() == 0){
                        attachmentList.add(attachment);
                        showRecyclerView(attachmentList);
                    }else {
                        attachmentList.add(attachment);
                        adapter.notifyItemInserted(adapter.getItemCount());
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (!getText(recipient).equals("") || !getText(cc).equals("") || !getText(bcc).equals("")
                || !getText(subject).equals("") || !getText(content).equals("") || (attachmentList.size() > 0)){
            showIsSaveDialog();
        }else {
            DataSupport.delete(MessageBean.class,messageBean.getId());         //空白就删除
            super.onBackPressed();
        }
    }

    @Override
    public int getEditMessageId() {
        return messageBean.getId();
    }

    @Override
    public void canSend() {
        send.setImageResource(R.drawable.mz_tab_ic_send_dark);
        send.setClickable(true);       //允许点击
    }

    @Override
    public void cannotSend() {
        send.setImageResource(R.drawable.ic_tb_send_disable);
        send.setClickable(false);       //禁止点击
    }

    @Override
    public void toastSending() {
        Toast.makeText(this,"正在发送中。。。",Toast.LENGTH_LONG).show();
    }

    @Override
    public void toastSuccess() {
        Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastFailed() {
        Toast.makeText(this,"发送失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void killActivity() {
        finish();
    }
}
