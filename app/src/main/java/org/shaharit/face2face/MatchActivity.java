package org.shaharit.face2face;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Bundle bundle = getIntent().getExtras();

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            System.out.println(String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        TextView buddyName = (TextView) findViewById(R.id.buddy_name);
        assert buddyName != null;
        buddyName.setText(bundle.toString().toCharArray(), 0, bundle.toString().length());
    }
}
