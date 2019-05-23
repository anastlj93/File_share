package com.example.fileshare;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                BlankFragment blankFragmentt =new BlankFragment();
                return blankFragmentt;
            case 1:
                GroupFragment groupFragment =new GroupFragment();
                return groupFragment;
            case 2:
                ContactsFragment blankFragmenttt =new ContactsFragment();
                return blankFragmenttt;
            case 3:
                ContactsFragment contactsFragment= new ContactsFragment();
                return contactsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() { return 3; }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "FILRE SHARE";
            case 1:
                return "GROUPS";
            case 2:
                return "CHATS";
            case 3:
                return "FILRE SHARE";
            default:
                return null;
        }    }
}