package com.example.activitytransitiontrial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "DETECT TRANSITION";
    private boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private LogFragment mLogFragment;
    private String INTERNAL_RECEIVER_ACTION = "com.package.name.ACTION_INTERNAL_MESSAGE";
    private Button startButton;
    public boolean activityTrackingEnabled;
    FusedLocationProviderClient mFusedLocationClient;

    private InternalReceiver internalReceiver = new InternalReceiver();

    File baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    String fileName = "ActivityTransitionTrial.txt";
    String filePath = baseDir + File.separator + fileName;

    public double latitude;
    public double longitude;

    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.btn_start_tracking);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        activityTrackingEnabled = false;
        activityTrackingEnabled = LoadInt();

        if(activityTrackingEnabled)
            startButton.setText("Stop Tracking");

        IntentFilter intentFilter = new IntentFilter(INTERNAL_RECEIVER_ACTION);
        intentFilter.addAction(INTERNAL_RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(internalReceiver, intentFilter);

        mLogFragment =
                (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionApproved()) {
                    if (activityTrackingEnabled) {
                        stopTracking();
                        activityTrackingEnabled = false;
                        startButton.setText("Start Tracking");

                    } else {
                        startTracking();
                        activityTrackingEnabled = true;
                        SaveBool("TrackingEnabled", true);
                        startButton.setText("Stop Tracking");
                    }

                } else {
                    // Request permission and start activity for result. If the permission is approved, we
                    // want to make sure we start activity recognition tracking.
                    Intent startIntent = new Intent(getApplicationContext(), PermissionRationaleActivity.class);
                    startActivityForResult(startIntent, 0);
                }
            }
        });

        Log.d(TAG,filePath);
        writeToFile("Start Entry\n",getApplicationContext());
        String check = readFromFile(getApplicationContext());
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if location is enabled
        if (isLocationEnabled()) {
            // getting last location from FusedLocationClient object
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        //latitudeTextView.setText(location.getLatitude() + "");
                        //longitTextView.setText(location.getLongitude() + "");
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            //latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    public static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filePath,true));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = new FileInputStream(filePath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
        return ret;
    }

    public void SaveBool(String key, boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean LoadInt(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean("TrackingEnabled", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Start activity recognition if the permission was approved.
        Log.d(TAG,"Activity Result");
        if (PermissionApproved() && !activityTrackingEnabled) {
            startTracking();
            startButton.setText("Stop Tracking");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean PermissionApproved() {
        // TODO: Review permission check for 29+.
        if (runningQOrLater) {
            boolean permission = (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) &&
                                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) &&
                                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) );
            return permission;
        } else {
            return true;
        }
    }

    private void startTracking() {
        Log.d(TAG, "START TRACKING");
        Intent intent = new Intent(MainActivity.this,
                ActivityTransitionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        printToScreen(new String("Transition API registered"));
    }
    private void stopTracking() {
        Log.d(TAG, "STOP TRACKING");
        Intent intent = new Intent(MainActivity.this,
                ActivityTransitionService.class);
        stopService(intent);
        printToScreen("Transition API de-registered");
    }

    public void printToScreen(@NonNull String message) {
        mLogFragment.getLogView().println(message);
        Log.d(TAG, message);
    }


    public class InternalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            date = calendar.getTime();
            getLastLocation();

            Log.d(TAG, "onReceive(): " + intent);
            String message = intent.getStringExtra("Internal Message");
            int batLevel = getBatteryPercentage(getApplicationContext());
            message = message + ", " + new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date())
                    + ", " + new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date())
                    + ", " + new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime())
                    + ", " + String.valueOf(batLevel)
                    + ", " + latitude
                    + ", " + longitude + "\n";
            Log.d(TAG, "Got message: " + message);
            printToScreen(message);
            writeToFile(message, context);
        }
    }
}