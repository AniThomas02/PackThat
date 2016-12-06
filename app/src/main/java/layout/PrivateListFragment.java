package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a.packthat.Event;
import com.example.a.packthat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrivateListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrivateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivateListFragment extends Fragment {
    //instance variables
    private int eventId;

    public PrivateListFragment() {
        // Required empty public constructor
    }

    //newInstance constructor for creating the fragment with arguments
    public static PrivateListFragment newInstance(int eventId) {
        PrivateListFragment privateListFragment = new PrivateListFragment();
        Bundle args = new Bundle();
        args.putInt("eventId", eventId);
        privateListFragment.setArguments(args);
        return privateListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.eventId = getArguments().getInt("eventId", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
