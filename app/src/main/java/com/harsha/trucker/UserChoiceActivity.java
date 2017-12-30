package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class UserChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choice);

        if(ParseUser.getCurrentUser() != null)
            loadToMapsActivity();


        Button button = (Button) findViewById(R.id.register_button);

        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(UserChoiceActivity.this,
                        RegistrationActivity.class);
                startActivity(myIntent);
            }
        });
        Button buttonl = (Button) findViewById(R.id.login_button);

        // Capture button clicks
        buttonl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent intent = new Intent(UserChoiceActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    void loadToMapsActivity()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
