package org.shaharit.face2face;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFriendsFragment extends Fragment {

    private DatabaseReference mFirebaseDatabaseReference;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.BUDDY_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create button for each definition.
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) data.getValue();
                    ((TextView) getView().findViewById(R.id.friend)).setText(map.get("displayName"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ((TextView) view.findViewById(R.id.friend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchUserFragment("l956KBVYSVa0tQSwuBMrjnYfGx22");
            }
        });
        return view;
    }

    private void launchUserFragment(String userId) {
        Fragment fragment = new MatchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(org.shaharit.face2face.R.id.main_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();
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