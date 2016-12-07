package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.a.packthat.PrivateEventActivity;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

import org.json.JSONObject;

import java.util.ArrayList;


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

                }
                dialog.dismiss();
            }
        });
        //delete the event in question
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
