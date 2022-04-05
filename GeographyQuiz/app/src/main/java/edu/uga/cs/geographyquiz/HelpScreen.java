package edu.uga.cs.geographyquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelpScreen extends AppCompatActivity{

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);


        TextView helpText = findViewById(R.id.helpDialog);
        Intent intent = getIntent();
        String message = intent.getStringExtra( MainActivity.MESSAGE_TYPE );

        if( message == null )
            helpText.setText( "No message received" );
        else
            helpText.setText( message );

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}