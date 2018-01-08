package com.harsha.trucker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.harsha.trucker.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private GoogleMap mMap;
    ParseObject use;
    NavigationView navigationView;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation, pickUpLocation;
    String selectedAddress;
    public Location lastLocation;
    GoogleMap.OnCameraIdleListener onCameraIdleListener;
    TextView addressBar;
    boolean recentered = false;

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void Logout(){
        ParseUser.logOut();
        showToast("Logged Out Successfully");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        mToolbar = (Toolbar) findViewById(R.id.nav_action_bar);
        setSupportActionBar(mToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addressBar = (TextView) findViewById(R.id.pick_up_edittext);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.profile:
                        Intent h1 = new Intent(MapsActivity.this, ProfileActivity.class);
                        startActivity(h1);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.current_trips:
                        Intent h2 = new Intent(MapsActivity.this, CurrentTripsActivity.class);
                        startActivity(h2);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.past_trips:
                        Intent h3 = new Intent(MapsActivity.this, PastTripsActivity.class);
                        startActivity(h3);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.our_rates:
                        Intent h4 = new Intent(MapsActivity.this, OurRatesActivity.class);
                        startActivity(h4);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.payment_option:
                        Intent h5 = new Intent(MapsActivity.this, PaymentOptionsActivity.class);
                        startActivity(h5);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.about_us:
                        Intent h6 = new Intent(MapsActivity.this, AboutUsActivity.class);
                        startActivity(h6);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.contact_us:
                        Intent h7 = new Intent(MapsActivity.this, ContactUsActivity.class);
                        startActivity(h7);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.support:
                        Intent h8 = new Intent(MapsActivity.this, SupportActivity.class);
                        startActivity(h8);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.logout:
                        Logout();
                        Intent h9 = new Intent(MapsActivity.this, UserChoiceActivity.class);
                        startActivity(h9);
                        mDrawerLayout.closeDrawers();
                        finish();
                        break;


                }

                return false;
            }
        });
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
        getTextView(R.id.profile_text).setText(ParseUser.getCurrentUser().getString("name"));
        getTextView(R.id.phone_text).setText(ParseUser.getCurrentUser().getString("phone"));
    }





    TextView getTextView(int id)
    {
        return (TextView)findViewById(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void search(String locationText) {
        //EditText location_tf = (EditText) findViewById(R.id.pick_up_edittext);
        //String locationText = location_tf.getText().toString();
        //selectedAddress = locationText;
        List<Address> addressList = null;

        if (!locationText.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locationText, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList.size() == 0)
            {
                showToast("Please restart your phone");
                return;
            }
            Address address = addressList.get(0);
            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
            pickUpLocation.setLatitude(latlng.latitude);
            pickUpLocation.setLongitude(latlng.longitude);
            mMap.clear();
            //mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
            //TODO: initialize location with lastKnownLocation if no GPS enabled
            //TODO: alert user if gps disabled
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        configureCameraIdle();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                currentLocation = new Location(location);
                updateLocation(location);
                lastLocation = location;
                if (!recentered) {
                    moveCamera(location);
                    recentered = true;
                    pickUpLocation = new Location(location);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                //TODO: Call GPS functions again
            }

            @Override
            public void onProviderDisabled(String s) {
                //TODO: Alert user and navigate to user settings
                showToast("Please enable GPS");

            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startLocation();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                startLocation();
            }
        }
    }

    void updateLocation(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17));
        mMap.clear();
        //mMap.addMarker(new MarkerOptions().position(userLocation));
    }

    void startLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        //TODO: put it to while
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        pickUpLocation = lastKnownLocation;
        if (lastKnownLocation != null)
            moveCamera(lastKnownLocation);
        if (lastKnownLocation != null) {
            updateLocation(lastKnownLocation);
            setEditText(lastKnownLocation);
        }
    }

    void setEditText(Location location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddress != null && listAddress.size() > 0) {
                String address = refineAddress(listAddress.get(0));
                selectedAddress = address;
                addressBar.setText(address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void moveCamera(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startLocation();
        }
    }


    String refineAddress(Address address) {
        String result = "";
        if (address.getAddressLine(0) != null) {
            result += address.getAddressLine(0) + ", ";
        }
        if (address.getAddressLine(1) != null) {
            result += address.getAddressLine(1) + ", ";
        }
        if (address.getAddressLine(2) != null) {
            // result += address.getAddressLine(2);
        }

        return result;
    }

    public void bookNow(View view) {
        try {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("latitude", pickUpLocation.getLatitude());
            intent.putExtra("longitude", pickUpLocation.getLongitude());
            intent.putExtra("address", selectedAddress);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gpsRecenter(View view) {
        if (lastLocation != null) {
            moveCamera(lastLocation);
            setEditText(lastLocation);
        }
    }

    void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = mMap.getCameraPosition().target;
                Location location = new Location("dummy");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                pickUpLocation = new Location(location);
                setEditText(location);
            }
        };

        mMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    int place_autocomplete_request_code = 1;

    public void placeSearch(View view) {
        place_autocomplete_request_code = 1;
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, place_autocomplete_request_code);
        } catch (Exception e) {
            showToast("Error loading places!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == place_autocomplete_request_code) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String address = place.getAddress().toString();// TODO: Check for place address
                addressBar.setText(address);
                selectedAddress = address;
                search(address);
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

