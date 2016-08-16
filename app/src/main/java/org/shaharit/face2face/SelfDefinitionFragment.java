package org.shaharit.face2face;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfDefinitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelfDefinitionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int NUM_OF_SELF_DEFINITIONS = 3;
    private static final String OTHER_DEFINITIONS = "other-definitions";
    private static final String SELF_DEFINITIONS = "self-definitions";
    private static final String USERS = "users";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int nChecked = 0;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar mProgressBar;

    public SelfDefinitionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherDefinitionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherDefinitionFragment newInstance(String param1, String param2) {
        OtherDefinitionFragment fragment = new OtherDefinitionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private View createDummyTextView(String text) {
        TextView textView = new TextView(this.getContext());
        textView.setText(text);
        return textView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_self_definition, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final FlowLayout flowContainer = (FlowLayout) view.findViewById(R.id.fragment_self_definition_flow_container);
        flowContainer.setMaxItems(NUM_OF_SELF_DEFINITIONS);

        // Add onClick binding to 'Next' button.
        final Button button = (Button) view.findViewById(R.id.fragment_self_definition_next_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String[] pickedDefinitions = flowContainer.getCheckedNames();
                if (pickedDefinitions.length != NUM_OF_SELF_DEFINITIONS) {
                    Toast.makeText(getContext(), R.string.self_definition_pick_alert_message, Toast.LENGTH_SHORT).show();
                } else { // This is a valid pick of definitions.
                    // Remove previous definitions and add new ones.
                    mFirebaseDatabaseReference
                            .child(USERS)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(SELF_DEFINITIONS)
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    for (String definition : pickedDefinitions) {
                                        mFirebaseDatabaseReference
                                                .child(USERS)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(SELF_DEFINITIONS)
                                                .push().setValue(definition);
                                    }
                                }
                            });
                    // Change fragment to profile.
                    ((NavigationActivity) getActivity()).displayView(R.id.nav_profile);
                }

            }
        });

        // TODO: move to one snapshot instead of two snapshots.
        final Map<String, String> oldDefinitions = new HashMap<String, String>();
        mFirebaseDatabaseReference.child(USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(SELF_DEFINITIONS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Create button for each definition.
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            oldDefinitions.put(data.getValue().toString(), null);
                        }
                        mFirebaseDatabaseReference.child(SELF_DEFINITIONS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Create button for each definition.
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    ToggleButton button = createButton(container, data.getKey());
                                    flowContainer.addItem(button, oldDefinitions.containsKey(data.getKey()));
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

        return view;
    }

    ToggleButton createButton(ViewGroup container, String text) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        ToggleButton button = (ToggleButton) inflater.inflate(R.layout.button_pill, container, false);
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