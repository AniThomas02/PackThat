package com.example.a.packthat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import layout.EventFriendFragment;
import layout.EventListFragment;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class GroupEventActivity extends AppCompatActivity{
    FragmentPagerAdapter groupEventAdapterPager;
    private static Event currentEvent;
    private static ArrayList<Friend> friendsInEvent;
    private static HashMap<EventList, ArrayList<EventListItem>> listHash;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent groupEventIntent = getIntent();
        currentEvent = (Event) groupEventIntent.getSerializableExtra("event");

        setContentView(R.layout.activity_event);

        friendsInEvent = new ArrayList<>();
        getFriendsInEvent();

        currentEvent.eventLists = new ArrayList<>();
        listHash = new HashMap<>();
        getEventLists();
    }

    public void getFriendsInEvent(){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectAllFriendsInEvent.php?eventId=" + currentEvent.id;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    JSONArray friendResponse = response.getJSONArray("array");
                                    Friend getFriend;
                                    for(int i =0; i < friendResponse.length(); i++){
                                        JSONObject row = friendResponse.getJSONObject(i);
                                        getFriend = new Friend(row.getInt("id"), row.getString("name"), row.getString("email"), row.getString("profileImg"));
                                        if(getFriend.friendId != User.Id){
                                            friendsInEvent.add(getFriend);
                                        }
                                    }
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("GroupEventActivity", error.toString());
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("GroupEventActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error Accessing DB.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getEventLists(){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectEventListsAndItems.php?eventId="+ currentEvent.id;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null){
                                    JSONArray lists = response.getJSONArray("listArray");
                                    JSONArray listItems = response.getJSONArray("listItemArray");
                                    EventList getList;
                                    EventListItem getListItem;
                                    for(int i =0; i < lists.length(); i++){
                                        JSONObject listRow = lists.getJSONObject(i);
                                        getList = new EventList(listRow.getInt("id"), listRow.getString("name"));
                                        for(int j = 0; j < listItems.length(); j++){
                                            JSONObject listItemRow = listItems.getJSONObject(j);
                                            if(getList.eventListId == listItemRow.getInt("listId")){
                                                getListItem = new EventListItem(listItemRow.getString("name"), listItemRow.getInt("id"), listItemRow.getString("completedBy"));
                                                getList.eventListItems.add(getListItem);
                                            }
                                        }
                                        currentEvent.eventLists.add(getList);
                                        for (EventList eventList: currentEvent.eventLists) {
                                            listHash.put(eventList, eventList.eventListItems);
                                        }
                                    }
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("GroupEventActivity", error.toString());
                            System.out.println("Error: " + error);
                            Toast.makeText(getApplicationContext(), "Error getting eventLists.", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("GroupEventActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error Accessing DB for events.", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_event);
        ViewPager groupEventViewPager = (ViewPager) findViewById(R.id.vpPager_event);
        groupEventAdapterPager = new MyGroupEventPagerAdapter(getSupportFragmentManager());
        groupEventViewPager.setAdapter(groupEventAdapterPager);
        groupEventViewPager.setCurrentItem(1);
    }

    public static class MyGroupEventPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyGroupEventPagerAdapter(FragmentManager fragmentManager) { super(fragmentManager); }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - Friends
                    return EventFriendFragment.newInstance(friendsInEvent, currentEvent);
                default:// Fragment # 1 - Home// Fragment # 0 - PrivateList Fragment
                    return EventListFragment.newInstance(currentEvent, listHash);
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}
