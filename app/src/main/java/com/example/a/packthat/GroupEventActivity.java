package com.example.a.packthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import layout.ChatLogFragment;
import layout.FriendsFragment;
import layout.GroupListFragment;
import layout.PrivateListFragment;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class GroupEventActivity extends AppCompatActivity{
    FragmentPagerAdapter gEAdapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_event);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        gEAdapterViewPager = new GroupEventPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(gEAdapterViewPager);

        vpPager.setCurrentItem(1);
    }

    public static class GroupEventPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public GroupEventPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment #0 - Group Chat
                    return ChatLogFragment.newInstance(0, "ChatLog");
                //case 1: // Fragment # 1 - Friends in Event
                //    return FriendsFragment.newInstance(1, "Friends", );
                case 2: // Fragment # 2 - Group Lists
                    return GroupListFragment.newInstance(2, "Group Lists");
                default: // Fragment # 3 - Private Lists
                    return PrivateListFragment.newInstance(3, "Private Lists");
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }


}
