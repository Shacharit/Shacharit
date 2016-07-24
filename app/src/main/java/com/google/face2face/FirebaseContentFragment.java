package com.google.face2face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link FirebaseContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirebaseContentFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FirebaseContentFragment";
    public static final String USERS_CHILD = "users";
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 30;
    public static final String DEFAULT_USER = "Rafi";
    private static final String MESSAGE_SENT_EVENT = "message_sent";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mDefinition;

    // Firebase instance variables
    private FirebaseRecyclerAdapter<String, DefinitionViewHolder> mFirebaseAdapter;


    public static class DefinitionViewHolder extends RecyclerView.ViewHolder {
        public TextView definition;

        public DefinitionViewHolder(View v) {
            super(v);
            definition = (TextView) itemView.findViewById(R.id.messageTextView);
        }
    }

    public FirebaseContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FirebaseContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirebaseContentFragment newInstance(String param1, String param2) {
        FirebaseContentFragment fragment = new FirebaseContentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firebase_content, container, false);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.userRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this.getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        NavigationActivity.mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String,
                DefinitionViewHolder>(
                String.class,
                R.layout.item_message,
                DefinitionViewHolder.class,
                NavigationActivity.mFirebaseDatabaseReference.child(USERS_CHILD).child(NavigationActivity.mUsername)) {

            @Override
            protected void populateViewHolder(DefinitionViewHolder viewHolder,
                                              String definition, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.definition.setText(definition);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


        mDefinition = (EditText) view.findViewById(R.id.messageEditText);
        mDefinition.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NavigationActivity.mSharedPreferences
                .getInt(Preferences.MOTTO_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});

        mDefinition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) view.findViewById(R.id.addDefinitionButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationActivity.mFirebaseDatabaseReference.child(USERS_CHILD).child(NavigationActivity.mUsername)
                        .push().setValue(mDefinition.getText().toString());
                mDefinition.setText("");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this.getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                NavigationActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode,
                        data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                NavigationActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Sending failed or it was canceled, show failure message to
                // the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }
}
