package com.gps.rahul.admin.firebaseminiproject.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.Model.OrderModel;
import com.gps.rahul.admin.firebaseminiproject.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        ,View.OnCreateContextMenuListener
    {

    public TextView cart_item_name,cart_item_price;
    public ImageView cart_item_count;

    private ItemClickListener itemClickListener;

    public void setCart_item_name(TextView cart_item_name) {
        this.cart_item_name = cart_item_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        cart_item_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        cart_item_price=(TextView)itemView.findViewById(R.id.cart_item_price);
        cart_item_count=(ImageView) itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select action");
            contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
        }
    }

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<OrderModel> listData=new ArrayList<>();
    private Context context;

    public CartAdapter(List<OrderModel> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        TextDrawable drawable=TextDrawable.builder().buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.cart_item_count.setImageDrawable(drawable);
        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        int price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.cart_item_price.setText(fmt.format(price));
        holder.cart_item_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
