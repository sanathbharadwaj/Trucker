package com.harsha.trucker;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class OurRatesActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_rates);

        Toolbar toolbar = (Toolbar) findViewById(R.id.nav_action_bar);
        setSupportActionBar(toolbar);
        if (toolbar!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }



    }
}
