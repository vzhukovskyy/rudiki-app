package ua.pp.rudiki;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RudikiGpsService extends Service implements android.location.LocationListener
{
    private static final String TAG = RudikiGpsService.class.getSimpleName();

    private LocationManager locationManager = null;
    private GpsLog gpsLog;
    private LocationTrigger locationTrigger;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        gpsLog = new GpsLog(this);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationTrigger = new LocationTrigger();

        registerLocationManagerListener();
    }

    private void registerLocationManagerListener() {

        final int LOCATION_INTERVAL = 1000;
        final float LOCATION_DISTANCE = 10f;

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.e(TAG, "onLocationChanged: " + location);

        gpsLog.log(location);

        locationTrigger.changeLocation(location.getLatitude(), location.getLongitude());
        if(locationTrigger.isTriggered()) {
            sendNotification();
        }
    }

    void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        int notificationId = 1;
        NotificationUtils.displayNotification(this, notificationId, "Ticker", "Rudiki", "You've entered trigger area", intent);
        Log.e(TAG, "notification displayed");
    }

    // ***********************************************
    // ***** LocationListener interface implementation
    // ***********************************************

    @Override
    public void onProviderDisabled(String provider) {
//        Log.e(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Log.e(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.e(TAG, "onStatusChanged: " + provider);
    }

    // ***********************************************
    // ***** Service overrides
    // ***********************************************

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        try {
            locationManager.removeUpdates(this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to remove location listener, ignore", ex);
        }

        super.onDestroy();
    }

}