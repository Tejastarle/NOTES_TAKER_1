package com.example.notes_taker_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
EditText email,password,confirmpass;
Button createaccount;
ProgressBar pb;
TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirmpass = findViewById(R.id.confirm_password_edit_text);
        createaccount = findViewById(R.id.create_account_btn);
        pb = findViewById(R.id.progress_bar);
        login = findViewById(R.id.login_text_view_btn);

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = email.getText().toString();
                String password1 = password.getText().toString();
                String confirmpass1 = confirmpass.getText().toString();

                boolean isvalidated = validateDate(email1,password1,confirmpass1);
                if(!isvalidated){
                    return;
                }
                createAccountInFirebase(email1,password1);

            }

            boolean validateDate(String email1,String password1,String confimpass1){
                //validate the data that are input by the user
                if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
                    email.setError("Email is invalid");
                    return false;
                }
                if(password1.length()<6){
                    password.setError("password length is invalid");
                    return false;
                }
                if(!password1.equals(confimpass1)){
                    confirmpass.setError("Password not matched");
                    return false;
                }
                return true;
            }

            void createAccountInFirebase(String email,String password){
                changeInProgress(true);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                changeInProgress(false);
                                if(task.isSuccessful()){
                                    //creating account is done
                                    Toast.makeText(CreateAccountActivity.this, "successfully account created,Check email to verify", Toast.LENGTH_SHORT).show();
                                    firebaseAuth.getCurrentUser().sendEmailVerification();
                                    firebaseAuth.signOut();
                                    finish();
                                }else {
                                    //failure
                                    Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            void changeInProgress(boolean inProgress) {
                if (inProgress) {
                    pb.setVisibility(View.VISIBLE);
                    createaccount.setVisibility(View.GONE);
                }else{
                    pb.setVisibility(View.GONE);
                    createaccount.setVisibility(View.VISIBLE);
                }
            }


        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            }
        });
    }
}