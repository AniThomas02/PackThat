package com.example.a.packthat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import layout.PrivateListFragment;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class PrivateEventActivity extends AppCompatActivity{
    private PrivateListFragment privateListFragment;
    private Event currentEvent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent privateEventIntent = getIntent();
        currentEvent = (Event) privateEventIntent.getSerializableExtra("event");

        setContentView(R.layout.activity_private_event);

        privateListFragment = new PrivateListFragment();
        privateListFragment.setArguments(getIntent().getExtras());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.layout_private_event, privateListFragment);
        fragmentTransaction.commit();
    }

}
