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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation, pickUpLocation;
    String selectedAddress;
    public Location lastLocation;
    GoogleMap.OnCameraIdleListener onCameraIdleListener;
    EditText addressBar;
    boolean recentered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addressBar = (EditText) findViewById(R.id.pick_up_edittext);


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
                finish();

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

