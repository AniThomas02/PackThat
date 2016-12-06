package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a.packthat.Event;
import com.example.a.packthat.EventListAdapter;
import com.example.a.packthat.R;

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
        //view Event dialog
        privateEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event event = privateEventsList.get(position);
                displayManageEventDialog(event);
            }
        });
    }

    public void displayManageEventDialog(Event event){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewEventLayout = inflater.inflate(R.layout.dialog_view_event, null);
        TextView eventName = (TextView) viewEventLayout.findViewById(R.id.textView_manage_event_name);
        eventName.setText(event.name);
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
                dialog.dismiss();
            }
        });
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
