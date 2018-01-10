package com.harsha.trucker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PastTripsActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    PastTripsAdapter adapter;
    List<OldTrip> tripList;

    Toolbar mToolbar;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_trips);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tripList = new ArrayList<>();

        final ParseQuery<ParseObject>query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    for (int i=0;i<objects.size();i++) {
                        ParseObject object = objects.get(i);
                        OldTrip old =new OldTrip(
                        // OldTrip oldTrip = new OldTrip();
                          object.getString("source"),
                          object.getString("username"),
                          object.getString("source"),
                          object.getString("destination"),
                          object.getString("amount"),
                          object.getString("updatedAt"),
                          object.getString("goodType")
                        );
                        tripList.add(old);


                    }
                    adapter = new PastTripsAdapter(tripList,getApplicationContext());
                    recyclerView.setAdapter(adapter);






                    }else{
                    showToast("loading unsuccessful");
                }

            }
        });



    }
    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




}
