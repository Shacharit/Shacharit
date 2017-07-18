package org.shaharit.face2face.gifts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.shaharit.face2face.R;
import org.shaharit.face2face.service.VolleySingleton;
import org.shaharit.face2face.utils.Utils;

import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class ReceivedGiftsAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final VolleySingleton volley;
    private final Context context;
    private List<ReceivedGift> items;

    ReceivedGiftsAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.volley = VolleySingleton.getInstance(context);
        this.context = context;
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
        ReceivedGift gift = items.get(i);
        int resId = Utils.getGiftTypeStringResourceId(gift.type);
        String text = "";
        if (resId != 0) {
            String typeStr = context.getResources().getString(resId);
            text = gift.senderName + " / " + typeStr;
        }
        ((TextView) convertView.findViewById(R.id.name)).setText(text);
        NetworkImageView buddyImg = (NetworkImageView) convertView.findViewById(R.id.photo);
        buddyImg.setImageUrl(gift.senderImageUrl, volley.getImageLoader());
        return convertView;
    }

    public void setItems(List<ReceivedGift> items) {
        this.items = items;
    }

}
