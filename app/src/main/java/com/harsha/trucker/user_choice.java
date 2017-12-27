package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class user_choice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choice);

        Button button = (Button) findViewById(R.id.register_button);

        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(user_choice.this,
                        RegistrationActivity.class);
                startActivity(myIntent);
            }
        });
        Button buttonl = (Button) findViewById(R.id.login_button);

        // Capture button clicks
        buttonl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent intent = new Intent(user_choice.this,
                        login.class);
                startActivity(intent);
            }
        });
    }
}
