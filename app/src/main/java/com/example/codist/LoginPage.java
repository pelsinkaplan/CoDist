package com.example.codist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codist.MainActivity;
import com.example.codist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends MainActivity {
    private Button loginButton;
    private TextView registerButton;
    private EditText emailEdit;
    private EditText passwordEdit;
    private String email;
    private String password;
    private AppCompatActivity act;
    FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        loginButton = (Button) findViewById(R.id.login);
        registerButton = (TextView) findViewById(R.id.register);
        emailEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            changeActivity(MainActivity.getInstance().openHomePage());
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.getInstance().openRegisterPage());
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();

                //name Check
                if (MainActivity.getInstance().controlEmailRegister(email).length() != 0) {
                    emailEdit.setError(MainActivity.getInstance().controlNameRegister(email));
                } else if (MainActivity.getInstance().controlPasswordRegister(password).length() != 0) {
                    passwordEdit.setError(MainActivity.getInstance().controlPasswordRegister(password));
                }else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginPage.this, "Succesfully Signed In.", Toast.LENGTH_SHORT).show();
                                changeActivity(MainActivity.getInstance().openHomePage());
                                finish();
                            }else {
                                Toast.makeText(LoginPage.this, "ERROR! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }
}
