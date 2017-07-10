package com.example.zuzanka.geoapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import android.widget.TextView;

import com.example.zuzanka.geoapplication.helpers.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.opencv.core.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends MainActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap googleMap = null;
    private static float degree;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private TextView textView2;
    private SensorManager mSensorManager;
    private boolean mapLoaded = false;
    private Context context;
    private boolean markerAttached = false;
    private Marker positionMarker;
    private ArrayList<Marker> listOfMarkersOnMap = new ArrayList<>();

    private ArrayList<LatLng> realLocations;
    private int max = 0;
    private ArrayList<BitmapDescriptor> descriptors;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //textView2 = (TextView) findViewById(R.id.textView2);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        //HUE_AZURE, HUE_BLUE, HUE_CYAN, HUE_GREEN, HUE_MAGENTA, HUE_ORANGE, HUE_RED, HUE_ROSE, HUE_VIOLET, HUE_YELLOW
        //readFile("exactLocations.txt", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        //readFile("recordedLocations.txt", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // show clusters with different colors
        descriptors = createMarkerColorsList();
        realLocations = readFile("exactLocations.txt");
        readFCluster(getClusters());

        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(listOfMarkersOnMap.get(listOfMarkersOnMap.size()-1).getPosition(), 15.0f));

        this.googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapLoaded = true;
            }
        });
    }

    private void readFCluster(ArrayList<Integer> clusters) {
        for (int i = 0;  i < max; i++){
            ArrayList<Point> clusterLocations = new ArrayList<>();

            for (int j=0; j<clusters.size(); j++){
                if (clusters.get(j) == i){
                    addPositionToMap(realLocations.get(j), descriptors.get(i));
                }
            }
        }
    }

    private ArrayList<BitmapDescriptor> createMarkerColorsList(){
        ArrayList<BitmapDescriptor> descriptors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        descriptors.add(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        return descriptors;
    }

    public ArrayList<Integer> getClusters(){
        ArrayList<Integer> clusters = new ArrayList<>();

        String cl = "1 1 2 2 2 0 3 4 5 6 6 6 6 6 6 6 5 0 7 8 3 9 10 10 0 1 0 0 11 11 0 12 12 2 0 0 0 0 9 9 4 4 4 8 13 13 13 0 13 9 " +
                "9 13 7 9 3 13 13 13 13 13 13 13 13 13 13 9 9 4 14 14 6 6 6 6 6 6 6 6 6 6 6 6 6 6 15 15 15 15 0 5 9 9 9 0 2 2 5 0 5 15 " +
                "0 16 16 16 16 16 6 6 6 6 6 6 6 6 6 6 6 14 14 14 14 14 7 7";
        cl.trim();
        for (String s: cl.split(" ")) {
            clusters.add(Integer.parseInt(s));
            if (Integer.parseInt(s) > max) {
                max = Integer.parseInt(s);
            }
        }
        return clusters;
    }

    private void readFile(String filename, BitmapDescriptor bitmapDescriptor){
        BufferedReader br = null;
        FileReader fr = null;

        try {
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            while ((sCurrentLine = br.readLine()) != null) {
                addPositionToMap(parseLine(sCurrentLine), bitmapDescriptor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private ArrayList<LatLng> readFile(String filename){
        BufferedReader br = null;
        FileReader fr = null;

        ArrayList<LatLng> latLngs = new ArrayList<>();

        try {
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            while ((sCurrentLine = br.readLine()) != null) {
                latLngs.add(parseLine(sCurrentLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return latLngs;
        }
    }

    private void addPositionToMap(LatLng latLng, BitmapDescriptor bitmapDescriptor) {
        positionMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Your current position").icon(bitmapDescriptor));
        positionMarker.setFlat(true);
        listOfMarkersOnMap.add(positionMarker);
    }

    private LatLng parseLine(String line) {
        LatLng position = new LatLng(Double.valueOf(line.split(";")[0]), Double.valueOf(line.split(";")[1]));
        return position;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*degree = Math.round(event.values[0]);

        double a;
        double b;

        textView2.setText(Float.toString(degree));
        if (markerAttached) {
            positionMarker.setRotation(degree);
        }*/
    }

    public LatLng getCurrentGPSPosition(){
        GPSTracker gpsTracker = new GPSTracker(this);
        return new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onGPSRequestButtonClick(View v){

        double a = 0;
        double b = 0;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //latitude.setText("GSP provider lat: " + String.valueOf(locationNet.getLatitude()) + ", lonf:" + String.valueOf(locationNet.getLongitude()));
        //longitude.setText("Network provider lat: " + String.valueOf(locationNet2.getLatitude()) + ", lonf:" + String.valueOf(locationNet2.getLongitude()));

        // Get GPS enabled status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Get Network enabled status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        LatLng position = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        positionMarker = googleMap.addMarker(new MarkerOptions().position(position).title("Your current position"));
        positionMarker.setFlat(true);
        listOfMarkersOnMap.add(positionMarker);
        markerAttached = true;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f));
        //googleMap.addPolyline(new PolylineOptions().add(position).add(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude() + 0.2f)));

        System.out.println("degree: " + degree);

        b = Math.sin(Math.toRadians(degree)) * 0.005f;
        a = Math.cos(Math.toRadians(degree)) * 0.005f;

        googleMap.addPolyline(new PolylineOptions().add(position).add(new LatLng(gpsTracker.getLatitude() + a, gpsTracker.getLongitude() + b)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                System.out.println("data: "+photo);
                System.out.println(data.getData().getEncodedPath());
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
