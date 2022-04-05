package edu.uga.cs.geographyquiz;

import static edu.uga.cs.geographyquiz.SqliteDbHelper.CONTINENT;
import static edu.uga.cs.geographyquiz.SqliteDbHelper.NEIGHBORS;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    SqliteDbHelper db = null;
    SQLiteDatabase dbInstance;
    private static final String DEBUG_TAG = "Sqlite";

    // Help Screen variables

        TextView helpDialog;
        private Button help;
        private Button StartQuiz;
        private Button ViewResults;
        String txt;
        public static final String MESSAGE_TYPE = "Simple Message";

    // End of Help Screen variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = SqliteDbHelper.getDbInstance(getApplicationContext());
        dbInstance = SqliteDbHelper.open(db);

        // For the help screen navigation

        help = (Button) findViewById(R.id.Help);
        StartQuiz = (Button) findViewById(R.id.StartQuiz);
        ViewResults = findViewById(R.id.ViewResults);

        StartQuiz.setOnClickListener(new startQuizClickListener());

        ViewResults.setOnClickListener(new ViewResultsClickListener());

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputStream inputStream = getResources().openRawResource(R.raw.help);
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    while(inputStream.read(buffer) != -1){
                        txt = new String(buffer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent( view.getContext(), HelpScreen.class );
                String message = txt;
                intent.putExtra( MESSAGE_TYPE, message );
                startActivity( intent );

            }
        });
        // Help screen navigation code ends here

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

    private static class ViewResultsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewResultsActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class startQuizClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(view.getContext(), startQuiz.class);
            view.getContext().startActivity(intent);
        }
    }
}