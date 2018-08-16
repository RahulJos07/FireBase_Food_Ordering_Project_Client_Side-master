package com.gps.rahul.admin.firebaseminiproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Database.Database;
import com.gps.rahul.admin.firebaseminiproject.Model.FoodModel;
import com.gps.rahul.admin.firebaseminiproject.Model.OrderModel;
import com.gps.rahul.admin.firebaseminiproject.Model.Rating;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetails extends AppCompatActivity implements RatingDialogListener{
    ImageView img_food;
    TextView food_name,food_price,food_description;
    FloatingActionButton btn_cart,btn_rating;
    RatingBar ratingBar;
    ElegantNumberButton elegantNumberButton;
    String Food_Id="";
    DatabaseReference databaseReference;
    DatabaseReference databaseReference_rating;
    FoodModel foodModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        databaseReference= FirebaseDatabase.getInstance().getReference("Food");
        databaseReference_rating=FirebaseDatabase.getInstance().getReference("Rating");

        img_food=(ImageView)findViewById(R.id.img_food_detail);
        food_name=(TextView)findViewById(R.id.food_name_detail);
        food_price=(TextView)findViewById(R.id.food_price_detail);
        food_description=(TextView)findViewById(R.id.food_description_detail);

        btn_cart=(FloatingActionButton)findViewById(R.id.btn_cart);
        btn_rating=(FloatingActionButton)findViewById(R.id.btn_rating);
        elegantNumberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(FoodDetails.this).addToCart(new OrderModel(
                        Food_Id,
                        foodModel.getName(),
                        elegantNumberButton.getNumber(),
                        foodModel.getPrice(),
                        foodModel.getDiscount()
                ));
                Toast.makeText(FoodDetails.this, "Added To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        //GetIntent
        if(getIntent()!=null)
        {
            Food_Id=getIntent().getStringExtra("FoodId");
        }
        if(!Food_Id.isEmpty() && Food_Id!=null)
        {
            if(Common.isConnectToInternet(getBaseContext())) {
                loadFood(Food_Id);
                RatingFood(Food_Id);

            }
            else
            {
                Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void RatingFood(String food_id) {
        Query foodRating=databaseReference_rating.orderByChild("foodId").equalTo(food_id);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Rating item=postSnapShot.getValue(Rating.class);
                    sum+= Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quick ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetails.this)
                .show();
    }

    private void loadFood(String food_id) {
        databaseReference.child(food_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodModel=dataSnapshot.getValue(FoodModel.class);

                //Set Image
                Picasso.with(getBaseContext()).load(foodModel.getImage()).into(img_food);
                food_name.setText(foodModel.getName());
                food_price.setText(foodModel.getPrice());
                food_description.setText(foodModel.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final Rating rating=new Rating(Common.currentuser.getPhone(),
                Food_Id,
                String.valueOf(i),
                s);
        databaseReference_rating.child(Common.currentuser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.currentuser.getPhone()).exists())
                {
                    //Remove old value
                    databaseReference_rating.child(Common.currentuser.getPhone()).removeValue();
                    //Update old value
                    databaseReference_rating.child(Common.currentuser.getPhone()).setValue(rating);
                }
                else
                {
                    //Update old value
                    databaseReference_rating.child(Common.currentuser.getPhone()).setValue(rating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
