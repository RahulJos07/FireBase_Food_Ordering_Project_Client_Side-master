package com.gps.rahul.admin.firebaseminiproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Database.Database;
import com.gps.rahul.admin.firebaseminiproject.Interface.ItemClickListener;
import com.gps.rahul.admin.firebaseminiproject.Model.CategoryModel;
import com.gps.rahul.admin.firebaseminiproject.Service.ListenOrder;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.ItemOffsetDecoration;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class Home_Page_Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txt_title;
    RecyclerView home_page_recycler_view;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    private List<CategoryModel> categoryModels;
    FirebaseRecyclerAdapter<CategoryModel,MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__page__navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectToInternet(getBaseContext())) {
                    loadMenu();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default , load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectToInternet(getBaseContext())) {
                    loadMenu();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


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

        //Register Service
        Intent service=new Intent(Home_Page_Navigation.this,ListenOrder.class);
        startService(service);

    }
    private void loadMenu() {
        FirebaseRecyclerOptions<CategoryModel> options=new FirebaseRecyclerOptions.Builder<CategoryModel>()
                .setQuery(databaseReference,CategoryModel.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<CategoryModel, MenuViewHolder>(options) {

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(itemView);
            }
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull CategoryModel model) {
                holder.menu_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.menu_image);
                final CategoryModel categoryModel=model;
                holder.setItemClickListener(new ItemClickListener() {
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
        adapter.startListening();
        home_page_recycler_view.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
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
        }else if (id == R.id.nav_change_pwd) {
            ShowChangePasswordDialog();
        }
        else if (id == R.id.nav_log_out) {
            Intent i=new Intent(Home_Page_Navigation.this,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ShowChangePasswordDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home_Page_Navigation.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_pwd=inflater.inflate(R.layout.change_password_layout,null);
        final EditText edtPassword=(EditText)layout_pwd.findViewById(R.id.edtPassword);
        final EditText edtNewPassword=(EditText)layout_pwd.findViewById(R.id.edtNewPassword);
        final EditText edtRepeatPassword=(EditText)layout_pwd.findViewById(R.id.edtRepeatPassword);
        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Change password here

                final android.app.AlertDialog waitingDialog=new SpotsDialog(Home_Page_Navigation.this);
                //check old password
                if(edtPassword.getText().toString().equals(Common.currentuser.getPassword()))
                {
                    //check new password and  repeat password
                    if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString()))
                    {
                        Map<String,Object> passwordUpdate=new HashMap<>();
                        passwordUpdate.put("Password",edtNewPassword.getText().toString());

                        //Make update
                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentuser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            waitingDialog.dismiss();
                                        Toast.makeText(Home_Page_Navigation.this, "Password was Update", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home_Page_Navigation.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home_Page_Navigation.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }
}
