package edu.uga.cs.geographyquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static final String Sqlite = "SqliteDbHelper";
    private static final String dbName = "geogQuiz.db";
    private static final int version = 1;
    private static SqliteDbHelper dbInstance = null;
    public static final int CONTINENT = 0;
    public static final int NEIGHBORS = 1;

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
                long countryId = db.insert(tableContinents, null, values);
            }
        } catch (Exception e) {

        }
    }

    public static void insert_data_in_database(SQLiteDatabase db, InputStream csv, int choice) {
        CSVReader reader = new CSVReader(new InputStreamReader(csv));

        switch (choice) {
            case CONTINENT:
                write_continent(db, reader);
                break;
            case NEIGHBORS:
                break;
        }
    }
}
