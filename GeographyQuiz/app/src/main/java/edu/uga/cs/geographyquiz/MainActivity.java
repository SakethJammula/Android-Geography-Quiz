package edu.uga.cs.geographyquiz;

import static edu.uga.cs.geographyquiz.SqliteDbHelper.CONTINENT;
import static edu.uga.cs.geographyquiz.SqliteDbHelper.NEIGHBORS;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    SqliteDbHelper db = null;
    SQLiteDatabase dbInstance;
    private static final String DEBUG_TAG = "Sqlite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = SqliteDbHelper.getDbInstance(getApplicationContext());
        dbInstance = SqliteDbHelper.open(db);

        try {
            SqliteDbHelper.ReaderDb continentData = new SqliteDbHelper.ReaderDb();
            continentData.csv = getAssets().open("country_continent.csv");
            continentData.choice = CONTINENT;
            new SqliteDbHelper.DbReaderHelper().execute(continentData);

            SqliteDbHelper.ReaderDb neighborData = new SqliteDbHelper.ReaderDb();
            neighborData.csv = getAssets().open("country_neighbors.csv");
            neighborData.choice = NEIGHBORS;
            new SqliteDbHelper.DbReaderHelper().execute(neighborData);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "Exception occurred : "+e);
        }

        Log.d(DEBUG_TAG, "Completed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbInstance = SqliteDbHelper.open(db);
        Log.d(DEBUG_TAG, "Reached");
    }
}