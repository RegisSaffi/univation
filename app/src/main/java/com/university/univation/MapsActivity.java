package com.university.univation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public static double latitude;
    public static double longitude;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    SharedPreferences preferences;

    ProgressBar progressBar;

    building building;
    List<building> buildings1;

    RecyclerView recyclerView;
    buildingAdapter buildingAdapter;

    BottomSheetBehavior sheetBehavior;

    ImageView city,toggle;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildings1=new ArrayList<>();
        progressBar=findViewById(R.id.progress);
        toggle=findViewById(R.id.toggle);

        search=findViewById(R.id.search);
        preferences=getSharedPreferences("univation",MODE_PRIVATE);

        recyclerView=findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buildingAdapter=new buildingAdapter(buildings1, this, loc -> {

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,20));

        });
        recyclerView.setAdapter(buildingAdapter);

        city=findViewById(R.id.city);
        city.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.ic_location_city_black_24dp,getTheme()));
        toggle.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.ic_keyboard_arrow_up_black_24dp,getTheme()));


        sheetBehavior=BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        toggle.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.ic_keyboard_arrow_down_black_24dp,getTheme()));


                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        toggle.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.ic_keyboard_arrow_up_black_24dp,getTheme()));

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleBottomSheet();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void toggleBottomSheet(){
        if(sheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }

        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
//        LatLng cst = new LatLng(-1.956512, 30.064607);
//        mMap.addMarker(new MarkerOptions().position(cst).title("University of Rwanda-CST"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cst,17));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-1.958143,30.063868),18));

        if(preferences.contains("buildings")){
            decode(preferences.getString("buildings","none"));
        }

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("context", "buildings");

        String query = builder.build().getEncodedQuery();
        getData gd=new getData();
        gd.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,query);

//        mMap.setOnMapClickListener(latLng -> {
//            mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked location"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//
//        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Where you are now!");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        mMap.addMarker(markerOptions);

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    //////////////////////////////////////////////////////
    private class getData extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;
        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            // TODO: Implement this method
            super.onPreExecute();
        }


        protected String doInBackground(String... params) {

            try {
                //url = new URL("https://bazaltd.000webhostapp.com/search.php");
                url=new URL("http://baza.itdevs.rw/univation/api.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL

                String query = params[0];
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
           if(result.equals("exception")){
               Toast.makeText(MapsActivity.this,"No internet",Toast.LENGTH_LONG).show();

           }else
            if(result.substring(0,1).equals("["))
            {
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("buildings",result);
                editor.apply();

                decode(result);
            }
            else
            {
                Toast.makeText(MapsActivity.this,result,Toast.LENGTH_LONG).show();

            }
        }}

        public void decode(String json){

        mMap.clear();
            try {

                JSONArray array = new JSONArray(json);
                for (int a = 0; a < array.length(); a++) {
                    JSONObject obj = array.getJSONObject(a);

                    String name = obj.getString("building_name");
                    String desc = obj.getString("building_description");
                    //String image = obj.getString("building_image");
                    String id = obj.getString("building_id");
                    String data= obj.getString("data");

                    building=new building();
                    building.setId(id);
                    building.setData(data);
                    building.setDesc(desc);
                    building.setName(name);


                    JSONArray dataAray=new JSONArray(data);
                    PolygonOptions dataOptions = new PolygonOptions();

                    for(int x=0;x<dataAray.length();x++){
                        JSONArray coordArray=dataAray.getJSONArray(x);
                        dataOptions.add(new LatLng(Double.parseDouble(coordArray.get(0).toString()),Double.parseDouble(coordArray.get(1).toString())));
                    }


                    dataOptions.clickable(true)
                            .strokeColor(getResources().getColor(R.color.grey))
                            .geodesic(true)
                            .fillColor(getResources().getColor(R.color.grey));

                    building.setLocation(getPolygonCenterPoint(dataOptions.getPoints()));
                    buildings1.add(building);

                    mMap.addPolygon(dataOptions);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(getPolygonCenterPoint(dataOptions.getPoints()));
                    markerOptions.title(name);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    mMap.addMarker(markerOptions);
                    mMap.setOnMarkerClickListener(marker -> {

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),20));

                        return true;
                    });

//                        final int POLYGON_PADDING_PREFERENCE = 200;
//                        final LatLngBounds latLngBounds = getPolygonLatLngBounds(dataOptions.getPoints());
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));

                }

                //CircleOptions circleOptions=new CircleOptions().center()
                buildingAdapter.notifyDataSetChanged();
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }


    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : polygon) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }

    private LatLng getPolygonCenterPoint(List<LatLng> polygonPointsList){
        LatLng centerLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }


    @Override
    public void onBackPressed() {

        if(sheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            super.onBackPressed();
        }
    }
}
