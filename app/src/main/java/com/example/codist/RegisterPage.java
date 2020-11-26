package com.example.codist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codist.MainActivity;
import com.example.codist.R;

public class RegisterPage extends MainActivity {
    private Button registerButton;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private EditText surnameEdit;
    private Button backButton;
    private String name;
    private String surname;
    private String email;
    private String password;
    private AppCompatActivity act;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        registerButton = (Button) findViewById(R.id.registerReg);
        backButton = (Button) findViewById(R.id.backReg);
        emailEdit = (EditText) findViewById(R.id.emailReg);
        passwordEdit = (EditText) findViewById(R.id.passwordReg);
        nameEdit = (EditText) findViewById(R.id.nameReg);
        surnameEdit = (EditText) findViewById(R.id.surnameReg);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(MainActivity.getInstance().openLoginPage());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEdit.getText().toString();
                surname = surnameEdit.getText().toString();
                email = emailEdit.getText().toString();
                password = passwordEdit.getText().toString();


                int counter = 0;
                //name Check
                if (MainActivity.getInstance().controlNameRegister(name).length() == 0) {
                    counter++;
                } else {
                    nameEdit.setError(MainActivity.getInstance().controlNameRegister(name));
                }
                //surname Check
                if (MainActivity.getInstance().controlSurnameRegister(surname).length() == 0) {
                    counter++;
                } else {
                    surnameEdit.setError(MainActivity.getInstance().controlSurnameRegister(surname));
                }
                if (MainActivity.getInstance().controlEmailRegister(email).length() == 0) {
                    counter++;
                } else {
                    emailEdit.setError(MainActivity.getInstance().controlEmailRegister(email));
                }
                if (MainActivity.getInstance().controlPasswordRegister(password).length() == 0) {
                    counter++;
                } else {
                    passwordEdit.setError(MainActivity.getInstance().controlPasswordRegister(password));
                }
                if (counter == 4) {
                    changeActivity(MainActivity.getInstance().openLoginPage());
                }


            }
        });
    }


    public void changeActivity(Class className) {
        startActivity(new Intent(this, className));
    }
}
