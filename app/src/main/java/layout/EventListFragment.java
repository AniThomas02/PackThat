package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a.packthat.Event;
import com.example.a.packthat.EventList;
import com.example.a.packthat.EventListItem;
import com.example.a.packthat.EventService;
import com.example.a.packthat.ExpandableListEventAdapter;
import com.example.a.packthat.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventListFragment extends Fragment {
    public Event currentEvent;
    public ArrayList<EventList> privateEventListHeaders;
    public HashMap<EventList, List<EventListItem>> privateEventListChild;
    public static ExpandableListEventAdapter privateExpandableListAdapter;
    private MyBroadcastReceiver mBroadcastReceiver;
    public EventService eventService;
    public Intent serviceIntent;

    public EventListFragment() {
        // Required empty public constructor
    }

    //newInstance constructor for creating the fragment with arguments
    public static EventListFragment newInstance(Event currEvent,
                                                HashMap<EventList, ArrayList<EventListItem>> privateEventListChild) {
        EventListFragment privateListFragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", currEvent);
        args.putSerializable("eventListChildren", privateEventListChild);
        privateListFragment.setArguments(args);
        return privateListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEvent = (Event) getArguments().getSerializable("event");
        privateEventListHeaders = currentEvent.eventLists;
        privateEventListChild = (HashMap<EventList, List<EventListItem>>) getArguments().getSerializable("eventListChildren");
    }

    @Override
    public void onStart() {
        super.onStart();

        Button addListButton = (Button)getView().findViewById(R.id.button_add_private_List);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View addEventListLayout = inflater.inflate(R.layout.dialog_add_list, null);
                final EditText addEventListEdit = (EditText) addEventListLayout.findViewById(R.id.editText_add_event_List);
                final Button addEventList = (Button) addEventListLayout.findViewById(R.id.button_add_event_list);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Add List");
                alert.setView(addEventListLayout);
                alert.setCancelable(true);

                final AlertDialog dialog = alert.create();
                addEventList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newEventList = addEventListEdit.getText().toString();
                        if(!newEventList.equals("")){
                            addNewEventList(newEventList);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getContext().getApplicationContext(), "Please put in a name for your new list.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        ExpandableListView privateExpandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView_private_list);
        privateExpandableListAdapter = new ExpandableListEventAdapter(getActivity(), privateEventListHeaders, privateEventListChild);
        privateExpandableListView.setAdapter(privateExpandableListAdapter);

        eventService = new EventService();
        serviceIntent = new Intent(getActivity(), EventService.class);
        serviceIntent.putExtra("eventId", currentEvent.id);
        getActivity().startService(serviceIntent);
        EventService.imAlive = true;

        mBroadcastReceiver = new MyBroadcastReceiver();
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(mBroadcastReceiver, new IntentFilter("eventFilter"));
    }

    public void addNewEventList(String newListName){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("eventId", currentEvent.id);
            params.put("listName", newListName);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addEventList.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                if(response != null){
                                    EventList tempList = new EventList(response.getInt("id"), response.getString("name"));
                                    privateEventListHeaders.add(tempList);
                                    privateEventListChild.put(tempList, new ArrayList<EventListItem>());
                                    privateExpandableListAdapter.notifyDataSetChanged();
                                }
                            }catch (Exception e){
                                Toast.makeText(getContext().getApplicationContext(), "Error finding friend", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext().getApplicationContext(), "Error creating new user account.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_list, container, false);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventService.imAlive = false;
    }

    class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent data){
            HashMap<EventList, List<EventListItem>> currentEventLists = (HashMap<EventList, List<EventListItem>>) data.getSerializableExtra("eventLists");
            if(currentEventLists != null){
                boolean contained;
                if(privateEventListHeaders.size() <= currentEventLists.size()){
                    ArrayList<EventList> tempList = new ArrayList<>(currentEventLists.keySet());
                    for (EventList eList: tempList ) {
                        contained = false;
                        for(EventList currList: privateEventListHeaders){
                            if(currList.eventListId == eList.eventListId){
                                contained = true;
                                currList.eventListItems = eList.eventListItems;
                                privateEventListChild.remove(currList);
                                privateEventListChild.put(currList, eList.eventListItems);
                            }
                        }
                        if(!contained){
                            privateEventListHeaders.add(eList);
                            if(eList.eventListItems == null){
                                privateEventListChild.put(eList, new ArrayList<EventListItem>());
                            }else{
                                privateEventListChild.put(eList, eList.eventListItems);
                            }
                        }
                    }
                }
                privateExpandableListAdapter.notifyDataSetChanged();
            }
        }
    }
}
