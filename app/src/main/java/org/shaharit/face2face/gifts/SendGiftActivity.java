package org.shaharit.face2face.gifts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SendGiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.shaharit.face2face.R.layout.activity_receive_gift);
        Bundle data = getIntent().getExtras();

        TextView buddyName = (TextView) findViewById(org.shaharit.face2face.R.id.buddy_name);
        String buddyNameStr = (String)data.get("username");
        assert buddyName != null;
        buddyName.setText(buddyNameStr.toCharArray(), 0, buddyNameStr.length());

        // TODO: Support the different types of gifts.
        String title = "היי " + buddyNameStr + ", הנה מתנה עבורך";
        // data.get("foobaz") + "\n" + data.get("name") + "\nfor event " + data.get("event")

    }
}