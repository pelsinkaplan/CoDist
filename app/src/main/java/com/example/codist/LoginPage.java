package com.example.codist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codist.MainActivity;
import com.example.codist.R;

public class LoginPage extends MainActivity {
    private Button loginButton;
    private TextView registerButton;
    private EditText emailEdit;
    private EditText passwordEdit;
    private String email;
    private String password;
    private AppCompatActivity act;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        loginButton = (Button) findViewById(R.id.login);
        registerButton = (TextView) findViewById(R.id.register);
        emailEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.getInstance().openRegisterPage());
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.getInstance().openHomePage());
            }
        });
    }

    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }
}
