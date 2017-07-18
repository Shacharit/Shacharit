package org.shaharit.face2face.gifts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.shaharit.face2face.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class GiftsFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "GiveGiftFragment";
    private DatabaseReference mFirebaseDatabaseReference;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gifts, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ReceivedGiftsFragment(), getString(R.string.nav_my_received_gifts));
        adapter.addFragment(new SentGiftsFragment(), getString(R.string.nav_my_sent_gifts));
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
