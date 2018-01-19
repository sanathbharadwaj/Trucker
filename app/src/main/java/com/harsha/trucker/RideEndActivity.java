package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import static com.harsha.trucker.Utilities.loadActivityAndFinish;

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
        final RatingBar rateDriver = (RatingBar)findViewById(R.id.rate_driver_star);
        ParseQuery query = new ParseQuery("Request");
        Intent intent = getIntent();
        String objectId = intent.getStringExtra("id");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if(e!=null)
                {
                    showToast("Error Submitting");
                    return;
                }
                object.put("rate",rateDriver.getRating());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null) {
                            //e.printStackTrace();
                            showToast("Failed to submit");

                        }else{
                            showToast("Submitted Successfully");
                            //loadActivityAndFinish(context, MapsActivity.class);
                            finish();

                        }
                    }
                });
            }
        });
        Intent intent1 = new Intent(this, MapsActivity.class);
        startActivity(intent1);
        finish();
    }


    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
