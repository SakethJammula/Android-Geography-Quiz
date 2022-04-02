package edu.uga.cs.geographyquiz;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

public class QuizDesign {
    public static ArrayList<String> listOfCountries = new ArrayList<>();
    public static ArrayList<String> countryInContinent = new ArrayList<>();
    public static ArrayList<String> neighborsOfCountry = new ArrayList<>();

    public static void fill_country_with_continent_list(Cursor cursor, int noOfCountries) {
        if (cursor.getCount() > 0 && listOfCountries.size() != noOfCountries) {
            while (cursor.moveToNext()) {
                int countryColumnId = cursor.getColumnIndex(SqliteDbHelper.countryName);
                listOfCountries.add(cursor.getString(countryColumnId));
                int continentColumnId = cursor.getColumnIndex(SqliteDbHelper.continent);
                countryInContinent.add(cursor.getString(continentColumnId));
            }

            Log.d(SqliteDbHelper.Sqlite, "numOfCountries : "+listOfCountries.size());
        }
    }

    public static void fill_neighbors_list(Cursor cursor, int noOfNeighbors) {
        if (cursor.getCount() > 0 && neighborsOfCountry.size() != noOfNeighbors) {
            while (cursor.moveToNext()) {
                int neighborColumnId = cursor.getColumnIndex(SqliteDbHelper.neighbors);
                neighborsOfCountry.add(cursor.getString(neighborColumnId));
            }

            Log.d(SqliteDbHelper.Sqlite, "Neighbors : "+neighborsOfCountry.size());
        }
    }
}
