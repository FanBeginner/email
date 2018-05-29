package com.example.email.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.email.R;
import com.example.email.bean.EmailBean;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private SharedPreferences sharedPreferences;
    private List<EmailBean> list;
    private OnAddressListener listener;

    public interface OnAddressListener{
        void setEmailId(EmailBean email);
    }

    public void setAddressListener(OnAddressListener listener){
        this.listener = listener;
    }

    public AddressAdapter(List<EmailBean> list){
        this.list = list;
    }

    public void setList(List<EmailBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                EmailBean email = list.get(position);
                setEmailId(email.getId());      //更新登录账号
                notifyDataSetChanged();    //更新视图
                if (listener != null){
                    listener.setEmailId(email);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmailBean email = list.get(position);
        switch (email.getLogo()){
            case "qq":
                holder.logo.setImageResource(R.drawable.ic_logo_qq);
                break;
            case "163":
                holder.logo.setImageResource(R.drawable.ic_logo_163);
                break;
        }
        holder.address.setText(email.getAddress());
        if (getEmailId() == email.getId()){
            holder.check.setVisibility(View.VISIBLE);
        }else {
            holder.check.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView logo;
        TextView address;
        ImageView check;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.address_layout);
            logo = itemView.findViewById(R.id.logo_item);
            address = itemView.findViewById(R.id.address_item);
            check = itemView.findViewById(R.id.check_item);
        }
    }

    private int getEmailId(){
        if (sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences.getInt("email_id",0);
    }

    private void setEmailId(int emailId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("email_id",emailId);
        editor.apply();
    }
}
