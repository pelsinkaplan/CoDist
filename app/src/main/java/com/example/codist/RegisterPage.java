package com.example.codist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codist.MainActivity;
import com.example.codist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        registerButton = (Button) findViewById(R.id.registerReg);
        backButton = (Button) findViewById(R.id.backReg);
        emailEdit = (EditText) findViewById(R.id.emailReg);
        passwordEdit = (EditText) findViewById(R.id.passwordReg);
        nameEdit = (EditText) findViewById(R.id.nameReg);
        surnameEdit = (EditText) findViewById(R.id.surnameReg);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Toast.makeText(RegisterPage.this, "User Already Logged In.", Toast.LENGTH_SHORT).show();
            changeActivity(MainActivity.getInstance().openHomePage());
            finish(); //
        }

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
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterPage.this, "User Created.", Toast.LENGTH_SHORT).show();
                                changeActivity(MainActivity.getInstance().openLoginPage());
                            }else {
                                Toast.makeText(RegisterPage.this, "ERROR! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
