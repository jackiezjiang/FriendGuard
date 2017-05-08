package groupb.a818g.friendguard.Messaging;

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

import groupb.a818g.friendguard.Global.GlobalRepository;
import groupb.a818g.friendguard.R;

/**
 * Created by YZ on 5/2/17.
 */

public class FriendConfirmInvitationActivity extends FragmentActivity {
    private String message;
    private String email;
    private TextView confirmationText;
    private Button confirmInvitation;
    private Button cancelInvitation;
    private Integer sessionId = null;

    FriendAcceptTask mAuthTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_confirmation);

        message = getIntent().getStringExtra("message");
        email = getIntent().getStringExtra("email");

        Log.e("message", message);
        Log.e("username from invi", email);

        if (message.equals("newSession")) {
            message = getIntent().getStringExtra("toDisplay");
            sessionId = (Integer) getIntent().getExtras().get("session_id");
        }

        confirmationText = (TextView) findViewById(R.id.notification_text);
        confirmInvitation = (Button) findViewById(R.id.confirm);
        cancelInvitation = (Button) findViewById(R.id.cancel_action);

        confirmationText.setText(message);

        confirmInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionId != null) {

                    mAuthTask = new FriendAcceptTask(email, sessionId);
                    mAuthTask.execute((Void) null);
                    GlobalRepository.addSession(email,sessionId);


                }


            }
        });

        cancelInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }



    public class FriendAcceptTask extends AsyncTask<Void, Void, Boolean> {


        Integer session_id;
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


        public FriendAcceptTask(String email, Integer session_id) {
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
                jsonObject.put("type", "sessionAccept");
                jsonObject.put("username", email);
                jsonObject.put("session_id", session_id);




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
            finish();




        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }










    }


}
