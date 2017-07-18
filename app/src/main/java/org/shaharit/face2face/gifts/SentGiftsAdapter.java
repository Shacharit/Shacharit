package org.shaharit.face2face.gifts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.shaharit.face2face.R;
import org.shaharit.face2face.model.GiftSuggestion;
import org.shaharit.face2face.service.VolleySingleton;

import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class SentGiftsAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final VolleySingleton volley;
    private List<Gift> items;

    SentGiftsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.volley = VolleySingleton.getInstance(context);
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.friend_item, viewGroup, false);
        }
        Gift gift = items.get(i);
        ((TextView) convertView.findViewById(R.id.name)).setText("Ofer");
//        NetworkImageView buddyImg = (NetworkImageView) convertView.findViewById(R.id.photo);
//        buddyImg.setImageUrl(gift.imageUrl, volley.getImageLoader());
        return convertView;
    }

    public void setItems(List<Gift> items) {
        this.items = items;
    }

}
