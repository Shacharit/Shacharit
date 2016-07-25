package com.google.face2face;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ndovrat on 7/24/16.
 */
public class Gift {
    public String sender;
    public String recipient_id;
    public String recipient;
    public String event;
    public String sent;
    public String date;
    public String cta;
    public String gender;
    public String sender_id;

    public Gift() {
        this.sent = "false";
        this.sender = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
