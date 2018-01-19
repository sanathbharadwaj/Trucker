package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private ImageView imgBack;
    private ImageView imgBack1;
    private ProgressBar imgLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView text = (TextView) findViewById(R.id.create_acc);

        // Capture button clicks
        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent intent = new Intent(LoginActivity.this,
                        RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



    public void logIn(View view){
        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        imgBack=(ImageView) findViewById(R.id.img_back);
        imgLoad = (ProgressBar)findViewById(R.id.loader);
        imgBack1 = (ImageView)findViewById(R.id.img_back1);


        if (username.getText().toString().matches("")||password.getText().toString().matches("")){
            showToast("Please fill all fields");
        }else {
                        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {

                                if (user != null) {
                                    //imgBack.setVisibility(View.VISIBLE);
                                    //imgLoad.setVisibility(View.VISIBLE);
                                    imgBack.setVisibility(View.VISIBLE);
                                    imgLoad.setVisibility(View.VISIBLE);
                                    imgBack1.setVisibility(View.VISIBLE);

                                    showToast("Login Successful");
                                    loadToMapsActivity();
                                    finish();
                                } else {
                                    //imgBack.setVisibility(View.GONE);
                                    //imgLoad.setVisibility(View.GONE);
                                    showToast(e.getMessage());
                                }
                                //imgBack.setVisibility(View.GONE);
                                //imgLoad.setVisibility(View.GONE);
                            }
                        });
                    }

        }


    void loadToMapsActivity()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
