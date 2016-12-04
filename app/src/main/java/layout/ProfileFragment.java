package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.a.packthat.MainActivity;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

public class ProfileFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private OnFragmentInteractionListener mListener;
    private NetworkImageView profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ProfileFragment newInstance(int page, String title) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putString("pageTitle", title);
        profileFragment.setArguments(args);
        return profileFragment;
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        profileImage = (NetworkImageView) getView().findViewById(R.id.imageView_profile);
        getUserCustomImage();
    }

    public void getUserCustomImage(){
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
        profileImage.setImageUrl("http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/uploads/" + User.ProfileImg, imageLoader);
    }

    @Override
    public void onResume(){
        super.onResume();
        fillProfileInfo();
    }

    public void fillProfileInfo(){
        TextView name = (TextView)getView().findViewById(R.id.textView_name);
        name.setText(User.Name);
        TextView email = (TextView)getView().findViewById(R.id.textView_email);
        email.setText(User.Email);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
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
