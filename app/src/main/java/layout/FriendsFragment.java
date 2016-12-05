package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a.packthat.Friend;
import com.example.a.packthat.FriendsListAdapter;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    //instance variables
    private String title;
    private int page;
    private ArrayList<Friend> friendsList;
    private OnFragmentInteractionListener mListener;
    private static FriendsListAdapter friendsListAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    //newInstance constructor for creating the fragment with arguments
    public static FriendsFragment newInstance(int page, String title, ArrayList<Friend> friendsList) {
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putString("pageTitle", title);
        args.putSerializable("friendsList", friendsList);
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("pageNumber", 0);
        title = getArguments().getString("pageTitle");
        friendsList = (ArrayList<Friend>) getArguments().getSerializable("friendsList");
    }

    @Override
    public void onStart(){
        super.onStart();

        ListView friendsListView = (ListView)getView().findViewById(R.id.listView_friends);
        friendsListAdapter = new FriendsListAdapter(getContext(), R.id.listView_friends, friendsList);

        friendsListView.setAdapter(friendsListAdapter);
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = friendsList.get(position);
                Toast.makeText(getContext(), friend.friendName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(friendsList != null && friendsList.size() != 0){

        }else{
            Toast.makeText(getContext().getApplicationContext(), "No Friends.", Toast.LENGTH_SHORT).show();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//          mListener.onFragmentInteraction(uri);
//        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
