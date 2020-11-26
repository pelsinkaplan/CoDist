package com.example.codist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.example.codist.HomePage;
import com.example.codist.RegisterPage;
import com.example.codist.LoginPage;

public class MainActivity extends AppCompatActivity {
    private final static MainActivity instance = new MainActivity();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    /******************open pages methods*****************/
    public Class openRegisterPage() {
        return RegisterPage.class;
    }

    public Class openHomePage() {
        return HomePage.class;
    }

    public Class openLoginPage() {
        return LoginPage.class;
    }

    /***************check user methods***************/
    public String controlNameRegister(String name) {
        if (name.length() == 0) {
            return "Lütfen isminizi girin!";
        }
        for (int i = 0; i < name.length(); i++) {
            if ((int) (name.toUpperCase().charAt(i)) > 90 || (int) (name.toUpperCase().charAt(i)) < 65) {
                return "İsim sadece alfabetik karakterlerden oluşabilir!";
            }
        }
        return "";
    }

    public String controlSurnameRegister(String surname) {
        if (surname.length() == 0) {
            return "Lütfen soyadınızı girin!";
        }
        for (int i = 0; i < surname.length(); i++) {
            if ((int) (surname.toUpperCase().charAt(i)) > 90 || (int) (surname.toUpperCase().charAt(i)) < 65) {
                return "İsim sadece alfabetik karakterlerden oluşabilir!";
            }
        }
        return "";
    }

    public String controlEmailRegister(String email) {
        if (email.length() == 0) {
            return "Lütfen e-posta adresinizi girin!";
        }
        boolean statement1 = false;
        boolean statement2 = false;
        int index1 = 0;
        int index2 = 0;
        for (int i = 1; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                statement1 = true;
                index1 = i + 1;
                break;
            }
        }
        for (int i = index1; i < email.length(); i++) {
            if (email.charAt(i) == '.') {
                statement2 = true;
                break;
            }
        }
        if (!statement1 || !statement2) {
            return "Lütfen geçerli bir e-posta adresi girin!";
        } else {
            return "";
        }
    }

    public String controlPasswordRegister(String password) {
        if (password.length() == 0) {
            return "Lütfen şifrenizi girin!";
        }
        if (password.length() < 6) {
            return "Lütfen en az 6 karakterden oluşan bir şifre girin!";
        }
        return "";
    }

}

