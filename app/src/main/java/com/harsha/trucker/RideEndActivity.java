package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RideEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_end);
    }

    public void done(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
