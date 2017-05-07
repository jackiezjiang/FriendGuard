package groupb.a818g.friendguard;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by YZ on 4/9/17.
 */

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private Integer session_id = null;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().;
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

            String typeLabel = data.getString("type");
            String message  = null;
        Log.e("GCM","GCM message received");
            Log.e("GCM", "GCM label"+typeLabel);
            if (typeLabel.equals("newFriend")) {

                message = data.getString("uname") + " request FriendGuard From you";
                sendNotification(message);


            } else if (typeLabel.equals("checkin")) {
                String uname = data.getString("username");
                String lat = data.getString("lat");
                String lng = data.getString("lon");
                String time = data.getString("time");



                sendNotificationAndStartMap(uname, lat, lng, time);

            } else if (typeLabel.equals("newSession")) {
                Log.e("label", typeLabel);
                String email = data.getString("friend");

                session_id= Integer.valueOf( data.getString("session_id"));
                Log.e("session", session_id.toString());
                Integer a_checkin_interval = Integer.valueOf( data.getString("a_checkin_interval"));
                Integer p_checkin_interval =  Integer.valueOf( data.getString("p_checkin_interval"));
                String start = data.getString("start");
                String end = data.getString("end");
                String msg = data.getString("message");

                String toDisplayMsg = email + ": " + "From " + start + " To " + end + " At intervel: " + a_checkin_interval.toString();

                Log.e("display", toDisplayMsg);


                sendStartSessionInvitation(toDisplayMsg, email, session_id);

            } else if (typeLabel.equals("sessionAccept")) {
                session_id= Integer.valueOf( data.getString("session_id"));
                String toWhom = data.getString("friend");
                sendNotificationBack(session_id, toWhom);

            } else if (typeLabel.equals("alert")) {
                String lat = data.getString("lat");
                String lng = data.getString("lon");
                String username = data.getString("username");
                String time = data.getString("time");


                sendNotificationAndStartMap(lat,  lng, username,time);




            }

            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Message: " + message);

            /*TODO: will check the type of message, and decide what the sendNotification to do. If type
             *  is   "newFriend": then go to a message activity showing "Please be my friendguard From user"
               *  if "setUp" type: display to start, end, timeInterval as message activity
               *  if "Danger" type: display the map with location
             */

            // [START_EXCLUDE]
            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */

            // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     *  GCM message received.
     */



    private void sendNotificationBack(Integer session_id, String toWhom) {
        Intent intent = new Intent(this, UserResponseActivity.class);
        intent.putExtra("session_id", session_id);
        intent.putExtra("email", toWhom);
        Log.e("toWhomGCMlistener",toWhom);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("intent", "intent");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.e("intent", "new pending");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle("GCM Message")
                .setContentText("Your friend accept the invitation")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        Log.e("notification","step1");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Log.e("notification","step2");

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }






    private void sendNotification(String message) {
        Log.e("Notification",message);
        Intent intent = new Intent(this, FriendResponseActivity.class);
        intent.putExtra("message", message);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("intent", "intent");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.e("intent", "new pending");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        Log.e("notification","step1");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Log.e("notification","step2");

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    private void sendStartSessionInvitation(String msg, String email, Integer session_id) {
        Intent intent = new Intent(this, FriendConfirmInvitationActivity.class);
        intent.putExtra("message", "newSession");
        intent.putExtra("toDisplay", msg);
        intent.putExtra("email", email);
        intent.putExtra("session_id", session_id);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("intent", "intent");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.e("intent", "new pending");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle("GCM Message")
                .setContentText("New Session")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        Log.e("notification","step1");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Log.e("notification","step2");

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }





    private void sendNotificationAndStartMap(String lat, String lng, String username, String time) {


        Intent intent = new Intent(this, SimpleMapActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        intent.putExtra("time", time);
        intent.putExtra("user", username);
        String message = "Your Friend " + username + " sent Safety Alert at: "+ time + "from lat :" + lat +
                "lng: " + lng + " .";
        //intent.putExtra("message", message);



        Log.e("intent", "intent");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.e("intent", "new pending");


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);









        Log.e("notification","step1");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Log.e("notification","step2");

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }





}