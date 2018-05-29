package com.example.email.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.email.EditAccountActivity;
import com.example.email.R;
import com.example.email.bean.EmailBean;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private Context context;
    private List<EmailBean> list;

    public AccountAdapter(List<EmailBean> list){
        this.list = list;
    }

    @NonNull
    @Override
    public AccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EmailBean email = list.get(position);
                Intent intent = new Intent(context, EditAccountActivity.class);
                intent.putExtra("account",email.getAddress());
                intent.putExtra("emailId",email.getId());
                Activity activity = (Activity) context;
                activity.startActivityForResult(intent,0);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        EmailBean email = list.get(position);
        holder.account.setText(email.getAddress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView account;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_layout);
            account = itemView.findViewById(R.id.account_item);
        }
    }
}
