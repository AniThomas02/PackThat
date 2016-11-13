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
            sendToGame(email, "newUser");
        }
    }

    private void attemptLogin(String email, String password) {
        if(email.equals("test@test.com") && password.equals("Test1")){
            sendToGame(email, "oldUser");
        }else {
            Toast.makeText(getApplicationContext(), "Could not find email and password with that name.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        if(email.matches("^([A-Z|a-z|0-9](\\.|_){0,1})+[A-Z|a-z|0-9]\\@([A-Z|a-z|0-9])+((\\.){0,1}[A-Z|a-z|0-9]){2}\\.[a-z]{2,3}$")) {
            return true;
        }else{
            Toast.makeText(getApplicationContext(), "Email must be in a proper emailing format (e.g: email@email.com)", Toast.LENGTH_SHORT).show();
            return false;
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

    private void sendToGame(String email, String userType){
        Intent gameIntent = new Intent(this, MainActivity.class);
        gameIntent.putExtra("email", email);
        gameIntent.putExtra("user", userType);
        startActivity(gameIntent);
        finish();
    }
}

