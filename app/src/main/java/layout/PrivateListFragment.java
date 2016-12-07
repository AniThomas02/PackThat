package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.a.packthat.EventList;
import com.example.a.packthat.EventListItem;
import com.example.a.packthat.ExpandableListEventAdapter;
import com.example.a.packthat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrivateListFragment extends Fragment {
    ArrayList<EventList> privateEventListHeaders;
    HashMap<EventList, List<EventListItem>> privateEventListChild;
    public static ExpandableListEventAdapter privateExpandableListAdapter;

    public PrivateListFragment() {
        // Required empty public constructor
    }

    //newInstance constructor for creating the fragment with arguments
    public static PrivateListFragment newInstance(ArrayList<EventList> privateEventListHeaders,
                                                  HashMap<EventList, ArrayList<EventListItem>> privateEventListChild) {
        PrivateListFragment privateListFragment = new PrivateListFragment();
        Bundle args = new Bundle();
        args.putSerializable("eventListHeaders", privateEventListHeaders);
        args.putSerializable("eventListChildren", privateEventListChild);
        privateListFragment.setArguments(args);
        return privateListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privateEventListHeaders = (ArrayList<EventList>) getArguments().getSerializable("eventListHeaders");
        privateEventListChild = (HashMap<EventList, List<EventListItem>>) getArguments().getSerializable("eventListChildren");
    }

    @Override
    public void onStart() {
        super.onStart();

        Button addListButton = (Button)getView().findViewById(R.id.button_add_private_List);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "Add Private List Click.", Toast.LENGTH_SHORT).show();
            }
        });

        ExpandableListView privateExpandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView_private_list);
        privateExpandableListAdapter = new ExpandableListEventAdapter(getActivity(), privateEventListHeaders, privateEventListChild);
        privateExpandableListView.setAdapter(privateExpandableListAdapter);

        /*
        privateExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        privateExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext().getApplicationContext(),
                        privateEventListHeaders.get(groupPosition).eventListName + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        privateExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext().getApplicationContext(),
                        privateEventListHeaders.get(groupPosition).eventListName + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        privateExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getContext().getApplicationContext()
                        , "Check: " + privateEventListChild.get(privateEventListHeaders.get(groupPosition)).get(childPosition).eliName
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
