package com.harsha.trucker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TrackTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    boolean recentered = false;
    private boolean assigned;
    private boolean driverLoading;
    private String driverId;
    private Marker driverMarker;
    private String requestId;
    public static Status status = Status.ACCEPTED;
    private Marker userMarker;
    private ProgressBar imgLoad;
    private PolylineOptions polylineOptions;
    List<LatLng> driverLocations = new ArrayList<LatLng>();
    private Polyline polyline;
    private ParseObject request;
    private ParseObject driver;
    private SharedPreferences.Editor editor;
    private String idFromLocal;
    private List<LatLng> latLngs;

    public enum Status{
        ACCEPTED, ASSIGNED, ARRIVED, STARTED, FINISHED, CANCELED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_trip);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findCurrentTrip();
        initializeButtons();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ASSIGNED");
        intentFilter.addAction("ARRIVED");
        intentFilter.addAction("STARTED");
        intentFilter.addAction("FINISHED");
        intentFilter.addAction("CANCELLED");
        registerReceiver(broadcastReceiver, intentFilter);
        polylineOptions = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        status = Status.ACCEPTED;
    }

    void findCurrentTrip()
    {
        SharedPreferences prefs = getSharedPreferences("com.harsha.trucker", MODE_PRIVATE);
        editor = getSharedPreferences("com.harsha.trucker", MODE_PRIVATE).edit();
        boolean isRunning = prefs.getBoolean("isRunning", false);
        if(!isRunning) {
            editor.putBoolean("isRunning", true);
            editor.apply();
            searchDriver();
            return;
        }
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        idFromLocal = prefs.getString("requestId", null);
        if(idFromLocal ==null)
        {
            showToast("Error loading current trip");
            finish();
        }
        query.getInBackground(idFromLocal, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                request = object;
                updateRideStatus(object);
            }
        });
    }

    void updateRideStatus(ParseObject mRequest)
    {

        switch (mRequest.getString("status"))
        {
            case "accepted":searchDriver(); status = Status.ACCEPTED;
                            break;
            case "assigned": searchDriver();break;
            case "arrived": searchDriver(); driverArrived();break;
            case "started": searchDriver(); rideStarted();break;
            case "finished": rideFinished();break;
            case "cancelled": rideCancelled();break;
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null) return;
            switch (action)
            {
                case "ASSIGNED" : break;
                case "ARRIVED" : driverArrived(); break;
                case "STARTED" : rideStarted();break;
                case "FINISHED": rideFinished();break;
                case "CANCELLED" : rideCancelled();break;
                default:break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        locationManager.removeUpdates(locationListener);
    }

    void driverArrived()
    {
        getTextView(R.id.status_text).setText("Driver arrived at your location");
        status = Status.ARRIVED;
    }

    void rideStarted()
    {
        locationManager.removeUpdates(locationListener);
        getTextView(R.id.status_text).setText("Ride in progress");
        status = Status.STARTED;
    }


    void rideFinished()
    {
        status = Status.FINISHED;
        editor.putBoolean("isRunning", false);
        editor.apply();
        Intent intent = new Intent(this, RideEndActivity.class);
        intent.putExtra("id", request.getObjectId());
        startActivity(intent);
        finish();
    }

    void rideCancelled()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        editor.putBoolean("isRunning", false);
        editor.commit();
        finish();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
                if (!recentered) {
                    moveCamera(location);
                    recentered = true;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                showSettingsAlert();
            }

            @Override
            public void onProviderDisabled(String s) {
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

    void startLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null)
            moveCamera(lastKnownLocation);
        if (lastKnownLocation != null) {
            updateLocation(lastKnownLocation);
        }
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void moveCamera(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
    }

    void updateLocation(Location location) {
        if(status == Status.STARTED) {
            userMarker.remove();
            locationManager.removeUpdates(locationListener);
            return;
        }
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17));
        if(userMarker != null)
            userMarker.remove();
     /*   if(status == Status.STARTED) {
            userMarker = mMap.addMarker(new MarkerOptions().position(userLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            updatePolylines(location);
        }
        else  */
            userMarker = mMap.addMarker(new MarkerOptions().position(userLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    void updatePolylines(List<LatLng> latLngs)
    {

       // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //driverLocations.addAll(latLngs);
        if(polyline == null)
            polyline = mMap.addPolyline(polylineOptions);
        polyline.setPoints(latLngs);

    }


    void initializeButtons()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.call_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ driver.getString("phoneNumber")));
                startActivity(intent);
            }
        });
    }

    void searchDriver()
    {
        imgLoad = (ProgressBar)findViewById(R.id.loader);
        driverLoading = false;
        Intent intent = getIntent();
        requestId = intent.getStringExtra("requestId");
        if(requestId == null)
        {
            requestId = idFromLocal;
        }
        editor.putString("requestId", requestId);
        editor.apply();
        final Handler handler = new Handler();
        assigned = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!driverLoading) {
                    imgLoad.setVisibility(View.VISIBLE);
                    driverLoading = true;
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
                    query.getInBackground(requestId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            request = object;
                            if (!object.getString("status").equals("accepted")) {
                                getDriverDetails(object);
                                status = Status.ASSIGNED;
                                assigned = true;
                                imgLoad.setVisibility(View.GONE);
                            }
                            driverLoading = false;
                        }

                    });
                }
                if(!assigned)
                    handler.postDelayed(this, 3000);
            }
        }, 100);
    }
    void getDriverDetails(ParseObject object)
    {
        driverId = object.getString("driverId");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Driver");
        query.getInBackground(driverId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                driver = object;
                updateDriverUI(object);
                getDriverImage(object);
                getDriverLocation();
            }

        });
    }

    void getDriverLocation()
    {
        final Handler handler = new Handler();
        //TODO: improve this code
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> query = new ParseQuery<>("Request");
                query.getInBackground(request.getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        /*List<ParseGeoPoint> points = object.getList("driverPath");
                        if(points == null || points.size()==0)
                            return;
                        ParseGeoPoint point = points.get(points.size()-1);*/
                        latLngs = new ArrayList<>();
                        ParseFile file = object.getParseFile("driverPath");
                        if(file!=null)
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Type listType = new TypeToken<List<LatLng>>() {}.getType();
                                String pathJSON = new String(data);
                                Gson gson = new Gson();
                                latLngs = gson.fromJson(pathJSON, listType);
                                if(latLngs == null || latLngs.size()==0)
                                    return;
                                updateDriverLocation();
                            }
                        });

                    }
                });
                if(status != Status.FINISHED)
                handler.postDelayed(this, 10000);

            }
        }, 1000);
    }

    private void updateDriverLocation() {
        LatLng point = latLngs.get(latLngs.size()-1);
        if(driverMarker != null)
            driverMarker.remove();
        LatLng latLng = new LatLng(point.latitude, point.longitude);
        if(status == Status.STARTED) {
            updatePolylines(latLngs);
        }
        driverMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    void getDriverImage(final ParseObject object)
    {
        ParseFile file = (ParseFile)object.get("profilePic");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e != null)
                {
                    showToast("Error retrieving driver image");
                    return;
                }
                setDriverImageView(data);
            }
        });
    }

    void setDriverImageView(byte[] data)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
        ImageView imageView = (ImageView) findViewById(R.id.driver_img);
        imageView.setImageBitmap(bitmap);
    }

    void updateDriverUI(ParseObject object)
    {
        findViewById(R.id.driver_search).setVisibility(View.INVISIBLE);
        findViewById(R.id.driver_img).setVisibility(View.VISIBLE);

        TextView name = findViewById(R.id.driver_name);
        name.setVisibility(View.VISIBLE);
        name.setText(object.getString("username"));

        TextView vehicleNumber = findViewById(R.id.vehicle_num);
        vehicleNumber.setVisibility(View.VISIBLE);
        vehicleNumber.setText(object.getString("vehicleNumber"));

        TextView vehicleName = findViewById(R.id.vehicle_name);
        vehicleName.setVisibility(View.VISIBLE);
        vehicleName.setText(object.getString("vehicleName"));

        findViewById(R.id.ride_cancel).setVisibility(View.VISIBLE);
        findViewById(R.id.call_button).setVisibility(View.VISIBLE);
    }

    TextView getTextView(int id)
    {
        return (TextView)findViewById(id);
    }

    List<LatLng> geoPointToLatLng(List<ParseGeoPoint> points)
    {
        List<LatLng> latLngs = new ArrayList<>();
        for(ParseGeoPoint point : points){
            latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        return  latLngs;
    }

    @Override
    public void onBackPressed()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Please enable GPS");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void cancelRide(View view)
    {
        Intent intent = new Intent(this, CancelRideActivity.class);
        intent.putExtra("requestId", requestId);
        startActivity(intent);
    }
}
