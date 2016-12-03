package com.example.a.packthat;


/**
 * Created by Ani Thomas on 12/1/2016.
 */
public class FriendsList {
    private User friend;

    public FriendsList(User friend) {
        this.friend = friend;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
