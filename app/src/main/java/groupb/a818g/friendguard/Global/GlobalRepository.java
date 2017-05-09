package groupb.a818g.friendguard.Global;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
    public static List<String> FriendsinmySession;
    public static List<SessionInfo> ParticipatingSessions;
    public static long AutoCheckinIntervalMins;
    public static String startTime;
    public static String endTime;
    public static void init() {
        SessionID = -1;
        if (FriendsinmySession == null) FriendsinmySession = new ArrayList<>();
            else FriendsinmySession.clear();
        if (ParticipatingSessions == null) ParticipatingSessions = new ArrayList<SessionInfo>();
        else ParticipatingSessions.clear();
        if (UserFriendsContacts == null) UserFriendsContacts = new ArrayList<>();
        else UserFriendsContacts.clear();

    }
    public static void addSession(String uname, Integer sessionID){
        for (SessionInfo si:ParticipatingSessions
             ) {
            if(si.ownerName==uname) return;
        }
        SessionInfo siNew = new SessionInfo(uname,sessionID);
        ParticipatingSessions.add((siNew));
    }
    public static SessionInfo.CheckInToken getLastCheckIn(String uname){
        for (SessionInfo si:ParticipatingSessions
                ) {
            if(si.ownerName==uname) {
                if(si.CheckIns==null || si.CheckIns.size()==0) return null;
                return si.CheckIns.get(si.CheckIns.size()-1);
            }
        }
        return null;
    }
    public static void updateCheckIn(String lat_, String lng_, String username_, String time_){
        for (SessionInfo si:ParticipatingSessions
                ) {
            if(si.ownerName.equals(username_)) {
                si.AddCheckinToken(lat_,lng_,username_,time_);
                return ;
            }
        }
        Log.e("GlobalRepo","fail to updateCheckin");
    }
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
        if(FriendsinmySession.isEmpty()) {
            Log.e("GlobalRepo","UserFriendsContactsinSession is empty");
        }
        return FriendsinmySession;
    }


}



