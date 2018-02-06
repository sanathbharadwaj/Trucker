package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import static com.harsha.trucker.Utilities.*;

import java.util.HashMap;

public class RideEndActivity extends AppCompatActivity {

    private TextView totalCash;
    private ParseObject request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_end);
        totalCash = findViewById(R.id.total_cash);
        setCash();



    }

    void setCash()
    {
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
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
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
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
                request = object;
                object.put("rating",rateDriver.getRating());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null) {
                            //e.printStackTrace();
                            showToast("Failed to submit");

                        }else{
                            showToast("Submitted Successfully");
                            //loadActivityAndFinish(context, MapsActivity.class);
                            updateAverageRating(rateDriver.getRating());
                        }
                    }
                });
            }
        });
    }

    void updateAverageRating(float rating)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("rating", Float.toString(rating));
        params.put("driverId", request.getString("driverId"));
        ParseCloud.callFunctionInBackground("updateAverageRating", params, new FunctionCallback<Integer>() {
            @Override
            public void done(Integer object, ParseException e) {
                if(e!=null || object.equals(0)){ showToast("Network error occured, please try again"); return;}
                loadActivityAndFinish(RideEndActivity.this, MapsActivity.class);
            }
        });
    }


    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
