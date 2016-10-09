package org.shaharit.face2face;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HobbiesFragment extends Fragment {
    private static final int MAX_CHECKED = 3;

    private int nChecked = 0;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar mProgressBar;
    private FragmentTabHost mTabHost;

    public HobbiesFragment() {
        // Required empty public constructor
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(org.shaharit.face2face.R.layout.fragment_profie_interests, container, false);
        mProgressBar = (ProgressBar) view.findViewById(org.shaharit.face2face.R.id.progressBar);
        mTabHost = (FragmentTabHost)getActivity().findViewById(android.R.id.tabhost);

        final FlowLayout flowContainer = (FlowLayout) view.findViewById(org.shaharit.face2face.R.id.flow_container);
        flowContainer.setMaxItems(MAX_CHECKED);

        // Check previously chosen definitions.
        mFirebaseDatabaseReference.child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("interests")
                .child("hobbies")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<String> interests = (List<String>) dataSnapshot.getValue();
                        mFirebaseDatabaseReference.child("hobbies").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Create button for each definition.
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    ToggleButton button = createButton(container, data.getKey());
                                    flowContainer.addItem(button, interests != null && interests.contains(data.getKey()));
                                }
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Button saveButton = (Button) view.findViewById(org.shaharit.face2face.R.id.profile_interests_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null){
                    mFirebaseDatabaseReference.child(Constants.USERS_CHILD).child(currentUser.getUid())
                            .child("interests")
                            .child("hobbies")
                            .setValue(Arrays.asList(flowContainer.getCheckedNames()));

                    mTabHost.setCurrentTab(2);
                }
            }
        });

        return view;
    }

    ToggleButton createButton(ViewGroup container, String text) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        ToggleButton button = (ToggleButton) inflater.inflate(org.shaharit.face2face.R.layout.button_pill, container, false);
        button.setTextOff(text);
        button.setTextOn(text);
        button.setText(text);
        return button;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}