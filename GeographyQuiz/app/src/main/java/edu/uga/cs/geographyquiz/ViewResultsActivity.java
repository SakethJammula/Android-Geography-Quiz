package edu.uga.cs.geographyquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ViewResultsActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    String[] mWords;
    private Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        // initialize the string array
        mWords = new String[100];
        for(int i = 0; i < mWords.length; i++)
            mWords[i] = i + "";

        // get a reference to the recyclerview so we can set it's layout manager
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create the adapter and tell the RecyclerView to use it.
        mAdapter = new Adapter(mWords);
        mRecyclerView.setAdapter(mAdapter);
    }

    // our custom view holder
    private class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv;

        public ListItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            tv = (TextView)itemView.findViewById(R.id.list_item_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(ViewResultsActivity.this, tv.getText() + " clicked!", Toast.LENGTH_SHORT).show();
        }

        public void bind(String s) {
            tv.setText(s);
        }
    }

    // our adapter
    private class Adapter extends RecyclerView.Adapter<ListItemHolder> {
        private String[] mWords;

        // constructor
        public Adapter(String[] words) {
            mWords = words;
        }

        // required method #1
        @Override
        public ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ViewResultsActivity.this);
            return new ListItemHolder(inflater, parent);
        }

        // required method #2
        @Override
        public void onBindViewHolder(ListItemHolder holder, int position) {
            holder.bind(mWords[position]);
        }

        // required method #3
        @Override
        public int getItemCount() {
            return mWords.length;
        }
    }
}
