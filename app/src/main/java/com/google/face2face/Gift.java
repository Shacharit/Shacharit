package com.google.face2face;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ndovrat on 7/24/16.
 */
public class Gift {
    public String sender;
    public String recipient;
    public String name;
    public String event;
    public String sent;
    public String date;

    public Gift() {
        this.sent = "false";
        this.sender = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

}
