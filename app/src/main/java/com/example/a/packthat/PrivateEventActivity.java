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

import layout.EventListFragment;

/**
 * Created by Ani Thomas on 11/16/2016.
 */
public class PrivateEventActivity extends AppCompatActivity{
    FragmentPagerAdapter privateEventAdapterPager;
    private static Event currentEvent;
    private static HashMap<EventList, ArrayList<EventListItem>> listHash;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent privateEventIntent = getIntent();
        currentEvent = (Event) privateEventIntent.getSerializableExtra("event");

        setContentView(R.layout.activity_event);

        currentEvent.eventLists = new ArrayList<>();
        listHash = new HashMap<>();
        getEventLists();
    }

    public void getEventLists(){
        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("eventId", currentEvent.id);
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
                            Log.i("PrivateEventActivity", error.toString());
                            System.out.println("Error: " + error);
                            Toast.makeText(getApplicationContext(), "Error getting eventLists.", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("PrivateEventActivity", e.toString());
            Toast.makeText(getApplicationContext(), "Error Accessing DB for events.", Toast.LENGTH_SHORT).show();
        }
        createFragments();
    }

    public void createFragments(){
        setContentView(R.layout.activity_event);
        ViewPager privateEventViewPager = (ViewPager) findViewById(R.id.vpPager_event);
        privateEventAdapterPager = new MyPrivateEventPagerAdapter(getSupportFragmentManager());
        privateEventViewPager.setAdapter(privateEventAdapterPager);
    }

    //This allows for expansion in the future if I decide to add more pages.
    public static class MyPrivateEventPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 1;

        public MyPrivateEventPagerAdapter(FragmentManager fragmentManager) { super(fragmentManager); }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default: // Fragment # 0 - PrivateList Fragment
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
