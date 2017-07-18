package org.shaharit.face2face.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.shaharit.face2face.R;
import org.shaharit.face2face.model.Buddy;
import org.shaharit.face2face.service.VolleySingleton;

import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class FriendAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final VolleySingleton volley;
    private List<Buddy> buddies = null;

    FriendAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.volley = VolleySingleton.getInstance(context);
    }
    @Override
    public int getCount() {
        if (buddies == null) {
            return 0;
        }
        return buddies.size();
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
        Buddy buddy = buddies.get(i);
        ((TextView) convertView.findViewById(R.id.name)).setText(buddy.displayName);
        NetworkImageView buddyImg = (NetworkImageView) convertView.findViewById(R.id.photo);
        buddyImg.setImageUrl(buddy.imageUrl, volley.getImageLoader());
        return convertView;
    }

    public void setItems(List<Buddy> items) {
        this.buddies = items;
    }
}
