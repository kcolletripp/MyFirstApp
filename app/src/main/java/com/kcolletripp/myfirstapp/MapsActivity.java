package com.kcolletripp.myfirstapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.textservice.SpellCheckerService;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.facebook.FacebookSdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

import android.os.AsyncTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {


    GoogleMap map;
    LocationManager locationManager;
    String placesSearchStr;
    LatLng currentLocation;
    private static final String TAG = MapsActivity.class.getName();
    //google places api
    String data;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Display activity
        setContentView(R.layout.activity_maps);

        //facebook shit
        FacebookSdk.sdkInitialize(getApplicationContext());



        //Put map fragment on activity
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        //Initialize list of markers (for google places)
        placeMarkers = new Marker[MAX_PLACES];


        mapFragment.getMapAsync(this);


        //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Toast.makeText(this, "test1",Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "test1",Toast.LENGTH_SHORT).show();
        }
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "test2",Toast.LENGTH_SHORT).show();
        }
        //Put that little button that centers the map on you
        map.setMyLocationEnabled(true);

    }


    //Hook up the action bar icons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //Settings button
            case R.id.action_settings:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .addToBackStack("SettingsFragment")
                        .commit();

                return true;

            //Facebook button
            case R.id.action_facebook:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new FacebookFragment())
                        .addToBackStack("FacebookFragment")
                        .commit();;
                return true;

            //Refresh button
            case R.id.action_refresh:
                findPlace();
                return true;
            //Random button
            case R.id.action_random:
                randomPlace();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




    @Override
    public void onLocationChanged(Location location) {

        //map.clear();
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());



        //Log.i(TAG,placesSearchStr);
        //new AsyncHttp().execute(placesSearchStr);



//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLocation);
//        markerOptions.title("i'm here");
//
//        map.addMarker(markerOptions);

        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Make the options appear in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buttons, menu);
        return true;
    }

    //Make the back button actually do things
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();

        } else {
            super.onBackPressed();
        }
    }


    /*
    Section for button calls
     */

    //Grab JSON and make it into a string
    public String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //1st = doInBackground params, 2nd = progressUpdate params, 3rd = OnPostExecute params
    class asyncHttp extends AsyncTask<String, Void, String> {
        private final String TAG_async = asyncHttp.class.getName();

        @Override
        protected String doInBackground(String... url) {
            String result = getJSON(url[0]);
            //passed straight to onPostExecute
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            data = result;

            //remove old markers
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }

            //add new markers
            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(result);
                JSONArray placesArray = resultObject.getJSONArray("results");
                places = new MarkerOptions[placesArray.length()];
                //loop through places
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    //flag for exception of JSON parsing
                    boolean missingValue=false;
                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";

                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                        placeLL = new LatLng(
                                Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        JSONArray types = placeObject.getJSONArray("types");
                        for(int t=0; t<types.length(); t++){
                            //what type is it
                            String thisType=types.get(t).toString();
                            //TODO do things with the type
                        }
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");

                    }
                    catch(JSONException jse){
                        missingValue=true;
                        jse.printStackTrace();

                    }

                    if(missingValue)    places[p]=null;
                    else
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet(vicinity);

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=map.addMarker(places[p]);
                }
            }
            //AuthMsg msg = new Gson().fromJson(data, AuthMsg.class);
            //Log.i(TAG_async,data);

        }
    }

    public void randomPlace() {

        if (places!=null && placeMarkers!=null) {
            int rand = new Random().nextInt(placeMarkers.length);
            if(places[rand]!=null) {
                placeMarkers[rand] = map.addMarker(places[rand]);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(places[rand].getPosition(), 17.0f));
            }
            return;
        } else {
            Toast.makeText(this, "Error: Generate places first",Toast.LENGTH_SHORT).show();
        }
        return;

    }


    public void findPlace() {

        if(currentLocation!=null) {

            String strTypes = "";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (prefs.getBoolean("set_food",true)) { strTypes = strTypes + "" + getString(R.string.settings_food); }
            if (prefs.getBoolean("set_entertainment",true)) { strTypes = strTypes + "" + getString(R.string.settings_entertainment); }
            if (prefs.getBoolean("set_shopping",true)) { strTypes = strTypes + "" + getString(R.string.settings_shopping); }

            if(!(prefs.getBoolean("set_food",true) || prefs.getBoolean("set_entertainment",true) || prefs.getBoolean("set_shopping",true))) {
                placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location=" + currentLocation.latitude + "," + currentLocation.longitude +
                        "&radius=1000&sensor=true" +
                        "&types=" + strTypes +
                        "&key=" + getString(R.string.google_key_server);

                Log.i(TAG, placesSearchStr);

                new asyncHttp().execute(placesSearchStr);
            } else {
                Toast.makeText(this, "All settings disabled",Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Can't find you",Toast.LENGTH_SHORT).show();
        }
        return;

    }


}
