package groupb.a818g.friendguard.Global;

import android.util.Log;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

/**
 * Created by kangpc on 5/7/2017.
 * Static class for maintaining app data
 */

public class GlobalRepository {
    public static String username;
    public static String password;
    public static String GCMtoken;
    public static List<String> UserFriendsContacts;

    public static String ServerHost = "friendguarddb.cs.umd.edu";
    public static int ServerPort = 10023;
    public static int SessionID;
    public static List<String> UserFriendsContactsinSession;
    public static long AutoCheckinIntervalMins;
    public static String startTime;
    public static String endTime;

    public static String getUsername()
    {
        if(username.isEmpty()) {
            Log.e("GlobalRepo","Username is empty");
        }
        return username;
    }
    public static String getPassword()
    {
        if(password.isEmpty()) {
            Log.e("GlobalRepo","Password is empty");
        }
        return password;
    }
    public static String getGCMtoken()
    {
        if(GCMtoken.isEmpty()) {
            Log.e("GlobalRepo","GCMtoken is empty");
        }
        return GCMtoken;
    }
    public static List<String> getUserFriendsContacts()
    {
        if(UserFriendsContacts.isEmpty()) {
            Log.e("GlobalRepo","UserFriendsContacts is empty");
        }
        return UserFriendsContacts;
    }
    public static List<String> UserFriendsContactsinSession()
    {
        if(UserFriendsContactsinSession.isEmpty()) {
            Log.e("GlobalRepo","UserFriendsContactsinSession is empty");
        }
        return UserFriendsContactsinSession;
    }



}
