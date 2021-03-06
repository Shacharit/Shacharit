package org.shaharit.face2face;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.otto.Subscribe;

import org.shaharit.face2face.events.Events;
import org.shaharit.face2face.friends.MyFriendsFragment;
import org.shaharit.face2face.gifts.GiveGiftFragment;
import org.shaharit.face2face.gifts.GotGiftFragment;
import org.shaharit.face2face.gifts.ReceiveGiftActivity;
import org.shaharit.face2face.gifts.GiftsFragment;
import org.shaharit.face2face.utils.EventBus;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ANONYMOUS = "none";
    private static final String TAG = "NavigationActivity";
    public static SharedPreferences mSharedPreferences;
    public static String mUsername;
    public static boolean mRedirectToFillProfile = false;

    // Firebase instance variables
    public static DatabaseReference mFirebaseDatabaseReference;
    public static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        EventBus.getInstance().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set default username is anonymous.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getIntent().getStringExtra("giveGifts") != null) {
            Intent intent = new Intent(this, BuddyEventActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            return;
        }
        if (getIntent().getStringExtra("receiveGift") != null) {
            Intent intent = new Intent(this, ReceiveGiftActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            return;
        }
        final String action = getIntent().getStringExtra("action");
        if ("match".equals(action)) {
            Fragment fragment = new MatchFragment();
            Bundle bundle = new Bundle();
            bundle.putString("userId", getIntent().getStringExtra("uid"));
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
            return;
        }
        if ("give_gift".equals(getIntent().getStringExtra("action"))) {
            Fragment fragment = new GiveGiftFragment();
            Bundle bundle = new Bundle();
            bundle.putString("notificationId", getIntent().getStringExtra("notificationId"));
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
            return;
        }
        if ("receive_gift".equals(action)) {
            Fragment fragment = new GotGiftFragment();
            Bundle bundle = new Bundle();
            bundle.putString("giftId", getIntent().getStringExtra("giftId"));
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
            return;
        }

        if ("RedirectToFillProfile".equals(getIntent().getStringExtra("mode"))) {
            mRedirectToFillProfile = true;
        }

        mUsername = currentUser.getUid();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Init the default fragment
        if (mRedirectToFillProfile) {
            displayView(R.id.nav_choose_other);
        } else if (savedInstanceState == null) {
            displayView(R.id.nav_default);
        }

        // Push token
        String push_token = mSharedPreferences.getString(Constants.PUSH_TOKEN, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (viewId == R.id.nav_default) {
            fragment = new DefaultFragment() ;
            title = getString(R.string.nav_default);
        } else if (viewId == R.id.nav_choose_other) {
            fragment = new OtherDefinitionFragment();
            title  = getString(R.string.nav_choose_other);
        } else if (viewId == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title = getString(R.string.nav_profile);
        }  else if (viewId == R.id.nav_who_am_I) {
            fragment = new SelfDefinitionFragment();
            title  = getString(R.string.nav_who_am_I);
        }  else if (viewId == R.id.nav_my_friends) {
            fragment = new MyFriendsFragment();
            title  = getString(R.string.nav_my_friends);
        } else if (viewId == R.id.nav_gifts) {
            fragment = new GiftsFragment();
            title  = getString(R.string.nav_gifts);
        }

        // Set the fragment.
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
        }

        // Set the toolbar title.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displayView(id);
        return true;
    }

    @Subscribe
    public void handleGiftSentEvent(Events.GiftSentEvent event) {
        Fragment fragment = new GiftsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }
    @Subscribe
    public void handleFriendClickedEvent(Events.FriendClickedEvent event) {
        Fragment fragment = new MatchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", event.userId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(org.shaharit.face2face.R.id.main_frame, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Subscribe
    public void handleGiftClickedEvent(Events.GiftClickedEvent event) {
        Fragment fragment = new GotGiftFragment();
        Bundle bundle = new Bundle();
        bundle.putString("giftId", event.giftId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(org.shaharit.face2face.R.id.main_frame, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }
}
