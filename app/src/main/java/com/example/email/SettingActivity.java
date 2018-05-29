package com.example.email;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.email.adapter.AccountAdapter;
import com.example.email.bean.EmailBean;
import com.example.email.fragment.DefaultDialog;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private RelativeLayout defaultLayout;
    private TextView defaultAccount;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView back = findViewById(R.id.back_setting);
        defaultLayout = findViewById(R.id.default_setting);
        defaultAccount = findViewById(R.id.default_account_setting);

        recyclerView = findViewById(R.id.recycler_setting);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this,RecyclerView.VERTICAL));

        TextView add = findViewById(R.id.add_setting);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        back.setOnClickListener(this);
        defaultLayout.setOnClickListener(this);
        defaultAccount.setOnClickListener(this);
        add.setOnClickListener(this);

        List<EmailBean> list = DataSupport.findAll(EmailBean.class);
        if (list.size() == 1){
            defaultLayout.setVisibility(View.GONE);
        }else {
            EmailBean email = DataSupport.find(EmailBean.class,getDefaultId());
            defaultAccount.setText(email.getAddress());
        }
        adapter = new AccountAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_setting:
                finish();
                break;
            case R.id.default_setting:
                showDefaultDialog();
                break;
            case R.id.add_setting:
                Intent intent = new Intent(this,AddAccountActivity.class);
                intent.putExtra("from",1);
                startActivityForResult(intent,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK || resultCode == 4 || resultCode == 3){
                    //ok默认账号，4登录账号，3既不是登录账号，也不是默认账号
                    //这一步说明删了一个账号，保险起见，刷新
                    List<EmailBean> list = DataSupport.findAll(EmailBean.class);
                    if (list.size() == 1){
                        defaultLayout.setVisibility(View.GONE);
                    }else {
                        EmailBean email = DataSupport.find(EmailBean.class,getDefaultId());
                        defaultAccount.setText(email.getAddress());
                    }
                    adapter = new AccountAdapter(list);
                    recyclerView.setAdapter(adapter);

                    if (resultCode == 4){
                        //登录账号被删，
                        setResult(RESULT_OK);        //返回ok,让主页面刷新
                    }else {
                        setResult(4);       //让address列表更新
                    }

                }else if (resultCode == 5){
                    //说明凉了，去死吧
                    setResult(5);         //让上一个页面也凉吧
                    finish();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK){      //成功添加了一个账号
                    setResult(5);    //关闭主页面
                    finish();
                }
                break;
        }
    }

    private void showDefaultDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        final DefaultDialog dialog = new DefaultDialog();
        dialog.show(fragmentManager,"DefaultDialog");
        dialog.setDialogListener(new DefaultDialog.OnDialogListener() {
            @Override
            public void setDefault(EmailBean email) {
                defaultAccount.setText(email.getAddress());
                setDefaultId(email.getId());
                dialog.dismiss();
            }
        });
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
}
