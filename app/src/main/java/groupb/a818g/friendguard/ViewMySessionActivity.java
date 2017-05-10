package groupb.a818g.friendguard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import groupb.a818g.friendguard.Global.GlobalRepository;


public class ViewMySessionActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocationActivity";
    private long INTERVAL;
    private long FASTEST_INTERVAL;
    private static final int MY_PERMISSIONS_REQUEST=1;
    private LinearLayout exit;
    private Integer session_id = null;
    private String email;
    private LinearLayout alert;
    private TextView lat;
    private TextView lng;
    private TextView time;
    private TextView safety;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    private Boolean isSafe = true;
    private GoogleMap mMap;
    private UserLocationTask mAuthTask;
    private UserEndSessionTask mAuthTask_EndSession;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    private long PassiveCheckinInterval = 1000*60;
    private long ActiveCheckinInterval = 1000*60; //2 min
    private AlertDialog ActiveCheckIndialog=null;
    private Handler mHandler;
    private Handler mHandler2;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        initCheckIns();

        Bundle extras = getIntent().getExtras();
        Log.e("int auto ", Integer.toString(extras.getInt("Auto Update Interval")))   ;
        INTERVAL = Long.valueOf(extras.getInt("Auto Update Interval")) * 100;
        FASTEST_INTERVAL = INTERVAL / 2;


        email = extras.getString("email");
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_view_my_session);
        alert = (LinearLayout) findViewById(R.id.MySessionAlert);
        lat = (TextView) findViewById(R.id.Lat_number2);
        lng = (TextView) findViewById(R.id.Lng_number2);
        time = (TextView) findViewById(R.id.time_number2);
        safety = (TextView) findViewById(R.id.Safety_Info2);
        exit = (LinearLayout) findViewById(R.id.MySessionEnd);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safety.setText("DANGER");
                isSafe = false;
                mLastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


                Log.e("alert session","button hit from " + Integer.toString(session_id));


                mAuthTask = new UserLocationTask(new Double(mCurrentLocation.getLatitude()).toString(), new Double(mCurrentLocation.getLongitude()).toString(), mLastUpdateTime, email, session_id);
                mAuthTask.execute((Void) null);
                updateUI();
            }
        });
        alert.setOnTouchListener(new View.OnTouchListener()
        {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                ImageView image=null;
                int count = alert.getChildCount();
                for (int i = 0; i < count; i++) {
                    View vv = alert.getChildAt(i);
                    if (vv instanceof ImageView) {
                        image=(ImageView) vv;
                    }
                }
                if(image==null) return false;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    image.setColorFilter(Color.argb(200, 190, 221, 30));
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    image.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        image.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                }
                return false;
            }

        });



        safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSafety();

            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionEnd(session_id, email);
            }
        });
        exit.setOnTouchListener(new View.OnTouchListener()
        {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                ImageView image=null;
                int count = alert.getChildCount();
                for (int i = 0; i < count; i++) {
                    View vv = alert.getChildAt(i);
                    if (vv instanceof ImageView) {
                        image=(ImageView) vv;
                    }
                }
                if(image==null) return false;
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    image.setColorFilter(Color.argb(200, 190, 221, 30));
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    image.setColorFilter(Color.argb(0, 0, 0, 0));
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        image.setColorFilter(Color.argb(0, 0, 0, 0));
                    }
                }
                return false;
            }

        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(ViewMySessionActivity.this);
        /*btnFusedLocation = (Button) findViewById(R.id.btnShowLocation);
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View arg0) {
        updateUI();
        }
        });*/

    }



    private void sessionEnd(Integer session_id, String email) {

        mAuthTask_EndSession = new UserEndSessionTask(session_id, email);
        mAuthTask_EndSession.execute((Void) null);
        finish();

    }
    private void initCheckIns() {
        PassiveCheckinInterval =  GlobalRepository.AutoCheckinIntervalMins*60*1000;
        mHandler = new Handler();
        mHandler2 = new Handler();
        startRepeatingTask();

    }
    private void ActiveCheckInDialog() {
        if(ActiveCheckIndialog!=null && ActiveCheckIndialog.isShowing()) return ;
        // 1. Instantiate an AlertDialog.Builder with its constructor
        //AlertDialog.Builder builder = new AlertDialog.Builder(ViewMySessionActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewMySessionActivity.this,  android.R.style.Theme_Material_Dialog_Alert);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Is everything OK?")
                .setTitle("Active Check-In");

        // 3. Get the AlertDialog from create()


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                SendAlerts();
            }
        });
        ActiveCheckIndialog = builder.show();
        ActiveCheckIndialog.show();

        final Handler handler3  = new Handler();
        final Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                if (ActiveCheckIndialog.isShowing()) {
                    ActiveCheckIndialog.dismiss();
                }
            }
        };

        ActiveCheckIndialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SendAlerts();
                handler3.removeCallbacks(runnable3);
            }
        });

        handler3.postDelayed(runnable3, 1000*60*3); //3min active checkin miss
        //AlertDialog dialog = builder.create();

    }

    private void SendAlerts(){
        safety.setText("DANGER");
        isSafe = false;
        mLastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


        Log.e("alert session","button hit from " + Integer.toString(session_id));


        mAuthTask = new UserLocationTask(new Double(mCurrentLocation.getLatitude()).toString(), new Double(mCurrentLocation.getLongitude()).toString(), mLastUpdateTime, email, session_id);
        mAuthTask.execute((Void) null);
        updateUI();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.e("onNewIntent", "is called");


    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mCurrentLocation != null) {

            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            mMap.setMyLocationEnabled(true);
            mMap.addMarker(markerOptions);}

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Location update could NOT started ..............: ");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat.setText(String.valueOf(mLastLocation.getLatitude()));
            lng.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        Log.d(TAG, "Location update started ..............: ");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();  //re-instantiate the location service
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        DateFormat.getTimeInstance().format(new Date());

        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");

        if (null != mCurrentLocation) {
            Log.i(TAG, mCurrentLocation.toString() + "#" + "Time: " + mLastUpdateTime + "# isSafe: " + isSafe.toString());
            String latitude = String.valueOf(mCurrentLocation.getLatitude());
            String longitude = String.valueOf(mCurrentLocation.getLongitude());

            time.setText(mLastUpdateTime);
            lat.setText(latitude);
            lng.setText(longitude);
            if (isSafe)
                safety.setText("safe");


            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            if (isSafe) {
                markerOptions.title(mLastUpdateTime + " " + "safe");
            } else {
                markerOptions.title(mLastUpdateTime + " " + "DANGER");
            }
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();

            }
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            Log.e("loc", mCurrentLocation.toString());


        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        session_id = extras.getInt("session_id");
        email = extras.getString("email");
        Log.e("session in onResume", Integer.toString(session_id));

    }

    Runnable PassiveCheckInRun = new Runnable() {
        @Override
        public void run() {
            try {
                sendPassiveCheckIn(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(PassiveCheckInRun, PassiveCheckinInterval);
            }
        }
    };
    Runnable ActiveCheckInRun = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("MySessionView","ActiveCheckIn Fired");
                ActiveCheckInDialog(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler2.postDelayed(ActiveCheckInRun, ActiveCheckinInterval);
            }
        }
    };
    void startRepeatingTask() {
        PassiveCheckInRun.run();
        ActiveCheckInRun.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(PassiveCheckInRun);
        mHandler2.removeCallbacks(ActiveCheckInRun);

    }
    void sendPassiveCheckIn() {
        if(session_id==null || session_id<=0 ||  mCurrentLocation==null) return ;
        mLastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Log.e("alert session","button hit from " + Integer.toString(session_id));
        mAuthTask = new UserLocationTask(new Double(mCurrentLocation.getLatitude()).toString(), new Double(mCurrentLocation.getLongitude()).toString(), mLastUpdateTime, email, session_id,"passive");
        mAuthTask.execute((Void) null);
    }
    public void confirmSafety() {


        new AlertDialog.Builder(this)
                .setTitle("Click OK if you are SAFE")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        safety.setText("safe");
                        isSafe = true;
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();



    }






    public class UserLocationTask extends AsyncTask<Void, Void, Boolean> {

        String checkinType;
        String lat;
        String lng;
        String time;
        String email;
        Integer session_id;
        SSLSocket sock = null;
        List<String> contacts;
        SSLContext sc;
        String server = "friendguarddb.cs.umd.edu";
        int port = 10023;
        String command = "";
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };


        public UserLocationTask(String lat, String lng, String time, String email, Integer session_id) {
            this.lat = lat;
            this.lng = lng;
            this.time = time;
            this.email = email;
            this.session_id = session_id;
            this.checkinType = "alert";
        }
        public UserLocationTask(String lat, String lng, String time, String email, Integer session_id,String checkinType_) {
            this.lat = lat;
            this.lng = lng;
            this.time = time;
            this.email = email;
            this.session_id = session_id;
            this.checkinType=checkinType_;
        }




        //TODO: bind service so the socket connection is shared

        @Override
        protected Boolean doInBackground(Void... params) {

            try {



                sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                sock = (SSLSocket) sc.getSocketFactory().createSocket(server, port);
                sock.setUseClientMode(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "checkin");
                jsonObject.put("checkin_type", checkinType);
                jsonObject.put("username", email);
                jsonObject.put("lat", lat);
                jsonObject.put("lon", lng);
                jsonObject.put("time", time);
                jsonObject.put("session_id", session_id);


                Log.e("json", jsonObject.toString());
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                wr.write(jsonObject.toString());
                wr.flush();


                //BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                //String str = rd.readLine();
                //System.out.println(str);
                //rd.close();

                sock.close();
                return true;


            } catch (KeyManagementException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
            // TODO: register the new account here.

        }



        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            Log.e("pstEx","Before success");



        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }




    }








    public class UserEndSessionTask extends AsyncTask<Void, Void, Boolean> {


        String email;
        Integer session_id;
        SSLSocket sock = null;
        List<String> contacts;
        SSLContext sc;
        String server = "friendguarddb.cs.umd.edu";
        int port = 10023;
        String command = "";
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };


        public UserEndSessionTask(Integer session_id, String email) {

            this.email = email;
            this.session_id = session_id;
        }


        //TODO: bind service so the socket connection is shared

        @Override
        protected Boolean doInBackground(Void... params) {

            try {


                sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                sock = (SSLSocket) sc.getSocketFactory().createSocket(server, port);
                sock.setUseClientMode(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "sessionEnd");
                jsonObject.put("username", email);

                jsonObject.put("session_id", session_id);


                Log.e("json", jsonObject.toString());
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                wr.write(jsonObject.toString());
                wr.flush();


                //BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                //String str = rd.readLine();
                //System.out.println(str);
                //rd.close();

                sock.close();
                return true;


            } catch (KeyManagementException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
            // TODO: register the new account here.

        }


        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;


            Log.e("pstEx", "Before success");


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }


}