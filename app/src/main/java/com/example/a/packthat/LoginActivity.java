package com.example.a.packthat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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

    //region REGISTRATION METHODS
    private void attemptRegister(String email, String password) {
        if(isEmailValid(email) && isPasswordValid(password)){
            getEmailFromDatabase(email, password);
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
        if(password.length() < 6){
            Toast.makeText(getApplicationContext(), "Password is too short, it must be at least 6 characters in length.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*\\d+.*")){
            Toast.makeText(getApplicationContext(), "Password must contain a number.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*[a-z]+.*")){
            Toast.makeText(getApplicationContext(), "Password must contain a lowercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*[A-Z]+.*")){
            Toast.makeText(getApplicationContext(), "Password must contain an uppercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void getEmailFromDatabase(final String enteredEmail, final String enteredPassword){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("email", enteredEmail);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectEmail.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String tempEmail = response.getString("Email");
                                if(!tempEmail.equalsIgnoreCase(enteredEmail)){
                                    registerUser(enteredEmail, enteredPassword);
                                }else{
                                    Toast.makeText(getApplicationContext(), "User already in use. Try logging in or use another email.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("LoginActivity", error.toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("LoginActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error creating new user account.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String email, String password){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            String tempName = email.substring(0, email.indexOf('@'));
            params.put("name", tempName);
            params.put("email", email);
            params.put("password", password);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addUser.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int tempId = response.getInt("Id");
                                String tempName = response.getString("Name");
                                String tempEmail = response.getString("Email");
                                String tempPassword = response.getString("Password");
                                String tempProfImg = response.getString("ProfileImg");
                                User tempUser = new User(tempId, tempName, tempEmail, tempPassword, tempProfImg);
                                sendToGame(tempUser);
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("LoginActivity", error.getStackTrace().toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("LoginActivity", e.getStackTrace().toString());
            Toast.makeText(getApplicationContext(), "Error creating new user account.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region LOGIN METHODS
    private void attemptLogin(String email, String password) {
        checkForUserInformation(email, password);
    }

    private void checkForUserInformation(final String enteredEmail, final String enteredPassword){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("email", enteredEmail);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectUser.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    String tempPassword = response.getString("Password");
                                    if(tempPassword.equals(enteredPassword)){
                                        int tempId = response.getInt("Id");
                                        String tempName = response.getString("Name");
                                        String tempEmail = response.getString("Email");
                                        String tempProfImg = response.getString("ProfileImg");
                                        User tempUser = new User(tempId, tempName, tempEmail, tempPassword, tempProfImg);
                                        sendToGame(tempUser);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Incorrect password! Please check your information and try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Unable to find a user with that email.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Unable to connect to database.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginActivity", error.toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("LoginActivity", e.toString());
        }
    }
    //endregion

    private void sendToGame(User user){
        Intent gameIntent = new Intent(this, MainActivity.class);
        gameIntent.putExtra("user", user);
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox_registering);
        if(checkBox.isChecked()){
            gameIntent.putExtra("newUser", "new");
        }else{
            gameIntent.putExtra("newUser", "false");
        }
        startActivity(gameIntent);
        finish();
    }
}

