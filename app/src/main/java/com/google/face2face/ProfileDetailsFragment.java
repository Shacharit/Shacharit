package com.google.face2face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

public class ProfileDetailsFragment extends Fragment {
    private static final String TAG = "ProfileDetailsFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    public ProfileDetailsFragment() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static ProfileDetailsFragment newInstance(String param1, String param2) {
        ProfileDetailsFragment fragment = new ProfileDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        mFirebaseDatabaseReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("display_name")) {
                            String displayName = dataSnapshot.child("display_name").getValue().toString();
                            Log.i(TAG, "display_name: " + displayName);
                            EditText editText = (EditText) view.findViewById(R.id.profile_details_name);
                            editText.setText(displayName);
                        }
                        if (dataSnapshot.hasChild("image_url")) {
                            String imageUrl = dataSnapshot.child("image_url").getValue().toString();
                            Log.i(TAG, "image_url: " + imageUrl);
                            try {
                                ImageView imageView = (ImageView) view.findViewById(R.id.profile_details_profile_imageView);
                                new ImageDownloader(imageView).execute(imageUrl);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
