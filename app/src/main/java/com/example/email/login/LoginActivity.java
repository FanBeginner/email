package com.example.email.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.email.BaseActivity;
import com.example.email.fragment.ProgressDialog;
import com.example.email.main.MainActivity;
import com.example.email.R;
import com.example.email.fragment.LoginFailDialog;
import com.example.email.fragment.ProtocolDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener,ILoginView {

    private TextView carryOut;
    private EditText address;
    private ImageView clear;
    private EditText password;
    private ImageView showOrHide;
    private TextView protocolText;
    private ProgressDialog progressDialog;
    private String host;
    private String smtpHost;
    private String logo;

    private boolean passwordIsVisible = false;
    private boolean clearIsVisible = true;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initAddress();
    }

    private void init(){

        loginPresenter = new LoginPresenter(this);

        ImageView back = findViewById(R.id.back_login);
        carryOut = findViewById(R.id.carry_out_login);
        address = findViewById(R.id.email_address_login);
        clear = findViewById(R.id.clear_address_login);
        password = findViewById(R.id.email_password_login);
        showOrHide = findViewById(R.id.show_or_hide_password_login);
        LinearLayout protocol = findViewById(R.id.protocol_login);
        protocolText = findViewById(R.id.protocol_text_login);

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!getAddress().equals("")){
                        showClearImage();
                    }
                }else {
                    hideClearImage();
                }
            }
        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getAddress().equals("")){
                    hideClearImage();
                }else{
                    if (!clearIsVisible){
                        showClearImage();
                    }
                }
                loginPresenter.checkEmail(getAddress(),getPassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginPresenter.checkEmail(getAddress(),getPassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);

        back.setOnClickListener(this);
        carryOut.setOnClickListener(this);
        clear.setOnClickListener(this);
        showOrHide.setOnClickListener(this);
        protocol.setOnClickListener(this);

        carryOut.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_login:
                finish();
                break;
            case R.id.carry_out_login:
                loginPresenter.login(getAddress(),getPassword(),getProtocol().toLowerCase(),logo,getHost(),smtpHost,this);
                break;
            case R.id.clear_address_login:
                clearAddress();
                hideClearImage();
                break;
            case R.id.show_or_hide_password_login:
                if (passwordIsVisible){
                    hidePassword();
                }else{
                    showPassword();
                }
                password.setSelection(getPassword().length());
                break;
            case R.id.protocol_login:
                showProtocolDialog(getProtocol());
                break;
        }
    }

    private void initAddress(){
        Intent intent = getIntent();
        logo = intent.getStringExtra("host");
        switch (logo) {
            case "qq":
                address.setText(R.string.email_qq);
                host = getProtocol().toLowerCase()+".qq.com";
                smtpHost = "smtp"+".qq.com";
                break;
            case "163":
                address.setText(R.string.email_163);
                host = getProtocol().toLowerCase()+".163.com";
                smtpHost = "smtp"+".163.com";
                break;
            case "126":
                address.setText(R.string.email_126);
                host = getProtocol().toLowerCase()+".126.com";
                smtpHost = "smtp"+".126.com";
                break;
        }
        address.setSelection(0,5);
    }

    private String getAddress() {
        return address.getText().toString();
    }


    private String getPassword() {
        return password.getText().toString();
    }


    private String getProtocol() {
        return protocolText.getText().toString();
    }

    private String getHost(){
        return host;
    }

    private void setHost(String protocol){
        if (protocol.equalsIgnoreCase("POP3")){
            switch (logo) {
                case "qq":
                    host = "pop.qq.com";
                    break;
                case "163":
                    host = "pop.163.com";
                    break;
                case "126":
                    host = "pop.126.com";
                    break;
            }
        }else if (protocol.equalsIgnoreCase("IMAP")){
            switch (logo) {
                case "qq":
                    host = "imap.qq.com";
                    break;
                case "163":
                    host = "imap.163.com";
                    break;
                case "126":
                    host = "imap.126.com";
                    break;
            }
        }
    }


    private void clearAddress() {
        address.setText("");
    }

    private void hidePassword() {
        passwordIsVisible = false;
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showOrHide.setImageResource(R.drawable.ic_visibility_off_black_24dp);
    }

    private void showPassword() {
        passwordIsVisible = true;
        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        showOrHide.setImageResource(R.drawable.ic_visibility_black_24dp);
    }

    private void hideClearImage(){
        clearIsVisible = false;
        clear.setVisibility(View.GONE);
    }

    private void showClearImage(){
        clearIsVisible = true;
        clear.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCarryOut() {
        carryOut.setTextColor(getResources().getColor(R.color.colorAccent));
        carryOut.setClickable(true);
    }

    @Override
    public void hideCarryout() {
        carryOut.setTextColor(-1979711488);
        carryOut.setClickable(false);
    }

    @Override
    public void showProgressDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        progressDialog = new ProgressDialog();
        progressDialog.show(fragmentManager,"progressDialog");
        progressDialog.setCancelable(false);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFailDialog dialog = new LoginFailDialog();
        dialog.show(fragmentManager,"loginFail");
        dialog.setCancelable(false);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void jumpInterface(int email_id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email_id",email_id);
        setResult(RESULT_OK);
        startActivity(intent);
    }

    private void showProtocolDialog(String protocol){
        FragmentManager fragmentManager = getSupportFragmentManager();
        final ProtocolDialog dialog = ProtocolDialog.newInstance(protocol);
        dialog.show(fragmentManager,"ProtocolDialog");
        dialog.setProtocolDialogListener(new ProtocolDialog.protocolDialogListener() {
            @Override
            public void protocolListener(String protocol) {
                protocolText.setText(protocol);
                setHost(protocol);
                dialog.dismiss();
            }
        });
    }
}
