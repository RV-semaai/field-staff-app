package com.semaai.agent.activity.newcustomer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.semaai.agent.R;
import com.semaai.agent.databinding.ActivityGpsViewBinding;
import com.semaai.agent.services.GpsTracker;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.MyLocationListener;
import com.semaai.agent.viewmodel.newcustomer.GPSViewViewModel;

public class GPSViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static double latitude1;
    public static double longitude1;
    private GoogleMap gMap;
    GPSViewViewModel gpsViewViewModel;
    private ActivityGpsViewBinding binding;
    double newLat, newLon;
    Boolean newLatLonChange = false;


    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsViewViewModel = ViewModelProviders.of(this).get(GPSViewViewModel.class);
        binding = DataBindingUtil.setContentView(GPSViewActivity.this, R.layout.activity_gps_view);
        binding.setLifecycleOwner(this);
        binding.setRegisterPhotoLocationViewModel(gpsViewViewModel);

        context = this;
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

            return;
        }

        intiView();
        onClick();
    }

    private void onClick() {
        binding.icBackDialog.cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.icBackDialog.cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });

        binding.cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });

        binding.cl1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clBackAlert.setVisibility(View.GONE);
                return false;
            }
        });
    }

    private void intiView() {

        binding.clBackAlert.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frg_mapview);

        Log.e("TAG", "intview: " + latitude1 + " " + longitude1);

        mapFragment.getMapAsync(this);

        binding.ivMapdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.clBackAlert.setVisibility(View.GONE);
                if (Constant.onUpdate) {
                    Constant.locationChange = true;
                    if (newLatLonChange) {
                        Log.i("-->", "old :" + latitude1 + " , new :" + newLat);
                        latitude1 = newLat;
                        longitude1 = newLon;
                        Constant.onUpdate = false;
                    }
                }
                finish();
            }
        });

        binding.cvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(GPSViewActivity.this);
                if (gpsTracker.canGetLocation()) {
                    newLatLonChange = true;
                    Constant.onUpdate = true;
                    LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    newLat = latLng.latitude;
                    newLon = latLng.longitude;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    gMap.animateCamera(cameraUpdate);
                    gMap.clear();
                    gMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("You")
                            .draggable(true));
                    gMap.getFocusedBuilding();
                    binding.clNoGPS.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("Marker"));
        gMap.getFocusedBuilding();
        LatLng latLng = new LatLng(latitude1, longitude1);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                binding.clBackAlert.setVisibility(View.GONE);
                googleMap.clear();
                latitude1 = latLng.latitude;
                longitude1 = latLng.longitude;
                newLat = latLng.latitude;
                newLon = latLng.longitude;
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("You")
                        .draggable(true));
                googleMap.getFocusedBuilding();
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                binding.clBackAlert.setVisibility(View.GONE);
            }
        });
        if (latitude1==0 && longitude1 == 0){
            binding.clNoGPS.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gMap != null) {
            gMap.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("You"));
            gMap.getFocusedBuilding();
            LatLng latLng = new LatLng(latitude1, longitude1);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            gMap.animateCamera(cameraUpdate);
            binding.clNoGPS.setVisibility(View.GONE);
            if (latitude1==0 && longitude1 == 0){
                binding.clNoGPS.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        binding.clBackAlert.setVisibility(View.VISIBLE);
    }
}