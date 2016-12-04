package com.example.a.packthat;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class User implements Serializable{
    public static int Id;
    public static String Name, Email, Password, ProfileImg;
    public static Bitmap profileImageBitmap;

    public User(int id, String name, String email, String password, String profileImg){
        Id = id;
        Name = name;
        Email = email;
        Password = password;
        ProfileImg = profileImg;
        profileImageBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.default_profile);
    }
}
