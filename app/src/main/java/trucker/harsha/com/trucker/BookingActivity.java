package trucker.harsha.com.trucker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class BookingActivity extends AppCompatActivity {
    String username;

    public enum PaymentMode
    {
        CASH,
        PAYTM,
        TRUCKER_WALLET
    }

    public enum GoodType
    {
        ELECTRONICS,
        FRAGILE,
        FOOD,
        FURNITURE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Intent intent = getIntent();
        username = ParseUser.getCurrentUser().getUsername();

    }

    public void onConfirmBooking()
    {
        LatLng latLng = new LatLng(23,35);
        ParseGeoPoint point = new ParseGeoPoint(latLng.latitude,latLng.longitude);
        ParseObject request = new ParseObject("Request");
        request.put("location", point);
        request.put("username", username);
        request.put("isProcessed", false);
        request.put("goodType", GoodType.ELECTRONICS);
        request.put("paymentMode", PaymentMode.CASH);

        //TODO: Catch all types of exceptions
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    showToast("Booking under process!");
                    finish();
                }
                else
                {
                    showToast("Error submitting request");
                }
            }
        });

    }

    void showToast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }


}
