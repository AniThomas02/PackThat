package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a.packthat.R;
import com.example.a.packthat.User;


public class HomeFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(int page, String title) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putString("pageTitle", title);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("pageNumber", 0);
        title = getArguments().getString("pageTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        Button createPersonalEventButton = (Button) getView().findViewById(R.id.button_add_personal_event);
        createPersonalEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                displayCreateEventView(0);
            }
        });

        Button createGroupEventButton = (Button) getView().findViewById(R.id.button_add_group_event);
        createGroupEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                displayCreateEventView(1);
            }
        });
    }

    //0 is personal, 1 is private
    public void displayCreateEventView(int eventType){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View createEventLayout = inflater.inflate(R.layout.dialog_add_event, null);
        final EditText createName = (EditText) createEventLayout.findViewById(R.id.editText_create_name);
        final EditText createDescription = (EditText) createEventLayout.findViewById(R.id.editText_create_description);
        final DatePicker createStartDate = (DatePicker) createEventLayout.findViewById(R.id.date_start);
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
                if(!eventName.equals("")){
                    if(!eventDescription.equals("")) {

                        //if(createStartDate.getDateFrom() != null){

                        //}
                        dialog.dismiss();
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

    public void displayViewEventPopup(View view){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewEventLayout = inflater.inflate(R.layout.dialog_add_event, null);
        Button manageEvent = (Button) viewEventLayout.findViewById(R.id.button_manage_event);

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
        dialog.show();
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
