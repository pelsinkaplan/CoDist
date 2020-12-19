package com.example.codist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends MainActivity{


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // kullanıcı giriş yapmamışsa login sayfasına gönder
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            changeActivity(MainActivity.getInstance().openLoginPage());
            finish();
        }

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        changeActivity(MainActivity.getInstance().openLoginPage());
        finish();
    }

    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }
}
