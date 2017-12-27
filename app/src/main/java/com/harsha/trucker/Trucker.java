package com.harsha.trucker;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by Admin on 9/19/2017.
 */

public class Trucker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
       /* Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("7504ba215dd57dcd2585e9406d50832ed4dda57e")
                .clientKey("1aea9245f094279bdd6bd46419fb565da1500a61")
                .server("http://18.220.173.24:80/parse")
                .build()
        ); */

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("anotherId")
                .clientKey("myClientChettinad")
                .server("http://52.14.72.244:1337/parse")
                .build()
        );

        ParsePush.subscribeInBackground("");

        ParseInstallation.getCurrentInstallation().saveInBackground();


        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);



    }
}


