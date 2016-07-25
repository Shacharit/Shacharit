package com.google.face2face;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GiveGiftsActivity extends AppCompatActivity {


    private Button button__sendGift;
    private ListView listView_gifts;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> gifts;
    private String selectedGift;
    private DatabaseReference mFirebaseDatabaseReference;
    private Gift giftToSend;

    /*
    Gift structure -
    Key - gift%d, Value - "type:<type>,url:<url>,text:<text>,cta:<cta>"
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_gifts);
        /*
        Bundle data = getIntent().getExtras();
        button__sendGift = (Button) findViewById(R.id.button_sendGift);
        listView_gifts = (ListView) findViewById(R.id.listView_gifts);
        gifts = new ArrayList<>();
        selectedGift = "";
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        giftToSend = new Gift();
        for (String key : data.keySet()) {
            if (key.contains("gift"))
                gifts.add((String)data.get(key));
            else if (key.equals("recipient"))
                giftToSend.recipient = (String)data.get(key);
            else if(key.equals("event"))
                giftToSend.event = (String)data.get(key);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gifts);

        listView_gifts.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        button__sendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // this line adds the data of your EditText and puts in your array
                gifts.add(selectedGift);
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();
            }
        });

        listView_gifts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                giftToSend.name = listView_gifts.getItemAtPosition(position).toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                //get current date time with Date()
                Date date = new Date();
                giftToSend.date = dateFormat.format(date);
                mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
            }
        });
        */
    }
}
