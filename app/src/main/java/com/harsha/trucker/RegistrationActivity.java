package com.harsha.trucker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.harsha.trucker.R;

public class RegistrationActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    final String USER_REGISTERED = "isRegistered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        preferences = getSharedPreferences("com.harsha.trucker",MODE_PRIVATE);
        if(preferences.getBoolean(USER_REGISTERED,false))
        {
            loadToMapsActivity();
        }
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
                    showToast("Registration error");
                    return;
                }
                showToast("Registration successful");
                editor = preferences.edit();
                editor.putBoolean(USER_REGISTERED, true);
                editor.apply();
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
