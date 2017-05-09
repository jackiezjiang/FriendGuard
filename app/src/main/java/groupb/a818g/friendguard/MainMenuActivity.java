package groupb.a818g.friendguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import groupb.a818g.friendguard.Global.GlobalRepository;
import groupb.a818g.friendguard.Global.SessionInfo;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button owenerButton = (Button) findViewById(R.id.mainmenu_owner_button);
        Button friendButton = (Button) findViewById(R.id.mainmenu_friend_button);

        owenerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                intent.putExtra("email", GlobalRepository.username);
                intent.putExtra("contacts", GlobalRepository.UserFriendsContacts.toString());
                startActivity(intent);

                //need to check if the user is already in a session or not
                // case a. not in at session ->main activity

                // case b. in a session ->location activity

            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GlobalRepository.ParticipatingSessions==null || GlobalRepository.ParticipatingSessions.isEmpty())  return ;


                final CharSequence colors[] = new CharSequence[GlobalRepository.ParticipatingSessions.size()];


                for(int i=0;i<GlobalRepository.ParticipatingSessions.size();i++)
                {
                    colors[i]=GlobalRepository.ParticipatingSessions.get(i).ownerName;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                builder.setTitle("Select Your Friend");

                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        Log.e("MainMenu","selected user "+colors[which].toString());
                        SessionInfo.CheckInToken ct = GlobalRepository.getLastCheckIn(colors[which].toString());
                        if(ct==null) return;
                        Intent intent = new Intent(MainMenuActivity.this, ViewAlertActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("lat", ct.lat);
                        intent.putExtra("lng", ct.lng);
                        intent.putExtra("time", ct.time);
                        intent.putExtra("user", ct.username);
                        intent.putExtra("title","STATUS");
                        startActivity(intent);
                    }
                });
                builder.show();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}
