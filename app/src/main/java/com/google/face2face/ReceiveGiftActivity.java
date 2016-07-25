package com.google.face2face;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ReceiveGiftActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_gift);
        Bundle data = getIntent().getExtras();
        textView = (TextView) findViewById(R.id.textView3);

        textView.setText("username " + data.get("sender") + "\n" + data.get("name")
        + "\nfor event " + data.get("event"));
    }
}