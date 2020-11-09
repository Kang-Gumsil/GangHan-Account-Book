package dankook.kanghyeyoung.capstone_2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

class PagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> mItems=new ArrayList<Fragment>();

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return new MainSpecFragment();

        } else if(position==1) {
            return new MainCalFragment();

        } else if(position==2) {
            return new MainStatFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
