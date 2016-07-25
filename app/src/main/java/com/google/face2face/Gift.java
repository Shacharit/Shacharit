package com.google.face2face;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ndovrat on 7/24/16.
 */
public class Gift {
    // Sender
    public String senderName;
    public String senderImageUrl;
    public String senderId;
    public String senderGender;
    // Recipient
    public String recipientImageUrl;
    public String recipientId;
    public String recipientName;
    public String recipientGender;
    // Data
    public boolean isSent;
    public String date;
    // Event
    public String eventTitle;
    public String eventDescription;

    // Gift
    public String cta;
    public String giftText;
    public String giftUrl;

    public Gift() {
        this.isSent = false;
        this.senderName = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
