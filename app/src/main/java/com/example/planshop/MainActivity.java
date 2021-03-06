package com.example.planshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference reff;
    private String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signUp(View view) {
        Intent sign = new Intent(this, signUp.class);
        startActivity(sign);
    }

    public void logIn(View view) {
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        txtEmail = findViewById(R.id.editMainEmail);
        txtPassword = findViewById(R.id.editMainPass);

        final String email, password;
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();


        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter email",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    reff = FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(task.getResult().getUser().getUid());

                                    signInSuccessful();

                                } else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                            "Login failed!!",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
    }


    private void signInSuccessful() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user.getAdmin()) {
                    Toast.makeText(getApplicationContext(), "Hello ADMIN", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, ActivitiesMenu.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, MemberEventList.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        };
        reff.addListenerForSingleValueEvent(userListener);

    }
}
