package groupb.a818g.friendguard;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.R.attr.port;

/**
 * Created by YZ on 4/12/17.
 */

public class SocketBindService extends Service {


    private final IBinder socketBinder = new SocketBinder();
    private SSLSocket sock= null;
    private SSLContext sc;
    private String server = "friendguarddb.cs.umd.edu";
    private int port = 10023;
    private String command = "";

    public SocketBindService() {
        openSocket();

    }



    TrustManager[] trustAllCerts = new TrustManager[] {
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



    @Override
    public IBinder onBind(Intent intent) {
        return socketBinder;
    }

    public class SocketBinder extends Binder {
        // Now you have access to all your Service's public methods
        public SocketBindService getService() {
            return SocketBindService.this;
        }
    }


    private void openSocket() {
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sock = (SSLSocket) sc.getSocketFactory().createSocket(server, port);
            sock.setUseClientMode(true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.e("Unknowhost", "unknown host");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void sendCommand(String msg) throws IOException {
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

        wr.write(msg);
        wr.flush();

        BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String str;
        while ((str = rd.readLine()) != null){
            System.out.println(str);
        }
        rd.close();



    }

}
