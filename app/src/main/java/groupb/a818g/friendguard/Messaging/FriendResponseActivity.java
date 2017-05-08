package groupb.a818g.friendguard.Messaging;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import groupb.a818g.friendguard.R;

/**
 * Created by YZ on 5/1/17.
 */

public class FriendResponseActivity extends FragmentActivity {

    private String message;
    private String email;
    private TextView confirmationText;
    private Button confirmInvitation;
    private Button cancelInvitation;
    private Integer sessionId = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_confirmation);

        message = getIntent().getStringExtra("message");
        email = getIntent().getStringExtra("username");

        Log.e("message", message);


        confirmationText = (TextView) findViewById(R.id.notification_text);
        confirmInvitation = (Button) findViewById(R.id.confirm);
        cancelInvitation = (Button) findViewById(R.id.cancel_action);

        confirmationText.setText(message);

        confirmInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });

        cancelInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }











}
