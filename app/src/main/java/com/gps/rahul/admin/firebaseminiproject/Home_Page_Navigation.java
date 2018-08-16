package com.gps.rahul.admin.firebaseminiproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.Model.CategoryModel;
import com.gps.rahul.admin.firebaseminiproject.Service.ListenOrder;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.ItemOffsetDecoration;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home_Page_Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txt_title;
    RecyclerView home_page_recycler_view;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    private List<CategoryModel> categoryModels;
    FirebaseRecyclerAdapter<CategoryModel,MenuViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__page__navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);


        //Init Firebase
        //mCurrent_user_id = mAuth.getCurrentUser().getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Home_Page_Navigation.this,Cart.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name For user
        txt_title=(TextView)navigationView.getHeaderView(0).findViewById(R.id.txt_title);
        txt_title.setText(Common.currentuser.getName());

        //Load Menu
        home_page_recycler_view=(RecyclerView) findViewById(R.id.home_page_recycler_view);
        home_page_recycler_view.setHasFixedSize(true);

        home_page_recycler_view.setLayoutManager(new GridLayoutManager(Home_Page_Navigation.this, 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(Home_Page_Navigation.this, R.dimen.activity_horizontal_margin);
        home_page_recycler_view.addItemDecoration(itemDecoration);

        //home_page_recycler_view.setLayoutManager(new LinearLayoutManager(Home_Page_Navigation.this));
        //home_page_recycler_view.setLayoutManager(new GridLayoutManager(this, 2));
        categoryModels=new ArrayList<>();

        if(Common.isConnectToInternet(this)) {
            loadMenu();
        }
        else
        {
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
        //Register Service
        Intent service=new Intent(Home_Page_Navigation.this,ListenOrder.class);
        startService(service);

    }
    private void loadMenu() {
        adapter=new FirebaseRecyclerAdapter<CategoryModel, MenuViewHolder>(CategoryModel.class,R.layout.menu_item,MenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, CategoryModel model, int position) {
                viewHolder.menu_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.menu_image);
                final CategoryModel categoryModel=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(Home_Page_Navigation.this, ""+categoryModel.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodlist=new Intent(Home_Page_Navigation.this,FoodListActivity.class);
                        foodlist.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });
            }
        };
        home_page_recycler_view.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(!drawer.isDrawerOpen(GravityCompat.START)) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }).setNegativeButton("No", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home__page__navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh)
            loadMenu();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent i=new Intent(Home_Page_Navigation.this,Cart.class);
            startActivity(i);

        } else if (id == R.id.nav_orders) {
            Intent i=new Intent(Home_Page_Navigation.this,OrderStatus.class);
            startActivity(i);
        } else if (id == R.id.nav_log_out) {
            Intent i=new Intent(Home_Page_Navigation.this,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
