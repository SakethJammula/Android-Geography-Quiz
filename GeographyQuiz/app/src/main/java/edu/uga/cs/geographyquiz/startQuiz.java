package edu.uga.cs.geographyquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class startQuiz extends AppCompatActivity {

    private ViewPager vp;
    private String[] strings;
    final int SIZE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        // Initialize the array

        strings = new String[SIZE];

        for(int i=0; i< strings.length; i++){
            strings[i] = "Fragment" + i;

        // Retrieve the ViewPager object reference
            vp = (ViewPager)findViewById(R.id.pager);

        // Set its PagerAdapter
        // Needs the FragmentManager reference so that the adapter can inform the fragment manager
        // of the new fragments

        vp.setAdapter(new myAdapter(getSupportFragmentManager(), strings));


        }
    }
}