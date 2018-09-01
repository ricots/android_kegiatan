package com.android.laporan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

import com.android.laporan.activity.detail_kegiatan;
import com.android.laporan.fragment.ContentFragment;
import com.android.laporan.fragment.home;
import com.android.laporan.helper.RestManager;
import com.android.laporan.helper.apidata;
import com.android.laporan.koneksi.SharedPrefManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity /*implements LocationListener*/ {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView, navigation_view2;
    private DrawerLayout drawerLayout;
    SharedPrefManager sharedPrefManager;
    String jabatan;

    protected double lat;
    protected double lon;
    protected double currentSpeed;
    protected double kmphSpeed;
    protected double avgSpeed;
    protected double avgKmph;
    protected double totalSpeed;
    protected double totalKmph;
    TextView latituteField;
    TextView longitudeField;
    int counter = 0;
    LocationManager locationManager;
    LocationListener locationListener;
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    apidata mApiService;
    private RestManager restManager;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().
                    add(R.id.frame,
                            new home(),
                            home.class.getSimpleName()).commit();
        }

        latituteField = (TextView) findViewById(R.id.lat);
        longitudeField = (TextView) findViewById(R.id.lng);
        init();
        start_get();
        restoreValuesFromBundle(savedInstanceState);
//        run();
        sharedPrefManager = new SharedPrefManager(this);
        jabatan = sharedPrefManager.getSP_jab();
        Toast.makeText(MainActivity.this, sharedPrefManager.getSP_jab(), Toast.LENGTH_LONG).show();
//        Bundle bundlekirim = getIntent().getExtras();
//        jabatan = bundlekirim.getString("kirim");

        if (jabatan.equalsIgnoreCase("jbt1")) {
            menu();
            navigationView.inflateMenu(R.menu.drawer);
        } else if (jabatan.equalsIgnoreCase("jbt2") || (jabatan.equalsIgnoreCase("jbt3"))) {
            menu_bidang();
            navigationView.inflateMenu(R.menu.drawer_dinaas);
        }
        // Initializing Toolbar and setting it as the actionbar

    }

    public void menu() {
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.book:
                        Bundle args = new Bundle();
                        args.putString("datakirim", "kegiatan");
                        ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragment.setArguments(args);
                        fragmentTransaction.commit();
                        String kgtnya = "KEGIATAN";
                        getSupportActionBar().setTitle(kgtnya);
                        return true;
                    case R.id.history:
                        Bundle argshistory = new Bundle();
                        argshistory.putString("datakirim", "history");
                        ContentFragment fragmenthistory = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionhistory = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionhistory.replace(R.id.frame, fragmenthistory);
                        fragmenthistory.setArguments(argshistory);
                        fragmentTransactionhistory.commit();
                        String hstnya = "HISTORY";
                        getSupportActionBar().setTitle(hstnya);
                        return true;
                    case R.id.logout:
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                        startActivity(new Intent(MainActivity.this, login.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void menu_bidang() {
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.book:
                        Bundle args = new Bundle();
                        args.putString("datakirim", "kegiatan");
                        ContentFragment fragment = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragment.setArguments(args);
                        fragmentTransaction.commit();
                        String KGT = "KEGIATAN";
                        getSupportActionBar().setTitle(KGT);
                        return true;
                    case R.id.history:
                        Bundle argshistory = new Bundle();
                        argshistory.putString("datakirim", "kegiatan");
                        ContentFragment fragmenthistory = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionhistory = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionhistory.replace(R.id.frame, fragmenthistory);
                        fragmenthistory.setArguments(argshistory);
                        fragmentTransactionhistory.commit();
                        String hst = "HISTORY";
                        getSupportActionBar().setTitle(hst);
                        return true;
                    case R.id.kdl:
                        Bundle all = new Bundle();
                        all.putString("datakirim", "all");
                        ContentFragment fragmentall = new ContentFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionall = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionall.replace(R.id.frame, fragmentall);
                        fragmentall.setArguments(all);
                        fragmentTransactionall.commit();
                        String dtl = "LIST KENDALA";
                        getSupportActionBar().setTitle(dtl);
                        return true;
                    case R.id.logout:
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                        startActivity(new Intent(MainActivity.this, login.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }


    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            latituteField.setText(
                    "" + mCurrentLocation.getLatitude()
            );

            longitudeField.setText("" + mCurrentLocation.getLongitude());

            // giving a blink animation on TextView
            latituteField.setAlpha(0);
            latituteField.animate().alpha(1).setDuration(300);

            longitudeField.setAlpha(0);
            longitudeField.animate().alpha(1).setDuration(300);
//            send_lokasi();
            // location last updated time
//            txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
        }

    }

    public void start_get(){
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
//                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
//                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
//                                Log.e(TAG, errorMessage);

                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void send_lokasi(){
        restManager = new RestManager();
        mApiService = restManager.ambil_data_kegiatan();
                //loading = ProgressDialog.show(MainActivity.this, null, "Harap Tunggu...", true, false);

                mApiService.update_lokasi(sharedPrefManager.getSPNama(),
                        latituteField.getText().toString(), longitudeField.getText().toString())
                        .enqueue(new Callback<ResponseBody>() {
                                     @Override
                                     public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                         if (response.isSuccessful()){
                                            // loading.dismiss();
                                             Toast.makeText(MainActivity.this, "Berhasil update data", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(add_kegiatan.this, ContentFragment.class)
//                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                         } else {
                                            // loading.dismiss();
                                             Toast.makeText(MainActivity.this, "Gagal update data", Toast.LENGTH_SHORT).show();
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<ResponseBody> call, Throwable t) {
                                         //loading.dismiss();
                                         Toast.makeText(MainActivity.this, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
                                     }
                        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        time_send();
    }

    @Override
    protected void onResume() {
        super.onResume();
        time_send();
    }

    @Override
    protected void onStop() {
        super.onStop();
        time_send();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        time_send();
    }

    public void time_send(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                send_lokasi();
            }
        },500000);
    }

//    @Override
//    public void onResume() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        locationManager.removeUpdates(this);
//        super.onPause();
//    }
//
//    public void run() {
//        final Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setSpeedRequired(true);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location newLocation) {
//                counter++;
//                currentSpeed = round(newLocation.getSpeed(), 3, BigDecimal.ROUND_HALF_UP);
//                kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
//                lat = round(((double) (newLocation.getLatitude())), 3, BigDecimal.ROUND_HALF_UP);
//                lon = round(((double) (newLocation.getLongitude())), 3, BigDecimal.ROUND_HALF_UP);
//                latituteField.setText("Current Latitude:        " + String.valueOf(lat));
//                longitudeField.setText("Current Longitude:      " + String.valueOf(lon));
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
//    }
//
//    public static double round(double unrounded, int precision, int roundingMode)
//    {
//        BigDecimal bd = new BigDecimal(unrounded);
//        BigDecimal rounded = bd.setScale(precision, roundingMode);
//        return rounded.doubleValue();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
}
