package com.example.gpsdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView longitudeText;
    TextView latitudeText;
    TextView addressText;
    final int MY_PERMISSION_FINE_LOCATION = 1;
    LocationManager locationManager;
    List<Address> addressList;
    Geocoder myGeocoder;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudeText = findViewById(R.id.id_Longitude);
        latitudeText = findViewById(R.id.id_Latitude);
        addressText = findViewById(R.id.id_addressText);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        addressList =  new ArrayList<>();
        myGeocoder = new Geocoder(this, Locale.US);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onLocationChanged(Location location) {
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
}