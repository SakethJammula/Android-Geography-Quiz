package edu.uga.cs.geographyquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public static final String Sqlite = "SqliteDbHelper";
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
    public static final String countryName = "country_name";
    public static final String continent = "continent";
    public static final String neighbors = "neighbors";
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
                    + dateTime + " TEXT "
                    + ")";

    private static final String [] tableScripts = {
            createContinents,
            createNeighbors,
            createQuiz
    };

    public static class ReaderDb {
        InputStream csv;
        int choice;
    }

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

    private static synchronized boolean retrieve_entries_in_table() {
        int continentCount = 0, neighborsCount = 0;

        if (!db.isOpen()) {
            return false;
        }

        Cursor cursor = db.query(tableContinents, null, null, null,
                null, null, null);

        if (cursor != null) {
            continentCount = cursor.getCount();

            if (continentCount > 0) {
                QuizDesign.fill_country_with_continent_list(cursor, continentCount);
                QuizDesign.design_country_questions();
                QuizDesign.QuizQuestions[] quiz = QuizDesign.get_questions_on_continents();

                for (QuizDesign.QuizQuestions quizQuestions : quiz) {
                    Log.d(Sqlite, "Question : " + quizQuestions.question);
                    Log.d(Sqlite, "Option0 : " + quizQuestions.option0);
                    Log.d(Sqlite, "Option1 : " + quizQuestions.option1);
                    Log.d(Sqlite, "Option2 : " + quizQuestions.option2);
                }
            }

            cursor.close();
        }

        cursor = db.query(tableNeighbors, null, null, null,
                null, null, null);

        if (cursor != null) {
            neighborsCount = cursor.getCount();

            if (neighborsCount > 0) {
                QuizDesign.fill_neighbors_list(cursor, neighborsCount);
                QuizDesign.design_neighbors_questions();
                QuizDesign.QuizQuestions[] quiz = QuizDesign.get_questions_on_neighbors();

                for (QuizDesign.QuizQuestions quizQuestions : quiz) {
                    Log.d(Sqlite, "Neighbors Question : " + quizQuestions.question);
                    Log.d(Sqlite, "Neighbors Option0 : " + quizQuestions.option0);
                    Log.d(Sqlite, "Neighbors Option1 : " + quizQuestions.option1);
                    Log.d(Sqlite, "Neighbors Option2 : " + quizQuestions.option2);
                    Log.d(Sqlite, "Neighbors Option3 : " + quizQuestions.option3);
                }
            }

            cursor.close();
        }

        return continentCount > 0 && neighborsCount > 0;
    }

    public static class QuizRecords {
        int scores;
        String dateTime;
    }

    public static synchronized QuizRecords[] retrieve_quiz_records() {
        QuizRecords[] quizRecords = null;
        if (!db.isOpen()) {
            return null;
        }

        Cursor cursor = db.query(tableQuiz, null, null, null,
                null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                quizRecords = new QuizRecords[cursor.getCount()];
                int i = 0;
                while (cursor.moveToNext()) {
                    quizRecords[i] = new QuizRecords();
                    int index = cursor.getColumnIndex(scores);
                    quizRecords[i].scores = cursor.getInt(index);
                    index = cursor.getColumnIndex(dateTime);
                    quizRecords[i].dateTime = cursor.getString(index);
                }
            }

            cursor.close();
        }

        return quizRecords;
    }

    public static class DbWriterContinentHelper extends AsyncTask<InputStream, Integer> {
        @Override
        protected Integer doInBackground(InputStream... csv) {
            CSVReader reader = new CSVReader(new InputStreamReader(csv[0]));
            if (db == null) {
                return -1;
            }

            write_continent(db, reader);
            retrieve_entries_in_table();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i) {
            Log.d(Sqlite, "Written continent data");
        }
    }

    public static class DbWriterNeighborsHelper extends AsyncTask<InputStream, Integer> {
        @Override
        protected Integer doInBackground(InputStream... csv) {
            CSVReader reader = new CSVReader(new InputStreamReader(csv[0]));
            if (db == null) {
                return -1;
            }

            write_neighbours(db, reader);
            retrieve_entries_in_table();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer i) {
            Log.d(Sqlite, "Written neighbors data");
        }
    }

    public static class DbReaderHelper extends AsyncTask<ReaderDb, ReaderDb> {

        @Override
        protected ReaderDb doInBackground(ReaderDb... arguments) {
            boolean isDbFilled = retrieve_entries_in_table();

            if (isDbFilled) {
                Log.d(Sqlite, "Database is filled");
                return null;
            }

            Log.d(Sqlite, "Database is empty for choice : "+arguments[0].choice);
            return arguments[0];
        }

        @Override
        protected void onPostExecute(ReaderDb result) {
            if (result != null) {
                Log.d(Sqlite, "Filling data base for choice : "+result.choice);
                switch (result.choice) {
                    case CONTINENT:
                        new DbWriterContinentHelper().execute(result.csv);
                        break;
                    case NEIGHBORS:
                        new DbWriterNeighborsHelper().execute(result.csv);
                }
            }
        }
    }

    public static class QuizReader extends AsyncTask<Void, QuizRecords[]> {

        @Override
        protected QuizRecords[] doInBackground(Void... arguments) {
            return retrieve_quiz_records();
        }

        @Override
        protected void onPostExecute(QuizRecords[] quizRecords) {
            // Place holder to call any method that can use the quiz records
        }
    }

    public static class QuizWriter extends AsyncTask<QuizRecords, Void> {
        @Override
        protected Void doInBackground(QuizRecords... arguments) {
            ContentValues values = new ContentValues();
            values.put(scores, arguments[0].scores);
            values.put(dateTime, arguments[0].dateTime);
            db.insert(tableQuiz, null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            // Place holder to call any method that wanted to get executed after the quiz is written
            // on the database
        }
    }
}
