package edu.uga.cs.geographyquiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class myAdapter extends FragmentStatePagerAdapter {

    // constructor

    private String[] strings;



    public myAdapter(FragmentManager fm, String[] str){
        super(fm);

        strings = str;
    }

    // Create and return the Fragment object for the ViewPager

    @Override
    public Fragment getItem(int position){
        // Differentiate between pages using position

        Fragment fragment = new QuizFragment();
        Bundle bundle = new Bundle();
        bundle.putString("KEY", strings[position]);
        fragment.setArguments(bundle);
        return fragment;
    }



    // Tell ViewPager how many items are in the adapter

        public int getCount(){
        // Perform DB here: query, find out records
        return strings.length;
        }

}
