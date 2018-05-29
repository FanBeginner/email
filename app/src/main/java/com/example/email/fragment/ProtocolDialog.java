package com.example.email.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.email.R;

public class ProtocolDialog extends DialogFragment implements View.OnClickListener {

    private TextView pop3;
    private TextView imap;
    private TextView exchange;

    private protocolDialogListener listener;

    public interface protocolDialogListener{
        void protocolListener(String protocol);
    }

    public void setProtocolDialogListener(protocolDialogListener listener){
        this.listener = listener;
    }

    public static ProtocolDialog newInstance(String protocol){
        ProtocolDialog dialog = new ProtocolDialog();
        Bundle args = new Bundle();
        args.putString("protocol",protocol);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_protocol,container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pop3 = view.findViewById(R.id.POP3_protocol);
        imap = view.findViewById(R.id.IMAP_protocol);
        exchange = view.findViewById(R.id.Exchange_protocol);

        pop3.setOnClickListener(this);
        imap.setOnClickListener(this);
        exchange.setOnClickListener(this);

        if (getArguments() != null) {
            String protocol = getArguments().getString("protocol","");
            selectProtocol(protocol);
        }
    }

    @Override
    public void onClick(View v) {
        String protocol = null;
        switch (v.getId()){
            case R.id.POP3_protocol:
                protocol = pop3.getText().toString();
                selectProtocol(protocol);
                break;
            case R.id.IMAP_protocol:
                protocol = imap.getText().toString();
                selectProtocol(protocol);
                break;
            case R.id.Exchange_protocol:
                protocol = exchange.getText().toString();
                selectProtocol(protocol);
                break;
        }
        if (listener != null){
            listener.protocolListener(protocol);
        }
    }

    private void cancelSelect(){
        pop3.setTextColor(getResources().getColor(R.color.black));
        imap.setTextColor(getResources().getColor(R.color.black));
        exchange.setTextColor(getResources().getColor(R.color.black));

        pop3.setCompoundDrawables(null,null,null,null);
        imap.setCompoundDrawables(null,null,null,null);
        exchange.setCompoundDrawables(null,null,null,null);
    }

    private void selectProtocol(String protocol){
        cancelSelect();
        Drawable drawable = getResources().getDrawable(R.drawable.ic_check_black_24dp);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (protocol) {
            case "POP3":
                pop3.setTextColor(getResources().getColor(R.color.colorAccent));
                pop3.setCompoundDrawables(null, null, drawable, null);
                break;
            case "IMAP":
                imap.setTextColor(getResources().getColor(R.color.colorAccent));
                imap.setCompoundDrawables(null, null, drawable, null);
                break;
            case "Exchange":
                exchange.setTextColor(getResources().getColor(R.color.colorAccent));
                exchange.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }
}
