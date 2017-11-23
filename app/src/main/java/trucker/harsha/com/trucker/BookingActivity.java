package trucker.harsha.com.trucker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.percolate.caffeine.ViewUtils;

public class BookingActivity extends AppCompatActivity {
    String username;
    String source, destination;
    LatLng pickUpLocation;
    EditText destinationET;
    private Spinner goodTypeSpinner;
    private Spinner vehicleTypeSpinner;
    private int place_autocomplete_request_code;

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
        //username = ParseUser.getCurrentUser().getUsername();
        username = "Sanath";
        destinationET = ViewUtils.findViewById(this, R.id.destination_edit_text);
        getDetails();



    }

    void getDetails()
    {
        Intent intent = getIntent();
        source = intent.getStringExtra("address");
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);
        pickUpLocation = new LatLng(latitude,longitude);
        EditText sourceEt =  ViewUtils.findViewById(this,R.id.pick_up_edittext);
        sourceEt.setText(source);
        sourceEt.setClickable(false);

    }

    public void onConfirmBooking(View view)
    {
        destination = destinationET.getText().toString();
        goodTypeSpinner = ViewUtils.findViewById(this, R.id.good_type);
        int goodType = goodTypeSpinner.getSelectedItemPosition() - 1;
        vehicleTypeSpinner = ViewUtils.findViewById(this, R.id.vehicle_type);
        int vehicleType = vehicleTypeSpinner.getSelectedItemPosition() - 1;

        RadioGroup radioGroup = ViewUtils.findViewById(this, R.id.radiogroup);
        if(!areEntriesValid(goodType, vehicleType, radioGroup))
            return;
        int paymentMode = getPaymentMode(radioGroup.getCheckedRadioButtonId());

        ParseGeoPoint point = new ParseGeoPoint(pickUpLocation.latitude,pickUpLocation.longitude);
        ParseObject request = new ParseObject("Request");
        request.put("location", point);
        request.put("username", username);
        request.put("status", "accepted");
        request.put("goodType", goodType);
        request.put("vehicleType", vehicleType);
        request.put("paymentMode", paymentMode);
        request.put("amount", 0);
        request.put("destination", destination);

        //TODO: Catch all types of exceptions
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    showToast("Booking accepted");
                    Intent inte = new Intent(BookingActivity.this, trackTrip.class);
                    startActivity(inte);
                    finish();
                }
                else
                {
                    showToast("Error submitting request");
                }
            }
        });

    }

    int getPaymentMode(int id)
    {
        switch (id)
        {
            case R.id.cash_radiobutton : return 0;
            case R.id.paytm_radiobutton : return 1;
            case R.id.cc_radiobutton : return 2;
            default: return 0;
        }
    }

    RadioGroup initializeRadioGroup()
    {
        RadioGroup radioGroup = ViewUtils.findViewById(this, R.id.radiogroup);
        RadioButton cashButton = ViewUtils.findViewById(this, R.id.cash_radiobutton);
        RadioButton payTMButton = ViewUtils.findViewById(this, R.id.paytm_radiobutton);
        RadioButton ccButton = ViewUtils.findViewById(this, R.id.cc_radiobutton);
        //radioGroup.addView(cashButton);
        //radioGroup.addView(payTMButton);
        //radioGroup.addView(ccButton);
        return  radioGroup;
    }



    void showToast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    boolean areEntriesValid(int vehicleType, int goodType, RadioGroup radioGroup)
    {
        boolean isValid = true;
        if(destination.equals("")) {
            showToast("Enter valid destination");
            isValid = false;
        }
        if(vehicleType == -1) {
            showToast("Please select vehicle type");
            isValid = false;
        }
        if(goodType == -1)
        {
            showToast("Please select good type");
            isValid = false;
        }
        if(radioGroup.getCheckedRadioButtonId()== -1)
        {
            showToast("Please select payment option");
            isValid= false;
        }

        return isValid;
    }

    public void placeSearch(View view)
    {
        place_autocomplete_request_code = 1;
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, place_autocomplete_request_code);
        } catch (GooglePlayServicesRepairableException e) {
            showToast("Error loading places!");
        } catch (GooglePlayServicesNotAvailableException e) {
            showToast("Error loading places!");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == place_autocomplete_request_code) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String address = place.getName().toString();//Check for place address
                destinationET.setText(address);
                Log.i("Place", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                showToast("Error retrieving place");
                // TODO: Handle the error.
                Log.i("Place", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }




}
