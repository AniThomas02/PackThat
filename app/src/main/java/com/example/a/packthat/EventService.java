package com.example.a.packthat;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class EventService extends IntentService {
    public static volatile boolean imAlive;
    LocalBroadcastManager localBroadcastManager;
    private int eventId;
    private RequestQueue requestQueue;

    public EventService() {
        super("EventService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventId = intent.getIntExtra("eventId", 0);

        //one minute is 60*1000
        try {
            while(imAlive) {
                Thread.sleep(10000);
                final HashMap<EventList, List<EventListItem>> listhash = new HashMap<>();
                try {
                    if(requestQueue == null){
                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                    }
                    String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/selectEventListsAndItems.php?eventId="+ eventId;
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
                                                    listhash.put(getList, getList.eventListItems);
                                                }
                                            }
                                            Intent dataIntent = new Intent("eventFilter");
                                            dataIntent.putExtra("eventLists", listhash);
                                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(dataIntent);
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
                Log.d("event", "" + imAlive);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
