package thangtv.com.trather.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import thangtv.com.trather.apis.Apis;
import thangtv.com.trather.commons.Const;
import thangtv.com.trather.ui.helper.ViewHelper;

/**
 * Created by Nguyen on 10/29/2015.
 */
public class PlaceData {

    static public void getPlaceWithId(String placeId, ResultCallback<PlaceBuffer> getData_Cb){
        PendingResult<PlaceBuffer> placeResult =
                Places.GeoDataApi.getPlaceById(Apis.mGoogleApiClient, placeId);
        placeResult.setResultCallback(getData_Cb);
    }
    static public Location getGpsData(Context context){
        MyLocationListener locationListener = new MyLocationListener();
        if(!locationListener.canGetLocation()){
            ViewHelper.showSettingsAlert(context);
        }
        return locationListener.getLocation();
    }

    /*---------- Listener class to get coordinates ------------- */
    private static class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}


        public Location getLocation() {
            Location location  = getLocation(LocationManager.PASSIVE_PROVIDER);
                if(location == null)
                    location = getLocation(LocationManager.GPS_PROVIDER);
                if(location == null)
                    location = getLocation(LocationManager.NETWORK_PROVIDER);
            return location;
        }

        public boolean canGetLocation(){
            boolean isGPSenable = isEnable(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnable = isEnable(LocationManager.NETWORK_PROVIDER);
            return (isGPSenable||isNetworkEnable);
        }
        public boolean isEnable(String locService){
            return Apis.locationManager.isProviderEnabled(locService);
        }

        private Location getLocation(String locationService){
            Location location = null;
            Apis.locationManager.requestLocationUpdates(
                    locationService,
                    Const.PLACE_MIN_TIME_UPDATES,
                    Const.PLACE_MIN_DISTANCE_UPDATES, this);
            if (Apis.locationManager != null) {
                location = Apis.locationManager.getLastKnownLocation(locationService);
            }
            return location;
        }
    }
}
