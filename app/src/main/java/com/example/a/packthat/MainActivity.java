package com.example.a.packthat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import layout.FriendsFragment;
import layout.HomeFragment;
import layout.ProfileFragment;

/**
 * Created by Ani Thomas on 11/1/2016.
 */
public class MainActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    public static User MY_USER;
    public ViewSwitcher nameSwitcher, usernameSwitcher, emailSwitcher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent gameIntent = getIntent();
        MY_USER = (User) gameIntent.getSerializableExtra("user");

        setContentView(R.layout.activity_home);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.setCurrentItem(1);
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
                    return FriendsFragment.newInstance(0, "Friends");
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

    public void editProfileInfo(View view){
        Button button = (Button)findViewById(R.id.button_edit_or_save);
        nameSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher_name);
        usernameSwitcher = (ViewSwitcher)findViewById(R.id.viewswitcher_Username);
        emailSwitcher = (ViewSwitcher)findViewById(R.id.viewswitcher_email);

        if(button.getText().equals("Edit Information")){
            //prep name
            TextView name = (TextView)findViewById(R.id.textView_name);
            EditText editName = (EditText)findViewById(R.id.editText_name);
            editName.setText(name.getText());
            //prep username
            TextView username = (TextView)findViewById(R.id.textView_username);
            EditText editUserName = (EditText)findViewById(R.id.editText_username);
            editUserName.setText(username.getText());
            //prep email
            TextView email = (TextView)findViewById(R.id.textView_email);
            EditText editEmail = (EditText)findViewById(R.id.editText_email);
            editEmail.setText(email.getText());
            //switch the views
            nameSwitcher.showNext();
            usernameSwitcher.showNext();
            emailSwitcher.showNext();
            //change the button
            button.setText("Save Information");
        }else{
            nameSwitcher.showPrevious();
            usernameSwitcher.showPrevious();
            emailSwitcher.showPrevious();
            button.setText("Edit Information");
        }
    }

    public void displayPasswordPopup(View view){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_change_password, null);
        //EditText oldPass = (EditText) alertLayout.findViewById(R.id.editText_old_pass);
        //EditText newPass = (EditText) alertLayout.findViewById(R.id.editText_new_pass);
        //EditText newPass2 = (EditText) alertLayout.findViewById(R.id.editText_new_pass2);
        Button password = (Button) alertLayout.findViewById(R.id.button_change_password);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change Password");
        alert.setView(alertLayout);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void displayCreateEventView(View view){
        LayoutInflater inflater = getLayoutInflater();
        View createEventLayout = inflater.inflate(R.layout.dialog_create_event, null);
        final EditText createName = (EditText) createEventLayout.findViewById(R.id.editText_create_name);
        Button createEvent = (Button) createEventLayout.findViewById(R.id.button_create_event);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Create Event");
        alertDialog.setView(createEventLayout);
        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = createName.getText().toString();
                if(!sName.equals("")){
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), "Field no set.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void displayViewEventPopup(View view){
        LayoutInflater inflater = getLayoutInflater();
        View viewEventLayout = inflater.inflate(R.layout.dialog_create_event, null);
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


}
