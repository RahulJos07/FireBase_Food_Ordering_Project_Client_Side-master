package com.gps.rahul.admin.firebaseminiproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Model.FoodModel;
import com.gps.rahul.admin.firebaseminiproject.Model.RequestModel;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.MenuViewHolder;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView listorders;
    public RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<RequestModel,OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference= FirebaseDatabase.getInstance().getReference("Requests");
        listorders=(RecyclerView)findViewById(R.id.listorders);
        listorders.setHasFixedSize(true);
        listorders.setLayoutManager(new LinearLayoutManager(OrderStatus.this));

        //if(getIntent().equals(null))
            loadOrders(Common.currentuser.getPhone());
        //else
          //  loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {

        Query matchId=databaseReference.orderByChild("phone").equalTo(phone);
        FirebaseRecyclerOptions<RequestModel> options=new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(matchId,RequestModel.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<RequestModel, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull RequestModel model) {
                viewHolder.order_id.setText(adapter.getRef(position).getKey());
                viewHolder.order_status.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.order_phone.setText(model.getPhone());
                viewHolder.order_address.setText(model.getAddress());
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        listorders.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
