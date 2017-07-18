package org.shaharit.face2face;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.model.*;
import org.shaharit.face2face.service.VolleySingleton;
import org.w3c.dom.Text;

public class GotGiftFragment extends Fragment {
    private static final String TAG = "GotGiftFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_got_gift, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String giftId = getArguments().getString("giftId");

        final ImageButton giftButton = (ImageButton) view.findViewById(R.id.giftAction);
        giftButton.setVisibility(View.GONE);

        final ImageButton videoButton = (ImageButton) view.findViewById(R.id.videoAction);
        videoButton.setVisibility(View.GONE);

        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.GIFT_CHILD)
                .child(giftId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final GiftDetails giftDetails = dataSnapshot.getValue(GiftDetails.class);

                        NetworkImageView buddyImg =
                                (NetworkImageView) view.findViewById(org.shaharit.face2face.R.id.buddy_image);

                        buddyImg.setImageUrl(giftDetails.senderImageUrl,
                                VolleySingleton.getInstance(getContext()).getImageLoader());

                        final TextView giftTitleView = (TextView) view.findViewById(R.id.giftTitle);

                        giftTitleView.setText(giftDetails.senderName + "קיבלת מתנה מ");

                        final TextView giftTextView = (TextView) view.findViewById(R.id.giftText);

                        giftTextView.setText(giftDetails.giftInfo.text);

                        final TextView giftUrlView = (TextView) view.findViewById(R.id.giftUrl);

                        final String detailsStr = "פרטים";

                        final String url = giftDetails.giftInfo.url;

                        if (!TextUtils.isEmpty(url)) {
                            String value = String.format("<html><a href=%s>%s</a></html>", url,
                                    detailsStr);
                            giftUrlView.setMovementMethod(LinkMovementMethod.getInstance());
                            giftUrlView.setText(Html.fromHtml(value));
                        } else {
                            giftUrlView.setVisibility(View.GONE);
                        }

                        view.findViewById(R.id.messageActionAction).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{giftDetails.senderEmail});
                                email.setType("message/rfc822");
                                startActivity(Intent.createChooser(email, ""));
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return view;
    }
}
