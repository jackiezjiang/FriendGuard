package groupb.a818g.friendguard;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import groupb.a818g.friendguard.Global.GlobalRepository;

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

                Intent intent = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                intent.putExtra("email", GlobalRepository.username);
                intent.putExtra("contacts", GlobalRepository.UserFriendsContacts.toString());
                startActivity(intent);
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
