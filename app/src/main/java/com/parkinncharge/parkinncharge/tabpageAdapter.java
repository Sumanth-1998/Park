package com.parkinncharge.parkinncharge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.parkinncharge.parkinncharge.ui.booking.current_booking;

public class tabpageAdapter extends FragmentStatePagerAdapter {


    String[] tabArray=new String[]{"UPCOMING","COMPLETED"};

    public tabpageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                upcoming Upcoming=new upcoming();
                return Upcoming;

            case 1:
                completed Completed=new completed();
                return Completed;

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
