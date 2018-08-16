package com.gps.rahul.admin.firebaseminiproject;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Model.OrderModel;
import com.gps.rahul.admin.firebaseminiproject.Model.RequestModel;
import com.gps.rahul.admin.firebaseminiproject.ViewHolder.CartAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gps.rahul.admin.firebaseminiproject.Database.Database;

public class Cart extends AppCompatActivity {
    RecyclerView listCart;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    TextView totalprice;
    Button btnPlaceOrder;
    List<OrderModel> cart=new ArrayList<>();
    CartAdapter cartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference=FirebaseDatabase.getInstance().getReference("Requests");

        listCart=(RecyclerView)findViewById(R.id.listCart);
        listCart.setHasFixedSize(true);
        listCart.setLayoutManager(new LinearLayoutManager(Cart.this));

        totalprice=(TextView)findViewById(R.id.total);
        btnPlaceOrder=(Button)findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size()>0) {
                    showAlertDialog();
                }
                else
                    Toast.makeText(Cart.this, "Your card is empty !!!!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More steps!");
        alertDialog.setMessage("Enter Your Address:  ");
        final EditText edtAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);//Add Edit Text To alert Dialog
        alertDialog.setIcon(R.drawable.shopping_cart);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RequestModel requestModel=new RequestModel(Common.currentuser.getPhone(),
                        Common.currentuser.getName(),
                        edtAddress.getText().toString(),
                        totalprice.getText().toString(),
                        cart);
                databaseReference.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(requestModel);
                //Delete Cart
                new Database(getBaseContext()).clearCart();
                Toast.makeText(Cart.this, "Thank You , Order Palace", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadListFood() {
        cart=new Database(this).getCarts();
        cartAdapter=new CartAdapter(cart,this);
        cartAdapter.notifyDataSetChanged();
        listCart.setAdapter(cartAdapter);

        //Calculate Total Price
        int total=0;
        for(OrderModel orderModel:cart)
        {
            total+=(Integer.parseInt(orderModel.getPrice()))*(Integer.parseInt(orderModel.getQuantity()));
            Locale locale=new Locale("en","US");
            NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

            totalprice.setText(fmt.format(total));

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
        {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).clearCart();
        for(OrderModel item:cart)
            new Database(this).addToCart(item);
        //Refresh
        loadListFood();
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
