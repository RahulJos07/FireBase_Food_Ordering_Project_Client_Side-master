package com.gps.rahul.admin.firebaseminiproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView order_id,order_status,order_phone,order_address;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);
        order_id=(TextView)itemView.findViewById(R.id.order_id);
        order_status=(TextView)itemView.findViewById(R.id.order_status);
        order_phone=(TextView)itemView.findViewById(R.id.order_phone);
        order_address=(TextView)itemView.findViewById(R.id.order_address);

        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onclick(view,getAdapterPosition(),false);
    }
}
