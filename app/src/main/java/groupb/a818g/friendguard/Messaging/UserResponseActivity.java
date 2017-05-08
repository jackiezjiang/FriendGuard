package groupb.a818g.friendguard.Messaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import groupb.a818g.friendguard.Global.GlobalRepository;
import groupb.a818g.friendguard.R;
import groupb.a818g.friendguard.ViewMySessionActivity;

/**
 * Created by YZ on 5/2/17.
 */

public class UserResponseActivity extends FragmentActivity {



    private String message;
    private TextView confirmationText;
    private Button confirmInvitation;
    private Button cancelInvitation;
    private Integer sessionId = null;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_confirmation);

        message = getIntent().getStringExtra("message");


        sessionId = (Integer) getIntent().getExtras().get("session_id");
        email = getIntent().getStringExtra("email");

        confirmationText = (TextView) findViewById(R.id.notification_text);
        confirmInvitation = (Button) findViewById(R.id.confirm);
        cancelInvitation = (Button) findViewById(R.id.cancel_action);

        confirmationText.setText(message);

        confirmInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserResponseActivity.this, ViewMySessionActivity.class);
                intent.putExtra("session_id", sessionId);
                intent.putExtra("email", email);
                Log.e("sessionid from user res", Integer.toString(sessionId));
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);

                GlobalRepository.SessionID = sessionId;
                GlobalRepository.FriendsinmySession.add(email);
                startActivity(intent);

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
