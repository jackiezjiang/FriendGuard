package groupb.a818g.friendguard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by YZ on 5/1/17.
 */

public class SimpleMapActivity extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private double  lat;
        private double lng;
        private String time;
        private String username;

    private TextView latView;
    private TextView lngView;
    private TextView timeView;
    private TextView safety;
    private TextView exit;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_simple_map);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            lat = Double.valueOf(getIntent().getStringExtra("lat"));
            lng = Double.valueOf(getIntent().getStringExtra("lng"));
            time = getIntent().getStringExtra("time");
            username = getIntent().getStringExtra("user");
            mapFragment.getMapAsync(this);



            latView = (TextView) findViewById(R.id.Lat_number);
            lngView = (TextView) findViewById(R.id.Lng_number);
            timeView = (TextView) findViewById(R.id.time_number);
            safety = (TextView) findViewById(R.id.Safety_Info);
            exit = (TextView) findViewById(R.id.EXIT);




            latView.setText(Double.toString(lat));
            lngView.setText(Double.toString(lng));
            timeView.setText(time);
            safety.setText("IN DANGER");

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            // Add a marker in Sydney, Australia, and move the camera.
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            LatLng sydney = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your friend " + username + "is in danger here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


        }
    }






