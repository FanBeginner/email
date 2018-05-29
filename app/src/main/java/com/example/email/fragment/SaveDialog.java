package com.example.email.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.email.R;

public class SaveDialog extends DialogFragment {

    private onSaveDialogListener listener;

    public interface onSaveDialogListener{
        void isSave(boolean isSave);
    }

    public void setSaveDialogListener(onSaveDialogListener saveDialogListener){
        listener = saveDialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_save,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView giveUp = view.findViewById(R.id.give_up_dialog);
        TextView save = view.findViewById(R.id.save_dialog);

        giveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.isSave(false);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.isSave(true);
                }
            }
        });
    }
}
