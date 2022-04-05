package edu.uga.cs.geographyquiz;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class QuizDesign {
    public static ArrayList<String> listOfCountries = new ArrayList<>();
    public static ArrayList<String> countryInContinent = new ArrayList<>();
    public static ArrayList<String> neighborsOfCountry = new ArrayList<>();
    public static ArrayList<String> countryAnswers = new ArrayList<>();
    public static ArrayList<String> neighborsAnswers = new ArrayList<>();
    private static boolean[] countryBitmap = null;
    private static boolean[] continentBitmap = null;
    private static final int noOfQuestions = 6;
    private static final int noOfOptions = 3;
    private static final HashSet<String> continentSet = new HashSet<>();
    private static ArrayList<String> uniqueContinents = null;
    private static QuizQuestions[] questionAndOptions = null;
    private static QuizQuestions[] neighborsAndOptions = null;

    public static class QuizQuestions {
        String question;
        String option0;
        String option1;
        String option2;
        String option3;
    }

    public static void fill_country_with_continent_list(Cursor cursor, int noOfCountries) {
        if (cursor.getCount() > 0 && listOfCountries.size() != noOfCountries) {
            while (cursor.moveToNext()) {
                int countryColumnId = cursor.getColumnIndex(SqliteDbHelper.countryName);
                listOfCountries.add(cursor.getString(countryColumnId));
                int continentColumnId = cursor.getColumnIndex(SqliteDbHelper.continent);
                countryInContinent.add(cursor.getString(continentColumnId));
            }

            continentSet.addAll(countryInContinent);

            if (uniqueContinents == null) {
                uniqueContinents = new ArrayList<>(continentSet);
            }
        }

        if (countryBitmap == null) {
            countryBitmap = new boolean[noOfCountries];
            Arrays.fill(countryBitmap, false);
        }

        if (continentBitmap == null) {
            continentBitmap = new boolean[uniqueContinents.size()];
            Arrays.fill(continentBitmap, false);
        }
    }

    public static void fill_neighbors_list(Cursor cursor, int noOfNeighbors) {
        if (cursor.getCount() > 0 && neighborsOfCountry.size() != noOfNeighbors) {
            while (cursor.moveToNext()) {
                int neighborColumnId = cursor.getColumnIndex(SqliteDbHelper.neighbors);
                neighborsOfCountry.add(cursor.getString(neighborColumnId));
            }
        }
    }

    private static int random_index_generator(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }

    private static void set_options(int option, int countryId, QuizQuestions quiz, ArrayList<String>array) {
        switch (option) {
            case 0:
                quiz.option0 = array.get(countryId);
                break;
            case 1:
                quiz.option1 = array.get(countryId);
                break;
            case 2:
                quiz.option2 = array.get(countryId);
                break;
        }
    }

    private static void fill_remaining_options(QuizQuestions quiz, ArrayList<String>array,
                                               boolean[] bitmap) {
        while (quiz.option0 == null || quiz.option1 == null || quiz.option2 == null) {
            int continentId = random_index_generator(array.size());

            if (continentId == array.size()) {
                continentId--;
            }

            if (bitmap[continentId]) {
                continue;
            }

            if (quiz.option0 == null) {
                quiz.option0 = "";
                quiz.option0 = array.get(continentId);
                bitmap[continentId] = true;
                continue;
            }

            if (quiz.option1 == null) {
                quiz.option1 = "";
                quiz.option1 = array.get(continentId);
                bitmap[continentId] = true;
                continue;
            }

            if (quiz.option2 == null) {
                quiz.option2 = "";
                quiz.option2 = array.get(continentId);
                bitmap[continentId] = true;
            }
        }

        Arrays.fill(bitmap, false);
    }

    private static void fill_remaining_neighbor_options(QuizQuestions quiz, JSONArray array,
                                                        int countryId, int correctOption) {
        boolean[] countryTracker = new boolean[listOfCountries.size()];
        Arrays.fill(countryTracker, false);

        countryTracker[countryId] = true;

        if (correctOption != -1) {
            countryTracker[correctOption] = true;
        }

        ArrayList<String> neighboringCountries = new ArrayList<>();

        for (int j = 0; j < array.length(); j++) {
            try {
                neighboringCountries.add(array.getString(j));
            } catch (JSONException e) {
                Log.d(SqliteDbHelper.Sqlite, "Exception occurred : "+e);
            }
        }
        HashSet<String>neighborSet = new HashSet<>(neighboringCountries);

        while (quiz.option0 == null || quiz.option1 == null || quiz.option2 == null) {
            int id = random_index_generator(listOfCountries.size());

            if (id == countryId || neighborSet.contains(listOfCountries.get(id)) ||
                    countryTracker[id]) {
                countryTracker[id] = true;
                continue;
            }

            if (quiz.option0 == null) {
                quiz.option0 = listOfCountries.get(id);
                countryTracker[id] = true;
                continue;
            }

            if (quiz.option1 == null) {
                quiz.option1 = listOfCountries.get(id);
                countryTracker[id] = true;
                continue;
            }

            if (quiz.option2 == null) {
                quiz.option2 = listOfCountries.get(id);
                countryTracker[id] = true;
            }
        }
    }

    public static void design_country_questions() {
        if (questionAndOptions == null) {
            questionAndOptions = new QuizQuestions[noOfQuestions];

            for (int i = 0; i < noOfQuestions; i++) {
                questionAndOptions[i] = new QuizQuestions();
                questionAndOptions[i].question = null;
                questionAndOptions[i].option0 = null;
                questionAndOptions[i].option1 = null;
                questionAndOptions[i].option2 = null;
            }

            for (int i = 0; i < noOfQuestions; i++) {
                int countryId = random_index_generator(listOfCountries.size());

                if (countryBitmap[countryId]) {
                    i--;
                    continue;
                }

                questionAndOptions[i].question = listOfCountries.get(countryId);
                countryBitmap[countryId] = true;
                countryAnswers.add(countryInContinent.get(countryId));
                int optionId = random_index_generator(noOfOptions);
                set_options(optionId, countryId, questionAndOptions[i], countryInContinent);

                for (int j = 0; j < uniqueContinents.size(); j++) {
                    if (uniqueContinents.get(j).equals(countryInContinent.get(countryId))) {
                        continentBitmap[j] = true;
                        break;
                    }
                }
                fill_remaining_options(questionAndOptions[i], uniqueContinents, continentBitmap);
            }
        }
    }

    public static QuizQuestions[] get_questions_on_continents() {
        return questionAndOptions;
    }

    private static int get_country_id(String countryName) {
        for (int i = 0; i < listOfCountries.size(); i++) {
            if (listOfCountries.get(i).equals(countryName)) {
                return i;
            }
        }

        return -1;
    }

    public static void design_neighbors_questions() {
        int correctOption = -1;

        if (neighborsAndOptions == null) {
            neighborsAndOptions = new QuizQuestions[noOfQuestions];

            for (int i = 0; i < noOfQuestions; i++) {
                neighborsAndOptions[i] = new QuizQuestions();
                neighborsAndOptions[i].question = null;
                neighborsAndOptions[i].option0 = null;
                neighborsAndOptions[i].option1 = null;
                neighborsAndOptions[i].option2 = null;
                neighborsAndOptions[i].option3 = null;
            }

            for (int i = 0; i < noOfQuestions; i++) {
                int countryId = random_index_generator(listOfCountries.size());

                if (countryBitmap[countryId]) {
                    i--;
                    continue;
                }

                neighborsAndOptions[i].question = listOfCountries.get(countryId);
                countryBitmap[countryId] = true;
                String neighboursJson = neighborsOfCountry.get(countryId);
                JSONObject countryJson;

                try {
                    countryJson = new JSONObject(neighboursJson);
                    JSONArray neighboursArrJson = countryJson.getJSONArray(listOfCountries.get(countryId));

                    int optionId = random_index_generator(noOfOptions);

                    if (neighboursArrJson.length() > 0) {
                        String countryName = neighboursArrJson.getString(0);
                        neighborsAnswers.add(countryName);
                        correctOption = get_country_id(countryName);
                        set_options(optionId, correctOption, neighborsAndOptions[i],
                                    listOfCountries);
                    }

                    fill_remaining_neighbor_options(neighborsAndOptions[i], neighboursArrJson,
                                                    countryId, correctOption);
                    neighborsAndOptions[i].option3 = "No Neighbors";
                } catch (Exception e) {
                    Log.d(SqliteDbHelper.Sqlite, "Exception occurred : "+e);
                }
            }
        }
    }

    public static QuizQuestions[] get_questions_on_neighbors() {
        return neighborsAndOptions;
    }

    public static int verify_answers(ArrayList<String>continents, ArrayList<String>neighbors) {
        int score = 0;

        for (int i = 0; i < continents.size(); i++) {
            if (continents.get(i) == null) {
                continue;
            }

            if (continents.get(i).equals(countryAnswers.get(i))) {
                score++;
            }
        }

        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i) == null) {
                continue;
            }

            if (neighbors.get(i).equals(neighborsAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }
}
