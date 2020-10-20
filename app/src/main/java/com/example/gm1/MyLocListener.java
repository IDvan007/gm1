package com.example.gm1;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocListener implements LocationListener {
    private LocListenerInterface locListenerInterface;
    @Override
    public void onLocationChanged(Location location) {
        try {
            locListenerInterface.OnLocationChanged(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}