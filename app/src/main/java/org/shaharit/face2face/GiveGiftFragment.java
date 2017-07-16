package org.shaharit.face2face;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.model.*;
import org.shaharit.face2face.model.Gift;
import org.shaharit.face2face.service.VolleySingleton;

public class GiveGiftFragment extends Fragment {

    private static final String TAG = "GiveGiftFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_give_gift, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String notificationId = getArguments().getString("notificationId");
        if (notificationId == null) {
            Log.e(TAG, "notificationId is null");
            return view;
        }
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.NOTIFICATION_CHILD)
                .child(notificationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final EventNotification notification = dataSnapshot.getValue(EventNotification.class);
                        NetworkImageView buddyImg = (NetworkImageView) view.findViewById(R.id.buddy_image);
                        if (buddyImg == null) {
                            return;
                        }
                        buddyImg.setImageUrl(notification.buddyImageUrl,
                                VolleySingleton.getInstance(getContext()).getImageLoader());
                        ((TextView) view.findViewById(R.id.buddyName)).setText(notification.buddyName);
                        ((TextView) view.findViewById(R.id.eventTitle)).setText(notification.eventTitle);
                        ((TextView) view.findViewById(R.id.description)).setText(notification.eventDescription);

                        view.findViewById(R.id.fabEmail).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{notification.buddyEmail});
                                email.setType("message/rfc822");
                                startActivity(Intent.createChooser(email, ""));
                            }
                        });
                        for(org.shaharit.face2face.model.Gift gift : notification.giftSuggestions) {
                            if (gift.type.equals("greeting")) {
                                view.findViewById(R.id.chooseGreetingLayout).setVisibility(View.VISIBLE);
                            } else if (gift.type.equals("video")) {
                                view.findViewById(R.id.chooseVideoLayout).setVisibility(View.VISIBLE);
                            } else if (gift.type.equals("physical")) {
                                view.findViewById(R.id.chooseGiftLayout).setVisibility(View.VISIBLE);
                            }
                        }
                        view.findViewById(R.id.chooseGreeting).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String titleFormat = getResources().getString(R.string.confirmGiftTitle);
                                String typeStr = getResources().getString(R.string.greetingDescription);
                                String title = String.format(titleFormat, notification.buddyName, typeStr, notification.eventName);
                                Dialog dialog = new Dialog(getContext());
                                dialog.setContentView(R.layout.confirm_sent_gift);
                                ((TextView) dialog.findViewById(R.id.title)).setText(title);
                                dialog.findViewById(R.id.positive).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        findAndCreateGift("greeting", notification);
                                    }
                                });
                            }
                        });
                        view.findViewById(R.id.chooseVideo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                findAndCreateGift("video", notification);
                            }
                        });
                        view.findViewById(R.id.chooseGift).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                findAndCreateGift("physical", notification);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }

    private void findAndCreateGift(String type, EventNotification notification) {
        Gift foundGift = null;
        for(Gift gift : notification.giftSuggestions) {
            if (gift.type.equals(type)) {
                foundGift = gift;
                break;
            }
        }
        if (foundGift == null) {
            Log.e(TAG, "Cannot find gift: " + type);
            return;
        }
        createSentGift(foundGift, notification);
    }

    private void createSentGift(Gift foundGift, EventNotification notification) {
        DatabaseReference newItem = mFirebaseDatabaseReference
                .child(Constants.SENT_GIFTS_CHILD)
                .push();
        newItem.child("eventTitle").setValue(notification.eventTitle);
        newItem.child("giftText").setValue(foundGift.greeting);
        newItem.child("url").setValue(foundGift.url);
        newItem.child("recipientId").setValue(notification.buddyId);
        newItem.child("senderUid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
