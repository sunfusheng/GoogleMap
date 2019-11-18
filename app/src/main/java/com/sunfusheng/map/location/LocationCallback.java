package com.sunfusheng.map.location;

import android.location.Address;

/**
 * @author sunfusheng
 * @since 2019-11-18
 */
public interface LocationCallback {
    void onLocationChanged(Address address);
}
