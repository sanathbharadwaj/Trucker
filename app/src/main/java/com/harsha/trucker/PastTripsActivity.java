package com.harsha.trucker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class PastTripsActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    PastTripsAdapter adapter;
    List<OldTrip> tripList;
    private ProgressBar imgLoad;
    private ImageView imgBack1;

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

        loadData();





    }

    private void loadData(){

        final ParseQuery<ParseObject>query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    imgLoad = (ProgressBar)findViewById(R.id.loader);
                    imgBack1 = (ImageView)findViewById(R.id.img_back1);
                    imgBack1.setVisibility(View.VISIBLE);
                    imgLoad.setVisibility(View.VISIBLE);
                    for (int i=0;i<objects.size();i++) {
                        ParseObject object = objects.get(i);
                        int a = object.getInt("amount");
                        Date date = object.getCreatedAt();
                        long rideDuration = (object.getUpdatedAt().getTime()-
                                object.getCreatedAt().getTime())/(1000*60*60*24);

                        OldTrip old =new OldTrip(
                                // OldTrip oldTrip = new OldTrip();
                                object.getString("source"),
                                object.getString("username"),
                                object.getString("source"),
                                object.getString("destination"),
                                String.valueOf(a),
                                date,
                                String.valueOf(rideDuration)
                        );
                        imgBack1.setVisibility(View.GONE);
                        imgLoad.setVisibility(View.GONE);
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
