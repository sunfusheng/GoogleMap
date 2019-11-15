package com.sunfusheng.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sunfusheng.StickyHeaderDecoration;
import com.sunfusheng.map.adapter.StickyGroupAdapter;
import com.sunfusheng.map.utils.StatusBarUtil;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private GoogleMap mGoogleMap;
    protected GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);

        initData();
        initView();
    }

    private void initData() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.support_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initView() {
        findViewById(R.id.btn_menu_more).setOnClickListener(v -> {
            if (bottomSheetDialog == null) {
                initBottomSheetDialog();
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();
            }
        });
    }

    private void initBottomSheetDialog() {
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_recycler_view, null, false);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StickyHeaderDecoration());
        StickyGroupAdapter stickyAdapter = new StickyGroupAdapter(this, Constants.items);
        recyclerView.setAdapter(stickyAdapter);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        int height = getWindowManager().getDefaultDisplay().getHeight();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bottomSheetView.getLayoutParams();
        layoutParams.height = height / 2;
        bottomSheetView.setLayoutParams(layoutParams);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

        bottomSheetDialog.setOnDismissListener(dialog -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initGoogleMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            initGoogleMap();
        }
    }

    private void initGoogleMap() {
        this.mGoogleMap.setMyLocationEnabled(true);

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.mGoogleApiClient.connect();

        this.mLocationRequest = LocationRequest.create();
        this.mLocationRequest.setInterval(5000);
        this.mLocationRequest.setFastestInterval(3000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, getMainLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    getAddress(location.getLatitude(), location.getLongitude());
                }
            }
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public String getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("sfs", "得到位置当前" + address + "'\n"
                    + "经度：" + String.valueOf(address.get(0).getLongitude()) + "\n"
                    + "纬度：" + String.valueOf(address.get(0).getLatitude()) + "\n"
                    + "国家：" + address.get(0).getCountryName() + "\n"
                    + "CountryCode: " + address.get(0).getCountryCode() + "\n"
                    + "城市：" + address.get(0).getLocality() + "\n"
                    + "街道：" + address.get(0).getAddressLine(0)
            );
            return address.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}
