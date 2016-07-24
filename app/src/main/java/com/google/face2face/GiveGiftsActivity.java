package com.google.face2face;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Set;

public class GiveGiftsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_gifts);
        Set<String> keys = getIntent().getExtras().keySet();


    }
}
