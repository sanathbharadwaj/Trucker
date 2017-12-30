package com.harsha.trucker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.harsha.trucker.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        TextView textl = (TextView) findViewById(R.id.log_acc);

        // Capture button clicks
        textl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent intent = new Intent(RegistrationActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onSubmit(View view)
    {
        ParseUser user = new ParseUser();
        user.setUsername(getEditText(R.id.username).getText().toString());
        user.setPassword(getEditText(R.id.password).getText().toString());
        user.setEmail(getEditText(R.id.email).getText().toString());
        user.put("phone", getEditText(R.id.phone_number).getText().toString());
        user.put("isDriver", false);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null)
                {
                    //TODO: Handling failures
                    showToast("Registration error");
                    return;
                }
                showToast("Registration successful");
                loadToMapsActivity();

            }
        });
    }

    void loadToMapsActivity()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }


    EditText getEditText(int id)
    {
        return (EditText)findViewById(id);
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
