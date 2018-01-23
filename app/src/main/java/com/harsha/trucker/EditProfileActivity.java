package com.harsha.trucker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import static com.harsha.trucker.Utilities.*;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    Context context;
    @Override
    public void onBackPressed() {
        loadActivityAndFinish(context,ProfileActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context  = this;


        loadUserData();
    }

    void loadUserData() {

        ParseQuery query = new ParseQuery("User");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    showToast("Failed to load user data");
                    return;
                }
                updateUserUI();
            }
        });
    }

    void updateUserUI() {
        getTextView(R.id.name_ed).setText(ParseUser.getCurrentUser().getString("name"));
        getTextView(R.id.phone_no).setText(ParseUser.getCurrentUser().getString("phone"));
        getTextView(R.id.email_add_ed).setText(ParseUser.getCurrentUser().getEmail());

    }
    TextView getTextView(int id)
    {
        return (TextView)findViewById(id);
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void updateProfile(View view){
        /*ParseUser.getCurrentUser().put("name",getTextView(R.id.name_ed).getText().toString());
        ParseUser.getCurrentUser().put("email",getTextView(R.id.email_add_ed).getText().toString());
        updateUserUI();
        loadToActivity();*/
        ParseUser use = ParseUser.getCurrentUser();
        use.put("name",getTextView(R.id.name_ed).getText().toString());
        //use.put("email",getTextView(R.id.email_add_ed).getText().toString());
        use.setEmail(getTextView(R.id.email_add_ed).getText().toString());
        use.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e!=null) {
                    //e.printStackTrace();
                    showToast("Failed to update" +
                            "Field Already exists");
                    loadActivityAndFinish(context, EditProfileActivity.class);


                }else{
                    showToast("Updated Successfully");
                    //loadActivityAndFinish(context, MapsActivity.class);
                    finish();

                }
            }
        });





    }


}
