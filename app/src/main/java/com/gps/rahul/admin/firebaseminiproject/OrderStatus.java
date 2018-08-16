package com.gps.rahul.admin.firebaseminiproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Model.RequestModel;
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
        adapter=new FirebaseRecyclerAdapter<RequestModel, OrderViewHolder>(
                RequestModel.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                databaseReference.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, RequestModel model, int position) {
                viewHolder.order_id.setText(adapter.getRef(position).getKey());
                viewHolder.order_status.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.order_phone.setText(model.getPhone());
                viewHolder.order_address.setText(model.getAddress());
            }
        };
        listorders.setAdapter(adapter);
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
