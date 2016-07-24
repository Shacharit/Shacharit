package com.google.face2face;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherDefinitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherDefinitionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int MAX_CHECKED = 3;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int nChecked = 0;
    private DatabaseReference mFirebaseDatabaseReference;

    public OtherDefinitionFragment() {
        // Required empty public constructor
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
        View view = inflater.inflate(R.layout.fragment_other_definition, container, false);
        final FlowLayout flowContainer = (FlowLayout) view.findViewById(R.id.flow_container);
        flowContainer.setMaxItems(MAX_CHECKED);
        mFirebaseDatabaseReference.child("self-definitions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create button for each definition.
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ToggleButton button = createButton(container, data.getKey());
                    flowContainer.addItem(button);
                }
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
