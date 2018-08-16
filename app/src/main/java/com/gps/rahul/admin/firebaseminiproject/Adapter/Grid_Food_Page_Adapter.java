package com.gps.rahul.admin.firebaseminiproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gps.rahul.admin.firebaseminiproject.Model.CategoryModel;
import com.gps.rahul.admin.firebaseminiproject.Model.FoodModel;
import com.gps.rahul.admin.firebaseminiproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Grid_Food_Page_Adapter extends BaseAdapter{
    Context context;
    int menu_item;
    List<FoodModel> foodModels;
    public Grid_Food_Page_Adapter(Context context, int menu_item, List<FoodModel> foodModels) {
        this.context=context;
        this.menu_item=menu_item;
        this.foodModels=foodModels;
    }

    @Override
    public int getCount() {
        return foodModels.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(context).inflate(menu_item,viewGroup,false);
        TextView menu_name=(TextView)view.findViewById(R.id.food_name);
        ImageView menu_image=(ImageView)view.findViewById(R.id.food_image);
        menu_name.setText(foodModels.get(i).getName());
        Picasso.with(context).load(foodModels.get(i).getImage()).into(menu_image);
        return view;
    }
}
