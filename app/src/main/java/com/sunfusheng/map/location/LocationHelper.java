package com.sunfusheng.map.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * @author sunfusheng
 * @since 2019-11-18
 */
@SuppressLint("MissingPermission")
public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private Context mContext;
    private LocationManager mLocationManager;
    private String mLocationProvider;
    private LocationListener mLocationListener;
    private LocationCallback mLocationCallback;

    public LocationHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public LocationManager getLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        return mLocationManager;
    }

    public String getLocationProvider() {
        if (mLocationProvider == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            mLocationProvider = mLocationManager.getBestProvider(criteria, true);
        }
        return mLocationProvider;
    }

    public LocationListener defaultLocationListener() {
        if (mLocationListener == null) {
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged() location:" + location);

                    Address address = getAddress(location.getLatitude(), location.getLongitude());
                    if (address != null) {
                        Log.d(TAG, "Longitude: " + address.getLongitude() + "\n"
                                + "Latitude: " + address.getLatitude() + "\n"
                                + "CountryName: " + address.getCountryName() + "\n"
                                + "CountryCode: " + address.getCountryCode() + "\n"
                                + "City: " + address.getLocality() + "\n"
                                + "Street: " + address.getAddressLine(0)
                        );
                    }

                    if (mLocationCallback != null && address != null) {
                        mLocationCallback.onLocationChanged(address);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d(TAG, "onStatusChanged() provider:" + provider);
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.w(TAG, "onProviderEnabled() provider:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.w(TAG, "onProviderDisabled() provider:" + provider);
                }
            };
        }
        return mLocationListener;
    }

    public Location getLastKnownLocation() {
        return getLocationManager().getLastKnownLocation(getLocationProvider());
    }

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void start(LocationCallback callback) {
        this.mLocationCallback = callback;
        getLocationManager().requestLocationUpdates(getLocationProvider(), 3000, 1, defaultLocationListener());
    }

    public void stop() {
        if (mLocationListener != null) {
            getLocationManager().removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }
}
