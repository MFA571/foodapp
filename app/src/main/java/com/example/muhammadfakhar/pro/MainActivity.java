package com.example.muhammadfakhar.pro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private EditText phoneET, passET;
    private FButton signIn, signUp;
    private ProgressBar progressBar;
    private String pass, phoneno;
    private CheckBox remCB; // by default it is checked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Log In");
        progressBar = findViewById(R.id.progressBar);
        phoneET = findViewById(R.id.etPhoneNo);
        passET = findViewById(R.id.etPassword);
        signIn = (findViewById(R.id.signin));
        signUp = (findViewById(R.id.signup));
        remCB = findViewById(R.id.rememberMeCB);
        signIn.setButtonColor(Color.parseColor("#ffffff"));
        signUp.setButtonColor(Color.parseColor("#ffffff"));

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference userTable = firebaseDatabase.getReference("User"); // from firebase

        // paper lib

        Paper.init(this);

        User aUser = Paper.book().read("User");
        if (aUser != null) {
            UserInstance.currUser = aUser;
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
        }

        //

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = passET.getText().toString();
                phoneno = phoneET.getText().toString();

                if (pass.isEmpty() || phoneno.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ConnectivityChangeReceiver.getInstance().isConnected(getApplicationContext())) {
                    progressBar.setVisibility(View.VISIBLE);
                    userTable.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (dataSnapshot.child(phoneno).exists()) {
                                User aUser = dataSnapshot.child(phoneno).getValue(User.class);
                                if (remCB.isChecked()) {
                                    Paper.book().write("User", aUser);
                                }
                                if (aUser.getPassword().equals(pass)) {
                                    aUser.setPhone(phoneno);
                                    UserInstance.currUser = aUser; // in order to use it in other activities, as no changes in it are required

                                    Intent intent = new Intent(MainActivity.this, Home.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "No User!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() { // no account
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

}
