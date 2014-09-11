package com.example.tomokazy.gpsmonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;



public class GPSMonitoring extends Activity {

    private LocationListener _locationListener;
    private LocationManager _locationManager;
    private static final int GPS_FREQUENCY_MILLIS = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsmonitoring);
        TextView v = (TextView)this.findViewById(R.id.TextView01);
        v.setText("Starting...");
        // Define a listener that responds to location updates -- new code from here:
        // Define a listener that responds to location updates
        _locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                registerForUpdates();
            }
            public void onProviderDisabled(String provider) {
                deregisterForUpdates();
            }
        };

        try {
            // Acquire a reference to the system Location Manager
            LocationManager locationManager =
                    (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);
        } catch (SecurityException e) {
            // requires ACCESS_FINE_LOCATION permission
            v.setText(e.getMessage());
        }
        Button b = (Button) this.findViewById(R.id.button);

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                close();
            }
        });
        // Acquire a reference to the system Location Manager
        _locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // ensure that GPS is switched on and available
        if (!_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessageNoGps();
        } else {
            registerForUpdates();
        }
        // permit things like network access on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
    }
    /** Closes the application */
    protected void close() {
        finish();
    }
    private void makeUseOfNewLocation(Location loc) {
        TextView v = (TextView)this.findViewById(R.id.TextView01);
        String text = "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude();
        v.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpsmonitoring, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /** Builds an alert message to allow the user the option of enabling GPS */
    private void showAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        launchGPSOptions();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
                close();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    /** Launches the SecuritySettings activity */
    private void launchGPSOptions() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
    }
    /** Register the listener with the Location Manager to receive location updates */
    private void registerForUpdates() {
        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_FREQUENCY_MILLIS, 0, _locationListener);
    }

    /** Register the listener with the Location Manager to receive location updates */
    private void deregisterForUpdates() {
        _locationManager.removeUpdates(_locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deregisterForUpdates();
    }
}
