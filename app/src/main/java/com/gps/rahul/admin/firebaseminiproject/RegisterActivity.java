package com.gps.rahul.admin.firebaseminiproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.rahul.admin.firebaseminiproject.Common.Common;
import com.gps.rahul.admin.firebaseminiproject.Model.LoginModel;

public class RegisterActivity extends AppCompatActivity {
    EditText edt_name_register,edt_mobile_number_register,edt_password_register,edt_code_register;
    Button btn_submit_register;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference= FirebaseDatabase.getInstance().getReference("User");
        edt_name_register=(EditText)findViewById(R.id.edt_name_register);
        edt_password_register=(EditText)findViewById(R.id.edt_password_register);
        edt_mobile_number_register=(EditText)findViewById(R.id.edt_mobile_number_register);
        edt_code_register=(EditText)findViewById(R.id.edt_code_register);
        btn_submit_register=(Button)findViewById(R.id.btn_submit_register);
        btn_submit_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterSubmit();
            }
        });
    }

    private void RegisterSubmit() {
        final String name=edt_name_register.getText().toString();
        final String m_no=edt_mobile_number_register.getText().toString();
        final String pwd=edt_password_register.getText().toString();
        final String s_code=edt_code_register.getText().toString();

        if(Common.isConnectToInternet(getBaseContext())) {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Please Waiting");
            progressDialog.show();
            if(name.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Name", Toast.LENGTH_SHORT).show();
            }
            else if(m_no.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Mobile Number", Toast.LENGTH_SHORT).show();
            }
            else if(pwd.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
            }
            else if(s_code.isEmpty())
            {
                Toast.makeText(this, "Please Enter the Secure Code", Toast.LENGTH_SHORT).show();
            }
            else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(m_no).exists()) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Mobile Number Already Register", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            LoginModel loginModel = new LoginModel(name, pwd, s_code);
                            databaseReference.child(m_no).setValue(loginModel);
                            Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            finish();
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
