package edu.uga.cs.geographyquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static final String Sqlite = "SqliteDbHelper";
    private static final String dbName = "geogQuiz.db";
    private static final int version = 1;
    private static SqliteDbHelper dbInstance = null;
    public static final int CONTINENT = 0;
    public static final int NEIGHBORS = 1;
    public static SQLiteDatabase db = null;

    // Table creation scripts
    private static final String tableContinents = "continents";
    private static final String tableNeighbors = "neighboring_countries";
    private static final String tableQuiz = "quiz";

    private static final String countryId = "country_id";
    private static final String countryName = "country_name";
    private static final String continent = "continent";
    private static final String neighbors = "neighbors";
    private static final String quizId = "quiz_id";
    private static final String scores = "scores";
    private static final String dateTime = "date_and_time";

    private static final String createContinents =
            "create table " + tableContinents + " ("
                    + countryId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + countryName + " TEXT NOT NULL, "
                    + continent + " TEXT NOT NULL"
                    + ")";

    private static final String createNeighbors =
            "create table " + tableNeighbors + " ("
                    + countryId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + neighbors + " TEXT, CONSTRAINT fk_country_id "
                    + "FOREIGN KEY (" + countryId + ") "
                    + "REFERENCES " + tableContinents + "(" + countryId + ")"
                    + ")";

    private static final String createQuiz =
            "create table " + tableQuiz + " ("
                    + quizId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + scores + " INTEGER, "
                    + dateTime + " DATETIME "
                    + ")";

    private static final String [] tableScripts = {
            createContinents,
            createNeighbors,
            createQuiz
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String tableScript : tableScripts) {
            db.execSQL(tableScript);
        }

        Log.d(Sqlite, "Database " + dbName + " created");
    }

    private SqliteDbHelper(Context context) {
        super(context, dbName, null, version);
    }

    public static synchronized SqliteDbHelper getDbInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new SqliteDbHelper(context.getApplicationContext());
        }
        Log.d( Sqlite, "Instance created");
        return dbInstance;
    }

    public static SQLiteDatabase open(SqliteDbHelper dbHelper) {
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tableScript : tableScripts) {
            db.execSQL( "drop table if exists " + tableScript );
            Log.d( Sqlite, "Table " + tableScript + " upgraded" );
        }
        onCreate( db );
    }

    private static void write_continent(SQLiteDatabase db, CSVReader reader) {
        String[] next;
        try {
            while ((next = reader.readNext()) != null) {
                ContentValues values = new ContentValues();
                values.put(countryName, next[0]);
                values.put(continent, next[1]);
                db.insert(tableContinents, null, values);
            }
        } catch (Exception e) {
            Log.e(Sqlite, "Exception occurred in writing continents : " +e);
        }
    }

    private static void write_neighbours(SQLiteDatabase db, CSVReader reader) {
        String[] next;

        try {
            while ((next = reader.readNext()) != null) {
                // Creating array of neighbor JSON for a given country
                ContentValues values = new ContentValues();
                ArrayList<String>neighborsList = new ArrayList<>();
                JSONObject neighborJson = new JSONObject();

                for (int i = 1; i < next.length; i++) {
                    if (next[i].length() == 0) {
                        continue;
                    }
                    neighborsList.add(next[i]);
                }

                neighborJson.put(next[0], new JSONArray(neighborsList));
                values.put(neighbors, neighborJson.toString());
                db.insert(tableNeighbors, null, values);
            }
        } catch (Exception e) {
            Log.e(Sqlite, "Exception occurred in writing neighbors: " +e);
        }
    }

    public static void insert_data_in_database(InputStream csv, int choice) {
        CSVReader reader = new CSVReader(new InputStreamReader(csv));

        switch (choice) {
            case CONTINENT:
                new DbWriterContinentHelper().execute(csv);
                break;
            case NEIGHBORS:
                new DbWriterNeighborsHelper().execute(csv);
                break;
        }
    }

    public static class DbWriterContinentHelper extends AsyncTask<InputStream, CSVReader> {
        @Override
        protected CSVReader doInBackground(InputStream... csv) {
            return new CSVReader(new InputStreamReader(csv[0]));
        }

        @Override
        protected void onPostExecute(CSVReader reader) {
            if (db == null) {
                return;
            }

            write_continent(db, reader);
        }
    }

    public static class DbWriterNeighborsHelper extends AsyncTask<InputStream, CSVReader> {
        @Override
        protected CSVReader doInBackground(InputStream... csv) {
            return new CSVReader(new InputStreamReader(csv[0]));
        }

        @Override
        protected void onPostExecute(CSVReader reader) {
            if (db == null) {
                return;
            }

            write_neighbours(db, reader);
        }
    }
}
