package layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.a.packthat.Friend;
import com.example.a.packthat.FriendsListAdapter;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

import org.json.JSONObject;

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
    public static FriendsListAdapter friendsListAdapter;

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
        //viewFriend dialog
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend friend = friendsList.get(position);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View viewFriendLayout = inflater.inflate(R.layout.dialog_view_friend, null);
                final Button deleteFriend = (Button) viewFriendLayout.findViewById(R.id.button_remove_friend);
                TextView friendName = (TextView) viewFriendLayout.findViewById(R.id.textView_view_friend_name);
                friendName.setText(friend.friendName);
                TextView friendEmail = (TextView) viewFriendLayout.findViewById(R.id.textView_view_friend_email);
                friendEmail.setText(friend.email);
                NetworkImageView friendProfImage = (NetworkImageView) viewFriendLayout.findViewById(R.id.imageView_view_friend_profile);
                RequestQueue requestQueue;
                ImageLoader imageLoader;
                requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
                imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache(){
                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap){
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url){
                        return cache.get(url);
                    }
                });
                friendProfImage.setImageUrl("http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/uploads/" + friend.profileImg, imageLoader);

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setView(viewFriendLayout);
                alert.setCancelable(true);

                final AlertDialog dialog = alert.create();
                deleteFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
                            JSONObject params = new JSONObject();
                            params.put("userId", User.Id);
                            params.put("friendId", friend.friendId);
                            String url = "http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/deleteFriend.php";
                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                    (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try{
                                                int affected = response.getInt("affected");
                                                if(affected >= 1){
                                                    Toast.makeText(getContext().getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                                                    friendsList.remove(friend);
                                                    friendsListAdapter.notifyDataSetChanged();
                                                    dialog.dismiss();
                                                }
                                            }catch (Exception e){
                                                Toast.makeText(getContext().getApplicationContext(), "Error deleting friend", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i("FriendsFragment", error.getStackTrace().toString());
                                            System.out.println("Error");
                                        }
                                    });
                            requestQueue.add(jsObjRequest);
                        }catch (Exception e){
                            Log.i("FriendsFragment", e.getStackTrace().toString());
                            Toast.makeText(getContext().getApplicationContext(), "Error deleting friend.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
