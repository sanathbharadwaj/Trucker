package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class RideEndActivity extends AppCompatActivity {

    private TextView totalCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_end);
        totalCash = findViewById(R.id.total_cash);
        setCash();
    }

    void setCash()
    {
        ParseQuery query = new ParseQuery("Request");
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("id");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e!=null)
                {
                    setCash();
                    return;
                }
                int totalFare = object.getInt("amount");
                totalCash.setText("Rs " + totalFare);
            }
        });
    }

    public void done(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
