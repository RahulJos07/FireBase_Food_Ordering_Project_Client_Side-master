package com.gps.rahul.admin.firebaseminiproject;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gps.rahul.admin.firebaseminiproject.Adapter.Grid_Food_Page_Adapter;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Database.Database;
import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.Model.FoodModel;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.FoodViewHolder;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.ItemOffsetDecoration;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {
    RecyclerView food_recycler_view;
    DatabaseReference databaseReference;
    String CategoryId="";
    FirebaseRecyclerAdapter<FoodModel,FoodViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<FoodModel,FoodViewHolder> searchadapter;
    List<String> suggestList=new ArrayList<>();
    //MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        getSupportActionBar().hide();
        databaseReference= FirebaseDatabase.getInstance().getReference("Food");

        //Local DB
        localDB=new Database(this);
        food_recycler_view=(RecyclerView) findViewById(R.id.food_recycler_view);
        food_recycler_view.setHasFixedSize(true);
        food_recycler_view.setLayoutManager(new GridLayoutManager(FoodListActivity.this, 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(FoodListActivity.this, R.dimen.activity_horizontal_margin);
        food_recycler_view.addItemDecoration(itemDecoration);

        //GetIntent
        if(getIntent()!=null)
        {
            CategoryId=getIntent().getStringExtra("CategoryId");
        }
        if(!CategoryId.isEmpty() && CategoryId!=null)
        {
            if(Common.isConnectToInternet(getBaseContext())) {
                loadFood(CategoryId);
            }
            else
            {
                Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        /*//Search
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your Food");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //when user type their text , we will change suggest list

                List<String> suggest=new ArrayList<String>();

                for(String search:suggest)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    food_recycler_view.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });*/
    }

    private void startSearch(CharSequence text) {
        searchadapter=new FirebaseRecyclerAdapter<FoodModel, FoodViewHolder>(
                FoodModel.class,
                R.layout.food_item,
                FoodViewHolder.class,
                databaseReference.orderByChild("Name").equalTo(text.toString())) //Compare Name
        {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, FoodModel model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                final FoodModel local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(FoodListActivity.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent fooddetail=new Intent(FoodListActivity.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",searchadapter.getRef(position).getKey());
                        startActivity(fooddetail);
                    }
                });
            }
        };
        food_recycler_view.setAdapter(searchadapter);
    }

    private void loadSuggest() {
        databaseReference.orderByChild("menuId").equalTo(CategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                    FoodModel foodModel =postSnapshot.getValue(FoodModel.class);
                    suggestList.add(foodModel.getName()); //Add Name of food to suggest list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<FoodModel, FoodViewHolder>(FoodModel.class,
                R.layout.food_item,
                FoodViewHolder.class,
                databaseReference.orderByChild("menuId").equalTo(categoryId) // like : Select * from Foods where MenuId =
            ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final FoodModel model, final int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                //Add Favorites
                if(localDB.isFavorites(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of Favorites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!localDB.isFavorites(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp  );
                            Toast.makeText(FoodListActivity.this, ""+model.getName()+" was remove from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final FoodModel local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(FoodListActivity.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent fooddetail=new Intent(FoodListActivity.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(fooddetail);
                    }
                });
            }
        };
        food_recycler_view.setAdapter(adapter);
    }
}
