package layout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.a.packthat.R;
import com.example.a.packthat.User;

public class ProfileFragment extends Fragment {
    //instance variables
    private NetworkImageView profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
