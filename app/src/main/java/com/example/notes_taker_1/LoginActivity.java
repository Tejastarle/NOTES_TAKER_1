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

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginbtn;
    ProgressBar pb;
    TextView createacc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        loginbtn = findViewById(R.id.login_btn);
        pb = findViewById(R.id.progress_bar);
        createacc = findViewById(R.id.create_account_text_view_btn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
            void loginUser(){
                String email1 = email.getText().toString();
                String password1 = password.getText().toString();

                boolean isvalidated = validateDate(email1,password1);
                if(!isvalidated){
                    return;
                }
                loginAccountInFirebase(email1,password1);
            }

            void loginAccountInFirebase(String email1,String password1){
                changeInProgress(true);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //login successful
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                //go to main activity
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Email not verified,Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            // login failed
                            Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            boolean validateDate(String email1,String password1){
                //validate the data that are input by the user
                if(!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
                    email.setError("Email is invalid");
                    return true;
                }
                if(password1.length()<6){
                    password.setError("password length is invalid");
                    return false;
                }
                return true;
            }

            void changeInProgress(boolean inProgress) {
                if (inProgress) {
                    pb.setVisibility(View.VISIBLE);
                    loginbtn.setVisibility(View.GONE);
                }else{
                    pb.setVisibility(View.GONE);
                    loginbtn.setVisibility(View.VISIBLE);
                }
            }
        });

        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });

    }
}