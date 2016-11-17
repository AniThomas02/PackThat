package com.example.a.packthat;

import java.io.Serializable;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class User implements Serializable{
    public static int Id;
    public static String Name, Username, Email, Password, ProfileImg;

    public User(int id, String name, String username, String email, String password, String profileImg){
        Id = id;
        Name = name;
        Username = username;
        Email = email;
        Password = password;
        ProfileImg = profileImg;
    }
}
