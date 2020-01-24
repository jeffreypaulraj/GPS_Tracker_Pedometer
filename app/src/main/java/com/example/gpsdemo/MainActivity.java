package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView longitudeText;
    TextView latitudeText;
    TextView addressText;
    TextView distanceText;
    TextView timeText;
    final int MY_PERMISSION_FINE_LOCATION = 1;
    LocationManager locationManager;
    List<Address> addressList;
    ListView locationListView;
    List<DestinationLog> locationLVList;
    Geocoder myGeocoder;
    ArrayList<Location> fullLocationList;
    ArrayList<Float> fullDistanceList;
    ArrayList<Long> timeLocationList;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudeText = findViewById(R.id.id_Longitude);
        latitudeText = findViewById(R.id.id_Latitude);
        addressText = findViewById(R.id.id_addressText);
        distanceText = findViewById(R.id.id_distanceText);
        timeText = findViewById(R.id.id_timeText);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        addressList =  new ArrayList<>();
        fullDistanceList = new ArrayList<>();
        fullLocationList = new ArrayList<>();
        timeLocationList = new ArrayList<>();
        locationLVList = new ArrayList<>();
        locationListView = findViewById(R.id.listView);
        myGeocoder = new Geocoder(this, Locale.US);
        CustomAdapter customAdapter = new CustomAdapter(this,R.layout.adapter_custom ,locationLVList);
        locationListView.setAdapter(customAdapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }

    public class CustomAdapter extends ArrayAdapter<DestinationLog> {

        List<DestinationLog> arrayList;
        Context parentContext;
        int xmlResource;
        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<DestinationLog> objects) {
            super(context, resource, objects);
            arrayList = objects;
            parentContext = context;
            xmlResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) parentContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.adapter_custom, null);

            TextView locationText = view.findViewById(R.id.id_adapterLocationText);
            TextView distanceText = view.findViewById(R.id.id_adapterDistanceText);
            TextView timeText = view.findViewById(R.id.id_adapterTimeText);



            return view;
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onLocationChanged(Location location) {
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            long time = SystemClock.elapsedRealtime()/1000;
            if(location!=null) {
                fullLocationList.add(location);
                if(timeLocationList.size() == 0){
                    timeLocationList.add(time);
                }
                else{
                    timeLocationList.add(time - timeLocationList.get(timeLocationList.size() - 1));
                }
                timeText.setText("Elapsed Time: " + timeLocationList.get(timeLocationList.size() - 1)+ " s");
                if(fullLocationList.size() > 0){
                    fullDistanceList.add(location.distanceTo(fullLocationList.get(0)));
                    distanceText.setText("Distance: " + fullDistanceList.get(fullDistanceList.size() - 1) + " m");
                }
            }

            latitudeText.setText("Latitude: " + location.getLatitude());
            longitudeText.setText("Longitude: " + location.getLongitude());
        } catch (SecurityException e) { }
        try {
            addressList = myGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addressText.setText(addressList.get(0).getAddressLine(0));
        }
        catch(IOException e){ }
    }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    }
                    catch (SecurityException e){ }
                }
            break;
        }
    }

    public class DestinationLog{

        String address;
        Float distance;
        Long timeSpent;

        public DestinationLog(String address, Float distance, Long timeSpent){
            this.address =address;
            this.distance = distance;
            this.timeSpent = timeSpent;
        }

        public String getAddress() { return address; }

        public Float getDistance() { return distance; }

        public Long getTimeSpent() { return timeSpent; }
    }
}