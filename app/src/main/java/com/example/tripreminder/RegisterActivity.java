package com.example.tripreminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText user_name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private Button register;
    private TextView login_btn;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user_name = findViewById(R.id.input_username);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        confirm_password = findViewById(R.id.input_confirm_password);
        register = findViewById(R.id.btn_register);
        login_btn = findViewById(R.id.have_account);
        fauth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String confirmPassword = confirm_password.getText().toString().trim();
                if (TextUtils.isEmpty(Email)) {
                    email.setError("email is required");
                    return;
                } else if (TextUtils.isEmpty(Password) || TextUtils.isEmpty(confirmPassword)) {
                    password.setError("password is required");
                    confirm_password.setError("password is required");
                    return;
                } else if (Password.length() < 6) {
                    password.setError("password must be >6 character");
                    return;
                } else {
                    if (Password.equals(confirmPassword)){
                        fauth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    fauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Verify Account Then Log In" , Toast.LENGTH_SHORT).show();
                                                fauth.signOut();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {
                        confirm_password.setError("Confirmation Password Not Match With Password");
                    }
                }


            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }
}