package com.example.a.packthat;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class AddUserTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... params){
        String name = "None";
        String username = params[0].substring(0, params[0].indexOf('@'));
        String link = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addUser.php?name=" + name
                + "&username=" + username + "&email=" + params[0] + "&password=" + params[1] + "&profileImg=Default";

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
        }catch (Exception e){
            return "Exception: " + e.getMessage();
        }
        return "success";
    }
}
