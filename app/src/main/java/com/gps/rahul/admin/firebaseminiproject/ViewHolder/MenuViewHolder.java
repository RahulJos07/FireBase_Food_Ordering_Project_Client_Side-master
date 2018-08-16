package com.gps.rahul.admin.firebaseminiproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    public TextView menu_name;
    public ImageView menu_image;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        menu_name = itemView.findViewById(R.id.menu_name);
        menu_image = itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onclick(view, getAdapterPosition(), false);
    }
}
