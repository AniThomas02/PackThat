package layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a.packthat.Event;
import com.example.a.packthat.EventListAdapter;
import com.example.a.packthat.GroupEventActivity;
import com.example.a.packthat.PrivateEventActivity;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class HomeFragment extends Fragment {
    // Store instance variables
    private ArrayList<Event> privateEventsList, groupEventsList;
    public EventListAdapter privateEventsListAdapter, groupEventsListAdaper;

    public HomeFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(ArrayList<Event> privateEventsList, ArrayList<Event> groupEventsList) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("privateEventsList", privateEventsList);
        args.putSerializable("groupEventsList", groupEventsList);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privateEventsList = (ArrayList<Event>) getArguments().getSerializable("privateEventsList");
        groupEventsList = (ArrayList<Event>) getArguments().getSerializable("groupEventsList");
    }

    @Override
    public void onStart(){
        super.onStart();

        Button addPrivateEventButton = (Button) getView().findViewById(R.id.button_add_personal_event);
        addPrivateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateEventView(0);
            }
        });
        Button addGroupEventButton = (Button) getView().findViewById(R.id.button_add_group_event);
        addGroupEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateEventView(1);
            }
        });

        ListView privateEventsListView = (ListView)getView().findViewById(R.id.listView_personal);
        privateEventsListAdapter = new EventListAdapter(getContext(), R.id.listView_personal, privateEventsList);
        privateEventsListView.setAdapter(privateEventsListAdapter);
        //view private event dialog
        privateEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = privateEventsList.get(position);
                displayManageEventDialog(event);
            }
        });

        ListView groupEventsListView = (ListView)getView().findViewById(R.id.listView_group);
        groupEventsListAdaper = new EventListAdapter(getContext(), R.id.listView_group, groupEventsList);
        groupEventsListView.setAdapter(groupEventsListAdaper);
        //view group event dialog
        groupEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = groupEventsList.get(position);
                displayManageEventDialog(event);
            }
        });
    }

    //0 is personal, 1 is private
    public void displayCreateEventView(final int eventType){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View createEventLayout = inflater.inflate(R.layout.dialog_add_event, null);
        final EditText createName = (EditText) createEventLayout.findViewById(R.id.editText_create_name);
        final EditText createDescription = (EditText) createEventLayout.findViewById(R.id.editText_create_description);
        final EditText createStartDate = (EditText) createEventLayout.findViewById(R.id.editText_date_start);
        Button createEvent = (Button) createEventLayout.findViewById(R.id.button_create_event);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        if(eventType == 0){
            alertDialog.setTitle("Create Personal Event");
        }else{
            alertDialog.setTitle("Create Group Event");
        }
        alertDialog.setView(createEventLayout);
        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = createName.getText().toString();
                String eventDescription = createDescription.getText().toString();
                String eventStartDate = createStartDate.getText().toString();
                if(!eventName.equals("")){
                    if(!eventDescription.equals("")) {
                        if(!eventStartDate.equals("")){
                            if(eventName.length() < 255 && eventDescription.length() < 255 && eventStartDate.length() < 255){
                                addNewEvent(eventName, eventDescription, eventStartDate, eventType);
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getContext().getApplicationContext(), "One of the inputs is too long, try something shorter.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext().getApplicationContext(), "You can make your startDate anything,"
                                    +" please don't leave it blank.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext().getApplicationContext(), "The event must have a description.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext().getApplicationContext(), "The event has no name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void addNewEvent(String eventName, String eventDescription, String eventStartDate, int eventType){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("createdById", User.Id);
            params.put("name", eventName);
            params.put("description", eventDescription);
            params.put("startDate", eventStartDate);
            params.put("isPrivate", eventType);
            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/addNewEvent.php";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int tempId = response.getInt("Id");
                                int tempCreatedById = response.getInt("CreatedById");
                                String tempName = response.getString("Name");
                                String tempDescription = response.getString("Description");
                                String tempStartDate = response.getString("StartDate");
                                int tempIsPrivate = response.getInt("IsPrivate");
                                Event newEvent = new Event(tempId, tempCreatedById, tempName, tempDescription, tempStartDate, tempIsPrivate);
                                if(newEvent.isPrivate == 0){
                                    privateEventsList.add(newEvent);
                                    privateEventsListAdapter.notifyDataSetChanged();
                                }else{
                                    groupEventsList.add(newEvent);
                                    groupEventsListAdaper.notifyDataSetChanged();
                                }
                            } catch(Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("MainActivity", Arrays.toString(error.getStackTrace()));
                            System.out.println("Error");
                        }
                    });
            requestQueue.add(jsObjRequest);
        }catch (Exception e){
            Log.i("MainActivity", Arrays.toString(e.getStackTrace()));
            Toast.makeText(getContext().getApplicationContext(), "Error creating new event.", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayManageEventDialog(final Event event){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewEventLayout = inflater.inflate(R.layout.dialog_view_event, null);
        TextView eventName = (TextView) viewEventLayout.findViewById(R.id.textView_manage_event_name);
        eventName.setText(event.name);
        TextView eventDescription = (TextView) viewEventLayout.findViewById(R.id.textView_manage_event_description);
        eventDescription.setText(event.description);
        TextView eventStartDate = (TextView) viewEventLayout.findViewById(R.id.textView_manage_event_startDate);
        eventStartDate.setText(event.startDate);
        Button manageEvent = (Button) viewEventLayout.findViewById(R.id.button_manage_event);
        Button deleteEvent = (Button) viewEventLayout.findViewById(R.id.button_delete_event);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("View Event");
        alertDialog.setView(viewEventLayout);
        alertDialog.setCancelable(true);

        final AlertDialog dialog = alertDialog.create();
        manageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event.isPrivate == 0){
                    Intent privateEventIntent = new Intent(getActivity(), PrivateEventActivity.class);
                    privateEventIntent.putExtra("event", event);
                    startActivity(privateEventIntent);
                }else{
                    Intent groupIntent = new Intent(getActivity(), GroupEventActivity.class);
                    groupIntent.putExtra("event", event);
                    startActivity(groupIntent);
                }
                dialog.dismiss();
            }
        });
        //delete the event in question
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.Id == event.createdById){
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
                        JSONObject params = new JSONObject();
                        params.put("eventId", event.id);
                        String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/deleteEvent.php";
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try{
                                            int affected = response.getInt("affected");
                                            if(affected >= 1){
                                                Toast.makeText(getContext().getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                                                if(event.isPrivate == 0){
                                                    privateEventsList.remove(event);
                                                    privateEventsListAdapter.notifyDataSetChanged();
                                                }else{
                                                    groupEventsList.remove(event);
                                                    groupEventsListAdaper.notifyDataSetChanged();
                                                }
                                                dialog.dismiss();
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(getContext().getApplicationContext(), "Error deleting event.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("HomeFragment", error.getStackTrace().toString());
                                        System.out.println("Error");
                                    }
                                });
                        requestQueue.add(jsObjRequest);
                    }catch (Exception e){
                        Log.i("HomeFragment", e.getStackTrace().toString());
                        Toast.makeText(getContext().getApplicationContext(), "Error deleting event.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
                        JSONObject params = new JSONObject();
                        params.put("userId", User.Id);
                        params.put("eventId", event.id);
                        String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/deleteUserFromEvent.php";
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try{
                                            int affected = response.getInt("affected");
                                            if(affected >= 1){
                                                Toast.makeText(getContext().getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                                                if(event.isPrivate == 0){
                                                    privateEventsList.remove(event);
                                                    privateEventsListAdapter.notifyDataSetChanged();
                                                }else{
                                                    groupEventsList.remove(event);
                                                    groupEventsListAdaper.notifyDataSetChanged();
                                                }
                                                dialog.dismiss();
                                            }
                                        }catch (Exception e){
                                            Toast.makeText(getContext().getApplicationContext(), "Error deleting event.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("HomeFragment", error.getStackTrace().toString());
                                        System.out.println("Error");
                                    }
                                });
                        requestQueue.add(jsObjRequest);
                    }catch (Exception e){
                        Log.i("HomeFragment", e.getStackTrace().toString());
                        Toast.makeText(getContext().getApplicationContext(), "Error deleting event.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
