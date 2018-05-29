package com.example.email;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.email.login.LoginActivity;
import com.example.email.main.MainActivity;

public class AddAccountActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int from = i.getIntExtra("from",0);
        if (from == 0){
            if (isLogin()){
                Intent intent = new Intent(AddAccountActivity.this, MainActivity.class);
                intent.putExtra("email_id",getDefaultId());
                setEmailId(getDefaultId());
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.activity_add_account);

        TextView QQ = findViewById(R.id.QQ_text);
        TextView email_163 = findViewById(R.id.text_163);
        QQ.setOnClickListener(this);
        email_163.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.QQ_text:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("host","qq");
                startActivityForResult(intent,1);
                break;
            case R.id.text_163:
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("host","163");
                startActivityForResult(i,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private boolean isLogin(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("isLogin",false);
    }

    private int getDefaultId(){
        return sharedPreferences.getInt("default_id",0);
    }

    private void setEmailId(int emailId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("email_id",emailId);
        editor.apply();
    }
}
