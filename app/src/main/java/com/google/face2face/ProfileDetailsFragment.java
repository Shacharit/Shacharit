package com.google.face2face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class ProfileDetailsFragment extends Fragment {
    private static final String TAG = "ProfileDetailsFragment";
    private static final int PICK_IMAGE = 100;
    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mNameEditText;
    private StorageReference mStorageReference;
    private ImageView mImageView;

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
        mNameEditText = (EditText) view.findViewById(R.id.profile_details_name);
        mImageView = (ImageView) view.findViewById(R.id.profile_details_profile_imageView);

        mFirebaseDatabaseReference.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("display_name")) {
                            String displayName = dataSnapshot.child("display_name").getValue().toString();
                            Log.i(TAG, "display_name: " + displayName);
                            mNameEditText.setText(displayName);
                        }
                        if (dataSnapshot.hasChild("image_url")) {
                            String imageUrl = dataSnapshot.child("image_url").getValue().toString();
                            Log.i(TAG, "image_url: " + imageUrl);
                            try {
                                new ImageDownloader(mImageView).execute(imageUrl);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Button saveButton = (Button) view.findViewById(R.id.profile_details_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null){
                    mFirebaseDatabaseReference.child(Constants.USERS_CHILD).child(currentUser.getUid())
                            .child("display_name")
                            .setValue(mNameEditText.getText().toString());
                    Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://il-hackathon.appspot.com");

        Button imageUploadButton = (Button) view.findViewById(R.id.profile_details_image_uplaod);
        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                startActivityForResult(chooserIntent, PICK_IMAGE);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                final Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                StorageReference imageReference = mStorageReference.child("images/" + currentUser.getUid() + ".jpeg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imagedata = baos.toByteArray();

                UploadTask uploadTask = imageReference.putBytes(imagedata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String imageUrl = downloadUrl.toString();
                        mFirebaseDatabaseReference.child(Constants.USERS_CHILD).child(currentUser.getUid())
                                .child(Constants.IMAGE_URL).setValue(imageUrl);
                        mImageView.setImageBitmap(bitmap);
                        Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
