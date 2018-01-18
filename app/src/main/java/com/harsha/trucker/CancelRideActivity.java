package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import com.harsha.trucker.R;

public class CancelRideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_ride);
    }

    public void submit(View view)
    {
        Intent intent = getIntent();
        String requestId = intent.getStringExtra("requestId");
        RadioGroup radioGroup = findViewById(R.id.cancel_reasons);
        int id = radioGroup.getCheckedRadioButtonId();
        if(id == -1)
        {
            showToast("Please select one option");
            return;
        }
        int index = radioGroup.indexOfChild((RadioButton)findViewById(id));
        ParseObject object = new ParseObject("CanceledRide");
        object.put("reason", index);
        object.put("requestId", requestId);
        object.saveInBackground();

        ParseQuery query = new ParseQuery("Request");
        query.getInBackground(requestId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.put("status", "cancelled");
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null) {
                            showToast("Error canceling ride!!");
                            return;
                        }
                        loadMapsActivity();
                    }
                });
            }
        });
    }


    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void loadMapsActivity()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
