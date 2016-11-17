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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
            try {
                //make new user
                //AddUserTask addUserTask = new AddUserTask();
                //addUserTask.execute(email, password);
                //addUserTask.get();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject params = new JSONObject();
                params.put("name", "Enter Name");
                String tempUsername = email.substring(0, email.indexOf('@'));
                params.put("username", tempUsername);
                params.put("email", email);
                params.put("password", password);
                params.put("profileImg", "Default");
                String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addUser.php";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int tempId = response.getInt("Id");
                                    String tempName = response.getString("Name");
                                    String tempUsername = response.getString("Username");
                                    String tempEmail = response.getString("Email");
                                    String tempPassword = response.getString("Password");
                                    String tempProfImg = response.getString("ProfileImg");
                                    User tempUser = new User(tempId, tempName, tempUsername, tempEmail, tempPassword, tempProfImg);
                                    sendToGame(tempUser);
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

                //User currUser = new User(0, "Tester", "Testing", email, password, "Default");
                //send user to game
            }catch (Exception e){
                Log.i("LoginActivity", e.toString());
                Toast.makeText(getApplicationContext(), "Error creating new user account.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void attemptLogin(String email, String password) {
        if(email.equals("test@test.com") && password.equals("Test1")){
            User currUser = new User(0, "TestName", "TestUsername", email, password, "Default");
            sendToGame(currUser);
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

    private void sendToGame(User user){
        Intent gameIntent = new Intent(this, MainActivity.class);
        gameIntent.putExtra("user", user);
        startActivity(gameIntent);
        finish();
    }
}

