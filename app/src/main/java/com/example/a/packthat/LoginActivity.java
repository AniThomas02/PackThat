package com.example.a.packthat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onCheckboxClicked(View view) {
        if(((CheckBox) view).isChecked()){
            ((Button) findViewById(R.id.button_login)).setText(R.string.checkbox_register_text);
        }else{
            ((Button) findViewById(R.id.button_login)).setText(R.string.checkbox_login_text);
        }
    }

    public void registerOrLogin(View view){
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox_registering);
        String email = ((EditText)findViewById(R.id.editText_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.editText_password)).getText().toString();
        if(checkBox.isChecked()){
            attemptRegister(email, password);
        }else{
            attemptLogin(email, password);
        }
    }

    private void attemptRegister(String email, String password) {
        if(isEmailValid(email) && isPasswordValid(password)){
            sendToGame(email);
        }
    }

    private void attemptLogin(String email, String password) {
        if(email.equals("Test@test.com") && password.equals("Test1")){
            sendToGame(email);
        }
    }

    private boolean isEmailValid(String email) {
        if(email.length() < 4) {
            Toast.makeText(getApplicationContext(), "Email must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        if(password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{6,}$")){
            return true;
        }else{
            Toast.makeText(getApplicationContext(), "Error with password. Check password requirements and try again.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sendToGame(String email){
        Intent gameIntent = new Intent(this, MainActivity.class);
        gameIntent.putExtra("email", email);
        startActivity(gameIntent);
        finish();
    }
}

