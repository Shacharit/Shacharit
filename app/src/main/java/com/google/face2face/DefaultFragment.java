package com.google.face2face;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DefaultFragment extends Fragment {


    public DefaultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.default_screen, container, false);

        final Button button = (Button) view.findViewById(R.id.default_screen_next_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).displayView(R.id.nav_choose_other);}
        });

        return view;
    }
}
