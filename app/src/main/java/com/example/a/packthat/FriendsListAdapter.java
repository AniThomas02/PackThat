package com.example.a.packthat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * A custom list adapter to handle lists of friends
 */
public class FriendsListAdapter extends ArrayAdapter<Friend> {
    public FriendsListAdapter(Context context, int resource, ArrayList<Friend> friendsList) {
        super(context, resource, friendsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_friends, parent, false);
        }
        TextView friendName = (TextView) convertView.findViewById(R.id.textView_friend_list_name);
        friendName.setText(friend.friendName);
        TextView friendEmail = (TextView) convertView.findViewById(R.id.textView_friend_list_email);
        friendEmail.setText(friend.email);
        NetworkImageView friendProfileImage = (NetworkImageView) convertView.findViewById(R.id.imageView_friend_list_profile);
        RequestQueue requestQueue;
        ImageLoader imageLoader;
        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> cache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap){
                cache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url){
                return cache.get(url);
            }
        });
        friendProfileImage.setImageUrl("http://webdev.cs.uwosh.edu/students/thomaa04/PackThatLiveServer/uploads/" + friend.profileImg, imageLoader);

        return convertView;
    }
}
