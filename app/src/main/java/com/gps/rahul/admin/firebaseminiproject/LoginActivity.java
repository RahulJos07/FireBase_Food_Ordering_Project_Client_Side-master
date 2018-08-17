package com.gps.rahul.admin.firebaseminiproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Model.LoginModel;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText edt_mobile_number,edt_password;
    Button btn_submit;
    TextView txt_SignUp;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    TextView txt_forgotpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_mobile_number=(EditText)findViewById(R.id.edt_mobile_number);
        edt_password=(EditText)findViewById(R.id.edt_password);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        txt_forgotpassword=(TextView)findViewById(R.id.txt_forgotpassword);
        // Init Paper
        Paper.init(this);
        databaseReference= FirebaseDatabase.getInstance().getReference("User");
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loginsubmit();
            }
        });
        txt_SignUp=(TextView)findViewById(R.id.txt_SignUp);
        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        txt_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shorForgotPwdDialog();
            }
        });
    }

    private void shorForgotPwdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");
        LayoutInflater inflater=this.getLayoutInflater();
        View forgot_View=inflater.inflate(R.layout.forgot_password,null);
        builder.setView(forgot_View);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        final EditText edt_mobile_number_forgot=(EditText)forgot_View.findViewById(R.id.edt_mobile_number_forgot);
        final EditText edt_secure_code_forgot=(EditText)forgot_View.findViewById(R.id.edt_secure_code_forgot);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LoginModel loginModel=dataSnapshot.child(edt_mobile_number_forgot.getText().toString())
                                .getValue(LoginModel.class);
                        if(loginModel.getSecureCode().equals(edt_secure_code_forgot.getText().toString()))
                            Toast.makeText(LoginActivity.this, "Your Password : "+loginModel.getPassword(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this, "Wrong secure code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void Loginsubmit() {
        final String m_no=edt_mobile_number.getText().toString();
        final String pwd=edt_password.getText().toString();
        if(Common.isConnectToInternet(getBaseContext())) {

            if(m_no.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Mobile Number", Toast.LENGTH_SHORT).show();
            }
            else if(pwd.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Please Waiting");
                progressDialog.show();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if user not exits in database
                        if (dataSnapshot.child(m_no).exists()) {
                            progressDialog.dismiss();
                            //Get User Information
                            LoginModel loginModel = dataSnapshot.child(m_no).getValue(LoginModel.class);
                            loginModel.setPhone(m_no); //Set Phone
                            if (loginModel.getPassword().equalsIgnoreCase(pwd)) {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                edt_mobile_number.setText("");
                                edt_password.setText("");
                                Intent i = new Intent(LoginActivity.this, Home_Page_Navigation.class);
                                Common.currentuser = loginModel;
                                startActivity(i);
                                databaseReference.removeEventListener(this);
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "User Not Exits In Database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else
        {
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
