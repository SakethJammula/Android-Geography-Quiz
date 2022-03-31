package edu.uga.cs.geographyquiz;

import static edu.uga.cs.geographyquiz.SqliteDbHelper.CONTINENT;
import static edu.uga.cs.geographyquiz.SqliteDbHelper.NEIGHBORS;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SqliteDbHelper db = null;
    SQLiteDatabase dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = SqliteDbHelper.getDbInstance(getApplicationContext());
        dbInstance = db.getWritableDatabase();

        try {
            SqliteDbHelper.insert_data_in_database(dbInstance,
                    getAssets().open("country_continent.csv"), CONTINENT);
            SqliteDbHelper.insert_data_in_database(dbInstance,
                    getAssets().open("country_neighbors.csv"), NEIGHBORS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}