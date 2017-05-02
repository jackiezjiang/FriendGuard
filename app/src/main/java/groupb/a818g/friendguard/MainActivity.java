package groupb.a818g.friendguard;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {


    private Button startTime, endTime;
    UserSetupTask mAuthTask;

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long MEASURE_TIME = 1000 * 30;
    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static final float MIN_DISTANCE = 10.0f;

    // Views for display location information
    private TextView mAccuracyView;
    private TextView mTimeView;
    private TextView mLatView;
    private TextView mLngView;

    private int mTextViewColor = Color.GRAY;

    // Current best location estimate
    private Location mBestReading;

    // Reference to the LocationManager and LocationListener
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private final String TAG = "LocationGetLocationActivity";
    private int AUTO_INTERVAL;
    private int MANNUAL_INTERVAL;
    private boolean SAFE = true;
    private TextView auto_update_inteval;
    private TextView mannul_checkin_interval;
    private  TextView invite_friends;
    private Button start_session;
    private boolean mFirstUpdate = true;
    private String email;
    private List<String> contacts;
    private String whenToStart, whenToEnd;



    private String currentDate;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        auto_update_inteval = (TextView) findViewById(R.id.auto_inteval);// on click of button display the dialog

        invite_friends= (TextView) findViewById(R.id.invite_friends);
        start_session = (Button) findViewById(R.id.start_session);
        startTime = (Button) findViewById(R.id.startTime);
        endTime = (Button) findViewById(R.id.endTime);
        email = getIntent().getStringExtra("email");


        final String contactsString = getIntent().getStringExtra("contacts");
        contacts =  new ArrayList<String>(Arrays.asList(contactsString.replace("[", "").replace("]","").replace(" ","").split(",")));

        currentDate = new SimpleDateFormat("YYYY-MM-dd").format(new Date());


        auto_update_inteval.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                showAuto();
            }
        });



        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showStartTimePicker();


            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEndTimePicker();

            }
        });


        start_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("start", whenToStart);


                mAuthTask = new UserSetupTask(email, whenToStart, whenToEnd , AUTO_INTERVAL, contacts);
                mAuthTask.execute((Void) null);


            }
        });





    }


    private void showStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {



                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        startTime.setText(hourOfDay + ":" + minute);
                        whenToStart = hourOfDay + ":" + minute + ":" + "00";
                    }
                }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);

        timePickerDialog.show();

    }

    private void showEndTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {



                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        endTime.setText(hourOfDay + ":" + minute);
                        whenToEnd = hourOfDay + ":" + minute + ":" + "00";
                    }
                }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
        timePickerDialog.show();

    }



    public void showAuto() {
        final NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(1);
        picker.setMaxValue(30);

        final FrameLayout layout = new FrameLayout(this);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        new AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Auto Update Interval minutes")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AUTO_INTERVAL = picker.getValue();
                        auto_update_inteval.setText("Auto Updates every " + AUTO_INTERVAL + " Minutes");
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();


    }




    public class UserSetupTask extends AsyncTask<Void, Void, Boolean> {


        String startTime;
        String endTime;
        int interval;
        String email;
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


        public UserSetupTask(String email, String startTime, String endTime, int interval, List<String> contacts) {
            this.email = email;
            this.startTime = startTime;
            this.endTime = endTime;
            this.interval = interval;
            this.contacts = contacts;

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
                jsonObject.put("type", "sessionStart");
                jsonObject.put("username", email);
                jsonObject.put("a_checkin_interval", interval);
                jsonObject.put("p_checkin_interval", 30);
                jsonObject.put("start", currentDate + " " + startTime);
                jsonObject.put("end", currentDate + " " + endTime);
                jsonObject.put("message", "please confirm the invitation");
                jsonObject.put("friends", contacts);




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
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            intent.putExtra("Auto Update Interval", AUTO_INTERVAL);
            intent.putExtra("Mannual Check-in Interval", MANNUAL_INTERVAL);
            intent.putExtra("email", email);
            Log.e("pstEx",new Integer(AUTO_INTERVAL).toString());
            startActivity(intent);




        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }




    }





}







