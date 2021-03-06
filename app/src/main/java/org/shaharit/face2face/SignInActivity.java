/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.shaharit.face2face;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static org.shaharit.face2face.Constants.PUSH_TOKEN;

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final String SERVER_OAUTH_CLIENT_ID = "539475951317-vsuq9tosmrn9jvcejelmn7u4qeuof7p6.apps.googleusercontent.com";
    public static final String USERS_CHILD = "users";

    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setContentView(org.shaharit.face2face.R.layout.activity_sign_in);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_OAUTH_CLIENT_ID)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        mSignInButton = (SignInButton) findViewById(org.shaharit.face2face.R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setScopes(gso.getScopeArray());

        mSignInButton.setOnClickListener(this);

        // Check for a logged-in user.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "SignInActivity onCreate(): currentUser == " + currentUser);
        if (currentUser != null) {
            // We always refresh the token just in case it was somehow missed
            setUserToken(currentUser);
            goToNavigation(getIntent().getExtras());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case org.shaharit.face2face.R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection Failed: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus().toString());
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT);
        }
    }

    private void addUserInfo(final GoogleSignInAccount acct) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference user = setUserToken(currentUser);
        Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                Log.d(TAG, "loadPeopleResult: " + loadPeopleResult);
                PersonBuffer buffer = loadPeopleResult.getPersonBuffer();
                Person person = buffer.get(0);
                DatabaseReference gender = user.child(Constants.GENDER);
                switch (person.getGender()) {
                    case 0:
                        gender.setValue(Constants.GENDER_MALE);
                        break;
                    case 1:
                        gender.setValue(Constants.GENDER_FEMALE);
                        break;
                    case 2:
                        gender.setValue(Constants.GENDER_OTHER);
                        break;
                }
                user.child(Constants.DISPLAY_NAME).setValue(person.getDisplayName());
                user.child(Constants.EMAIL_ADDRESS).setValue(acct.getEmail());
                user.child(Constants.BIRTHDAY).setValue(person.getBirthday());
                if (person.getImage().hasUrl()) {
                    String imageUrl = person.getImage().getUrl();
                    if (imageUrl.matches(".*sz=50$")) {
                        imageUrl = imageUrl.substring(0, imageUrl.length() - 2) + "256";
                    }
                    user.child(Constants.IMAGE_URL).setValue(imageUrl);
                }
                buffer.release();
            }
        });
    }

    private DatabaseReference setUserToken(FirebaseUser currentUser) {
        String token = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this).getString(PUSH_TOKEN, null);
        final DatabaseReference user = mFirebaseDatabaseReference.child(Constants.USERS_CHILD).child(currentUser.getUid());

        if (token != null) {
            user.child("reg_id").setValue(token);
        }
        return user;
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Save user token
                            addUserInfo(acct);

                            Bundle bundle = getIntent().getExtras();
                            if (bundle == null) {  // No extras were given to us. Create a new one.
                                bundle = new Bundle();
                            }
                            bundle.putString("mode", "RedirectToFillProfile");
                            goToNavigation(bundle);
                        }
                    }
                });
    }

    // Open NavigationActivity. If the given Bundle is not null, it is used as Extras.
    private void goToNavigation(Bundle extras) {
        Intent intent = new Intent(SignInActivity.this, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
        finish();
    }
}
