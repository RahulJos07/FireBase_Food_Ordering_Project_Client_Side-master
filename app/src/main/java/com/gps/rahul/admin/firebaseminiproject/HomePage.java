package com.gps.rahul.admin.firebaseminiproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {
    String name;
    TextView txt_name_home_page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        name=getIntent().getStringExtra("name");
        //Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
        txt_name_home_page=(TextView)findViewById(R.id.txt_name_home_page);
        txt_name_home_page.setText("Welcome : "+name);
    }
}
