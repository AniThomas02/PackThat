package com.example.a.packthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import layout.FriendsFragment;
import layout.HomeFragment;
import layout.ProfileFragment;

/**
 * The main pages of PackThat. Consists of 3 different "Pages"
 * Friends, profile, and a home page
 * Created by Ani Thomas on 11/1/2016.
 */
public class MainActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    public static User MY_USER;
    public static ArrayList<Friend> friendsList;
    public FriendsFragment friendsFragment;
    public ViewSwitcher imageSwitcher, nameSwitcher, emailSwitcher;
    private static final int RESULT_LOAD_IMAGE = 1;
    Bitmap newPhoto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent gameIntent = getIntent();
        MY_USER = (User) gameIntent.getSerializableExtra("user");
        String newUser = gameIntent.getStringExtra("newUser");

        friendsList = new ArrayList<>();
        getFriends();

        setContentView(R.layout.activity_home);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        if(newUser.equals("new")){
            vpPager.setCurrentItem(1);
        }else{
            vpPager.setCurrentItem(2);
        }
    }

    public void getFriends(){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("id", User.Id);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectAllFriends.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    JSONArray friendResonse = response.getJSONArray("array");
                                    Friend getFriend;
                                    for(int i =0; i < friendResonse.length(); i++){
                                        JSONObject row = friendResonse.getJSONObject(i);
                                        getFriend = new Friend(row.getInt("id"), row.getString("name"), row.getString("email"), row.getString("profileImg"));
                                        friendsList.add(getFriend);
                                    }
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("MainActivity", error.toString());
                            System.out.println("Error");
                            Toast.makeText(getApplicationContext(), "Error getting friends.", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("MainActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error Accessing DB.", Toast.LENGTH_SHORT).show();
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - Friends
                    return FriendsFragment.newInstance(0, "Friends" , friendsList);
                case 1: // Fragment # 1 - Profile
                    return ProfileFragment.newInstance(1, "My Profile");
                default:// Fragment # 2 - Home
                    return HomeFragment.newInstance(2, "Home");
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    //region FRIENDS
    public void displayAddFriendDialog(View view){
        LayoutInflater inflater = getLayoutInflater();
        View addFriendLayout = inflater.inflate(R.layout.dialog_add_friend, null);
        final EditText addFriendEmail = (EditText) addFriendLayout.findViewById(R.id.editText_add_friend_email);
        final Button addFriend = (Button) addFriendLayout.findViewById(R.id.button_add_friend);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Friend");
        alert.setView(addFriendLayout);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFriendEmail = addFriendEmail.getText().toString();
                if(isValidEmail(newFriendEmail)){
                    addFriend(newFriendEmail);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private boolean isValidEmail(String email) {
        if (!email.equals("")) {
            if (email.matches("^([A-Z|a-z|0-9](\\.|_){0,1})+[A-Z|a-z|0-9]\\@([A-Z|a-z|0-9])+((\\.){0,1}[A-Z|a-z|0-9]){2}\\.[a-z]{2,3}$")) {
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "Email must be in a proper emailing format (e.g: email@email.com)", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "You need to add a friend's email.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void addFriend(String friendEmail){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("email", friendEmail);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectFriend.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                if(response != null){
                                    int friendId = response.getInt("Id");
                                    String friendName = response.getString("Name");
                                    String friendEmail = response.getString("Email");
                                    String friendImg = response.getString("ProfileImg");
                                    Friend newFriend = new Friend(friendId, friendName, friendEmail,friendImg);
                                    addFriendToDatabase(newFriend);
                                }else{
                                    Toast.makeText(getApplicationContext(), "No user has that email address.", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Error finding friend", Toast.LENGTH_SHORT).show();
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

    public void addFriendToDatabase(final Friend newFriend){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userId", User.Id);
            params.put("friendId", newFriend.friendId);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addFriend.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                if(response != null){
                                    int affected = response.getInt("affected");
                                    if(affected == 1){
                                        friendsList.add(newFriend);
                                        adapterViewPager.notifyDataSetChanged();
                                    }
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Error finding friend", Toast.LENGTH_SHORT).show();
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

    //region PROFILE
    public void switchToImageUpload(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra("crop", true);
        galleryIntent.putExtra("outputX", 150);
        galleryIntent.putExtra("outputY", 150);
        galleryIntent.putExtra("aspectX", 1);
        galleryIntent.putExtra("aspectY", 1);
        galleryIntent.putExtra("scale", true);
        galleryIntent.putExtra("return-data", true);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    public void addNewProfileImage(View view){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //compress image into output
            newPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            //encode image as a string
            final String newImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addProfileImage.php";
            StringRequest stringRequest = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if(response.equals("1")){
                                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                                    User.ProfileImg = User.Id + ".jpg";
                                    User.profileImageBitmap = newPhoto;
                                    imageSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher_profile_image);
                                    imageSwitcher.showPrevious();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception ex) {
                                Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", error.toString());
                            System.out.println("Error");
                        }
                    }){
                protected Map<String, String> getParams(){
                    //Creating post arguments to send to the php page
                    Map<String,String> params = new HashMap<>();
                    params.put("newImage", newImage);
                    params.put("userId", User.Id + "");
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }catch (Exception e){
            Log.i("MainActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error adding new profile image to database.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelNewProfileImage(View view){
        ImageView profImg = (ImageView)findViewById(R.id.imageView_profile);
        profImg.setImageBitmap(User.profileImageBitmap);
        imageSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher_profile_image);
        imageSwitcher.showPrevious();
    }

    public void editProfileInfo(View view){
        //get all current variables
        Button button = (Button)findViewById(R.id.button_edit_or_save);
        nameSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher_name);
        emailSwitcher = (ViewSwitcher)findViewById(R.id.viewswitcher_email);

        TextView viewName = (TextView)findViewById(R.id.textView_name);
        EditText editName = (EditText)findViewById(R.id.editText_name);
        TextView viewEmail = (TextView)findViewById(R.id.textView_email);
        EditText editEmail = (EditText)findViewById(R.id.editText_email);

        //check if they are editing or saving
        if(button.getText().equals("Edit Information")){
            //change the edit texts to views
            editName.setText(viewName.getText());
            editEmail.setText(viewEmail.getText());
            //switch the views
            nameSwitcher.showNext();
            emailSwitcher.showNext();
            //request focus on the first entry
            editName.requestFocus();
            //change the button
            button.setText("Save Information");
        }else{
            String editNameS = editName.getText().toString();
            String editEmailS = editEmail.getText().toString();
            if(!editNameS.equals("")){
                if(!editEmailS.equals("")){
                    if(editEmailS.matches("^([A-Z|a-z|0-9](\\.|_){0,1})+[A-Z|a-z|0-9]\\@([A-Z|a-z|0-9])+((\\.){0,1}[A-Z|a-z|0-9]){2}\\.[a-z]{2,3}$")){
                        if(!editNameS.equals(User.Name) || !editEmailS.equals(User.Email) ){
                            updateUserInformation(editNameS, editEmailS);
                        }else{
                            switchInfoToView();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Email must be in proper email format.", Toast.LENGTH_SHORT).show();
                        editEmail.requestFocus();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Email cannot be blank to be save.", Toast.LENGTH_SHORT).show();
                    editEmail.requestFocus();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Name cannot be blank to be save.", Toast.LENGTH_SHORT).show();
                editName.requestFocus();
            }
        }
    }

    public void switchInfoToView(){
        //grab profileInfo
        TextView viewName = (TextView)findViewById(R.id.textView_name);
        EditText editName = (EditText)findViewById(R.id.editText_name);
        TextView viewEmail = (TextView)findViewById(R.id.textView_email);
        EditText editEmail = (EditText)findViewById(R.id.editText_email);
        Button button = (Button)findViewById(R.id.button_edit_or_save);
        //switch back to viewInfo
        viewName.setText(editName.getText());
        viewEmail.setText(editEmail.getText());
        nameSwitcher.showPrevious();
        emailSwitcher.showPrevious();
        button.setText("Edit Information");
    }

    public void updateUserInformation(final String newName, final String newEmail){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("name", newName);
            params.put("email", newEmail);
            params.put("id", User.Id);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/updateUserInfo.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int tempId = response.getInt("Id");
                                if(tempId >= 1){
                                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                                    User.Name = newName;
                                    User.Email = newEmail;
                                    switchInfoToView();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("MainActivity", error.toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("MainActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error updating userInfo.", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayPasswordPopup(View view){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_change_password, null);
        final EditText oldPass = (EditText) alertLayout.findViewById(R.id.editText_old_pass);
        final EditText newPass = (EditText) alertLayout.findViewById(R.id.editText_new_pass);
        final EditText newPass2 = (EditText) alertLayout.findViewById(R.id.editText_new_pass2);
        Button password = (Button) alertLayout.findViewById(R.id.button_change_password);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change Password");
        alert.setView(alertLayout);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassS = oldPass.getText().toString();
                String newPassS = newPass.getText().toString();
                String newPass2S = newPass2.getText().toString();
                if(!oldPassS.equals("") && !newPassS.equals("") && !newPass2S.equals("")){
                    if(oldPassS.equals(User.Password)){
                        if(newPassS.equals(newPass2S)){
                            if(isNewPasswordValid(newPassS)){
                                updatePassword(newPassS);
                                dialog.dismiss();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Old password is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Make sure all entries are filled out.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private boolean isNewPasswordValid(String password) {
        if(password.length() < 6){
            Toast.makeText(getApplicationContext(), "New password is too short, it must be at least 6 characters in length.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*\\d+.*")){
            Toast.makeText(getApplicationContext(), "New password must contain a number.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*[a-z]+.*")){
            Toast.makeText(getApplicationContext(), "New password must contain a lowercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!password.matches(".*[A-Z]+.*")){
            Toast.makeText(getApplicationContext(), "New password must contain an uppercase letter.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public void updatePassword(final String newPass){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("password", newPass);
            params.put("id", User.Id);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/updateUserPassword.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int tempId = response.getInt("Id");
                                if(tempId >= 1){
                                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                                    User.Password = newPass;
                                }else{
                                    Toast.makeText(getApplicationContext(), "Error saving.", Toast.LENGTH_SHORT).show();
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("MainActivity", error.toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("MainActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error updating userInfo.", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    //region HOME PAGE
    public void displayViewEventPopup(View view){
        LayoutInflater inflater = getLayoutInflater();
        View viewEventLayout = inflater.inflate(R.layout.dialog_add_event, null);
        Button manageEvent = (Button) viewEventLayout.findViewById(R.id.button_manage_event);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("View Event");
        alertDialog.setView(viewEventLayout);
        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();
        manageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void displayPersonalEvent(View view){
        displayCreateEventView(0);
    }

    public void displayGroupEvent(View view){
        displayCreateEventView(1);
    }

    //0 is personal, 1 is private
    public void displayCreateEventView(int eventType){
        LayoutInflater inflater = getLayoutInflater();
        View createEventLayout = inflater.inflate(R.layout.dialog_add_event, null);
        final EditText createName = (EditText) createEventLayout.findViewById(R.id.editText_create_name);
        final EditText createDescription = (EditText) createEventLayout.findViewById(R.id.editText_create_description);
        final DatePicker createStartDate = (DatePicker) createEventLayout.findViewById(R.id.date_start);
        Button createEvent = (Button) createEventLayout.findViewById(R.id.button_create_event);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Create Event");
        alertDialog.setView(createEventLayout);
        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = createName.getText().toString();
                String eventDescription = createDescription.getText().toString();
                if(!eventName.equals("")){
                    if(!eventDescription.equals("")) {
                        //if(createStartDate.getDateFrom() != null){

                        //}
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(), "The event must have a description.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "The event has no name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            if(extras != null){
                newPhoto = extras.getParcelable("data");
                ImageView profileImage = (ImageView)findViewById(R.id.imageView_profile);
                profileImage.setImageBitmap(null);
                profileImage.setImageBitmap(newPhoto);
            }

            //switch over to my change image button
            imageSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher_profile_image);
            if(!imageSwitcher.getCurrentView().equals(findViewById(R.id.view_image2))){
                imageSwitcher.showNext();
            }
        }
    }
}
