package trucker.harsha.com.trucker;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
     /*   Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("7504ba215dd57dcd2585e9406d50832ed4dda57e")
                .clientKey("1aea9245f094279bdd6bd46419fb565da1500a61")
                .server("http://18.220.173.24:80/parse")
                .build()
        );

        */

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myAppId")
                .clientKey("myClient")
                .server("http://192.168.23.121:1337/parse")
                .build()
        );


        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);



    }
}


