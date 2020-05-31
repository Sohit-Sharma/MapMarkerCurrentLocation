package com.example.mapmarkercurrentlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView addressz;
    private LatLng latLng;
    SupportMapFragment mSupportMapFragment;
    private FusedLocationProviderClient mLocationClient;
    private Location location;
    double latitude, longitude;
    GoogleMap map;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intID();
        initilizeMap();
        IniLizeMap();
    }

    private void intID() {
        addressz=findViewById(R.id.tv_address);
    }

    private void initilizeMap() {

        mLocationClient = new FusedLocationProviderClient(this);

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_map);

        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.frag_map, mSupportMapFragment).commit();
        }

    }

    public void IniLizeMap() {
        try {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    map = googleMap;

                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    // Showing / hiding your current location
                    googleMap.setMyLocationEnabled(false);

                    // Enable / Disable zooming controls
                    googleMap.getUiSettings().setZoomControlsEnabled(false);

                    // Enable / Disable my location button
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                    // Enable / Disable Compass icon
                    googleMap.getUiSettings().setCompassEnabled(false);

                    // Enable / Disable Rotate gesture`enter code here`
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);

                    // Enable / Disable zooming functionality
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);


                    getCurrentLocation();

                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            latLng = cameraPosition.target;
                            googleMap.clear();
                            try {
                                getAddress(latLng.latitude, latLng.longitude);
                                String lat = latLng.latitude + "";
                                String lng = latLng.longitude + "";
                                String location = getAddress(latLng.latitude, latLng.longitude);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });



                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude,longitude)).zoom(14).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }


            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCurrentLocation() {

        mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful()) {
                    location = task.getResult();

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15)
                        );
                    }
//                gotoLocation(location.getLatitude(), location.getLongitude());
                } else {
                    final LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if (locationResult == null) {
                                return;
                            }
                            location = locationResult.getLastLocation();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                            mLocationClient.removeLocationUpdates(locationCallback);
                        }
                    };
                    mLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                }

            }
        });
    }


    public String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {

            System.out.println("get address");
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                System.out.println("size====" + addresses.size());
                Address address = addresses.get(0);

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");
                    }
                }
                System.out.println("ad==" + address);
                System.out.println("result---" + result.toString());

                addressz.setText(result.toString()); // Here is you AutoCompleteTextView where you want to set your string address (You can remove it if you not need it)
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }
}
