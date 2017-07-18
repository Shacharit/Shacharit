package org.shaharit.face2face.gifts;

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
import com.squareup.otto.Bus;

import org.shaharit.face2face.Constants;
import org.shaharit.face2face.R;
import org.shaharit.face2face.events.Events;
import org.shaharit.face2face.model.EventNotification;
import org.shaharit.face2face.model.Gift;
import org.shaharit.face2face.service.VolleySingleton;
import org.shaharit.face2face.utils.EventBus;

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
                        if (notification == null) {
                            return;
                        }
                        NetworkImageView buddyImg = (NetworkImageView) view.findViewById(R.id.buddy_image);
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
                                int description = R.string.greetingDescription;
                                final String type = "greeting";
                                handleGiftClick(description, type, notification);
                            }
                        });
                        view.findViewById(R.id.chooseVideo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int description = R.string.videoDescription;
                                final String type = "video";
                                handleGiftClick(description, type, notification);
                            }
                        });
                        view.findViewById(R.id.chooseGift).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int description = R.string.physicalDescription;
                                final String type = "physical";
                                handleGiftClick(description, type, notification);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }

    private void handleGiftClick(int description, final String type, final EventNotification notification) {
        String titleFormat = getResources().getString(R.string.confirmGiftTitle);
        String typeStr = getResources().getString(description);
        String title = String.format(titleFormat, notification.buddyName, typeStr, notification.eventName);
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.confirm_sent_gift);
        ((TextView) dialog.findViewById(R.id.title)).setText(title);
        ((TextView) dialog.findViewById(R.id.subtitle)).setText(notification.eventDescription);
        dialog.findViewById(R.id.positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findAndCreateGift(type, notification);
                EventBus.getInstance().post(new Events.GiftSentEvent());
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
        createSentGift(foundGift, notification, type);
    }

    private void createSentGift(Gift foundGift, EventNotification notification, String type) {
        DatabaseReference newItem = mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.SENT_GIFTS_CHILD)
                .push();
        newItem.child("eventTitle").setValue(notification.eventTitle);
        newItem.child("eventName").setValue(notification.eventName);
        newItem.child("giftText").setValue(foundGift.greeting);
        newItem.child("url").setValue(foundGift.url);
        newItem.child("recipientId").setValue(notification.buddyId);
        newItem.child("senderUid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newItem.child("type").setValue(type);
    }
}
