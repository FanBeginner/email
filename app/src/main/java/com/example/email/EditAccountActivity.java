package com.example.email;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.email.bean.Attachment;
import com.example.email.bean.EmailBean;
import com.example.email.bean.MessageBean;
import com.example.email.main.MainActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

public class EditAccountActivity extends BaseActivity {

    private int emailId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        ImageView back = findViewById(R.id.back_edit_account);
        TextView title = findViewById(R.id.title_edit_account);
        TextView delete = findViewById(R.id.delete_edit_account);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        emailId = intent.getIntExtra("emailId",0);

        title.setText(account);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteMenu(v);
            }
        });
    }

    //显示删除弹窗
    private void showDeleteMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_delete, null);
        TextView deleteMenu = view.findViewById(R.id.delete_menu_main);
        TextView cancelMenu = view.findViewById(R.id.cancel_menu_main);

        deleteMenu.setText("删除账号");

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

        deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataSupport.deleteAll(MessageBean.class,"emailId = ?",String.valueOf(emailId));//删除该账号的所有消息
                List<Attachment> attachmentList = DataSupport.where("emailId = ?"
                        ,String.valueOf(emailId)).find(Attachment.class);
                for (Attachment attachment:attachmentList){
                    if (attachment.getDownload()){
                        deleteFile(attachment.getFileName());
                    }
                    DataSupport.delete(Attachment.class,attachment.getId());
                }//删除该账号所有的附件
                DataSupport.delete(EmailBean.class,emailId);      //删除该账号
                List<EmailBean> list = DataSupport.findAll(EmailBean.class);

                if (list.size() > 0){

                    if (getDefaultId() == getEmailId()){       //登录的账号正好是默认账号
                        if (getDefaultId() == emailId){           //删除的是默认账号
                            setDefaultId(list.get(0).getId());      //将数据库中第一个设置为默认账号
                            setEmailId(list.get(0).getId());        //同时将数据库中第一个设置为登录账号
                            setResult(4);          //返回4，说明登录账号被删了
                        }else {
                            setResult(3);
                        }
                    }else {                               //默认账号和登录账号不是同一个
                        if (getDefaultId() == emailId){          //删除的是默认账号
                            setDefaultId(list.get(0).getId());              //将数据库中第一个设置为默认账号
                            setResult(RESULT_OK);      //返回ok,便是默认账号被删了
                        }else if (getEmailId() == emailId){          //说明删除的是此刻正登录的账号，删除后需要更新页面
                            setEmailId(getDefaultId());         //将登录账号设为默认账号
                            setResult(4);
                        }else {
                            setResult(3);        //返回3，说明删除的既不是登录账号，也不是默认账号
                        }
                    }
                }else {
                    setResult(5);     //说明凉了
                    setLoginStatus();
                    Intent intent = new Intent(EditAccountActivity.this,AddAccountActivity.class);
                    startActivity(intent);
                }
                finish();
                popWindow.dismiss();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private int getDefaultId(){
        return sharedPreferences.getInt("default_id",0);
    }

    private void setDefaultId(int defaultId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("default_id",defaultId);
        editor.apply();
    }

    private int getEmailId(){
        return sharedPreferences.getInt("email_id",0);
    }

    private void setEmailId(int emailId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("email_id",emailId);
        editor.apply();
    }

    private void setLoginStatus(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.apply();
    }
}
