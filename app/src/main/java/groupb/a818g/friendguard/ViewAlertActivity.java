package groupb.a818g.friendguard;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.port;

public class ViewAlertActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private double  lat;
    private double lng;
    private String time;
    private String username;

    private TextView latView;
    private TextView lngView;
    private TextView timeView;
    private TextView safety;
    private LinearLayout MenuCall;
    private LinearLayout MenuMsg;
    private LinearLayout exit;
    private TextView ownerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        lat = Double.valueOf(getIntent().getStringExtra("lat"));
        lng = Double.valueOf(getIntent().getStringExtra("lng"));
        time = getIntent().getStringExtra("time");
        username = getIntent().getStringExtra("user");
        String titleText = getIntent().getStringExtra("title");
        mapFragment.getMapAsync(this);




        latView = (TextView) findViewById(R.id.Lat_number);
        lngView = (TextView) findViewById(R.id.Lng_number);
        timeView = (TextView) findViewById(R.id.time_number);
        safety = (TextView) findViewById(R.id.Safety_Info);
        exit = (LinearLayout) findViewById(R.id.AlertView_Exit);
        ownerName = (TextView) findViewById(R.id.AlertView_OwnerNameText);
        ownerName.setText(username);
        MenuCall = (LinearLayout) findViewById(R.id.AlertView_Call);
        MenuMsg = (LinearLayout) findViewById(R.id.AlertView_Msg);

        MenuCall.setOnTouchListener(new View.OnTouchListener()
        {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                ImageView image=null;
                int count =  ((LinearLayout) v).getChildCount();
                for (int i = 0; i < count; i++) {
                    View vv =  ((LinearLayout) v).getChildAt(i);
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
        MenuCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        MenuMsg.setOnTouchListener(new View.OnTouchListener()
        {
            private Rect rect;
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                ImageView image=null;
                int count = ((LinearLayout) v).getChildCount();
                for (int i = 0; i < count; i++) {
                    View vv = ((LinearLayout) v).getChildAt(i);
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
        MenuMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        TextView titleView = (TextView) findViewById(R.id.AlertView_TitleText);
        titleView.setText(titleText);
        if(titleText.equals("STATUS")) {
            ImageView iconView = (ImageView) findViewById(R.id.AlertView_Icon);
            iconView.setColorFilter(Color.argb(200, 190, 221, 30));
        } else {
            ImageView iconView = (ImageView) findViewById(R.id.AlertView_Icon);
            iconView.setColorFilter(Color.argb(200, 190, 30, 30));
        }
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
