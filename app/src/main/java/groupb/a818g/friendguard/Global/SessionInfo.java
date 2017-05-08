package groupb.a818g.friendguard.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangpc on 5/8/2017.
 */

public class SessionInfo {
    public String ownerName;
    public Integer sessionID;
    public List<CheckInToken> CheckIns;
    SessionInfo(){
        CheckIns = new ArrayList<CheckInToken>();
    }
    SessionInfo(String ownername_,Integer sessionID_){
        ownerName=ownername_;
        sessionID=sessionID_;
        CheckIns = new ArrayList<CheckInToken>();
    }
    public void AddCheckinToken(String lat_, String lng_, String username_, String time_)
    {
        CheckInToken ck = new CheckInToken(lat_,lng_,username_,time_);
        CheckIns.add(ck);
    }
    public class CheckInToken {
        public String lat;
        public String lng;
        public String username;
        public String time;
        CheckInToken(String lat_, String lng_, String username_, String time_){
            lat=lat_;
            lng=lng_;
            username=username_;
            time=time_;
        }
    }
}