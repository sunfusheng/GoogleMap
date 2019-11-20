package com.sunfusheng.googlemap;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.sunfusheng.StickyHeaderDecoration;
import com.sunfusheng.googlemap.adapter.StickyGroupAdapter;
import com.sunfusheng.googlemap.location.LocationHelper;
import com.sunfusheng.googlemap.utils.DisplayUtil;
import com.sunfusheng.googlemap.utils.StatusBarUtil;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final Permissions permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    );

    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;

    private GoogleMap mMap;

    private LocationHelper mLocationHelper;

    public static String[][] items = {
            {"街景", "1"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);

        initView();
        initData();
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
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_recycler_view, null);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new StickyHeaderDecoration());
        StickyGroupAdapter stickyAdapter = new StickyGroupAdapter(this, items);
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

    private void initData() {
        SoulPermission.getInstance().checkAndRequestPermissions(permissions, new CheckRequestPermissionsListener() {
            @Override
            public void onAllPermissionOk(Permission[] allPermissions) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.support_map_fragment);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(MainActivity.this);
                    initMyLocationButton(mapFragment.getView());
                }
            }

            @Override
            public void onPermissionDenied(Permission[] refusedPermissions) {

            }
        });
    }

    private void initMyLocationButton(View view) {
        View vMyLocationButton = view.findViewById(Integer.parseInt("2"));
        if (vMyLocationButton == null) {
            return;
        }

        if (vMyLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vMyLocationButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            final int margin = DisplayUtil.dp2px(this, 16);
            params.setMargins(margin, margin, margin, margin);
            vMyLocationButton.setLayoutParams(params);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setMyLocationEnabled(true);
        this.mMap.getUiSettings().setMyLocationButtonEnabled(true);

        this.mLocationHelper = new LocationHelper(this);
        this.mLocationHelper.start(address -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15));
        });
    }

    @Override
    protected void onDestroy() {
        if (mLocationHelper != null) {
            mLocationHelper.stop();
        }
        super.onDestroy();
    }
}
