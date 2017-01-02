package ua.pp.rudiki;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SwitchStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount googleAccount;

    TextView switch1status, switch2status;
    CheckBox switch1checkbox, switch2checkbox;
    Date switch1turnoffTime = null, switch2turnoffTime = null;

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> scheduledFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1status = (TextView)findViewById(R.id.switch1status);
        switch2status = (TextView)findViewById(R.id.switch2status);
        switch1checkbox = (CheckBox)findViewById(R.id.switch1checkbox);
        switch2checkbox = (CheckBox)findViewById(R.id.switch2checkbox);

        signIn();
    }

    void onSignedIn() {
        requestSwitchState();
        startService(new Intent(this, RudikiGpsService.class));
    }

    @Override
    public void onSwitchStateReceived(String response) {
        if(response != null) {
            try {
                JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray states = json.getJSONArray("states");
                JSONArray turnoffs = json.getJSONArray("turnoffs");

                int switch1secondsToTurnoff = getSecondsFromString(turnoffs.getString(0));
                int switch2secondsToTurnoff = getSecondsFromString(turnoffs.getString(1));

                Date now = new Date();
                switch1turnoffTime = (switch1secondsToTurnoff > 0) ? new Date(now.getTime()+switch1secondsToTurnoff*1000) : null;
                switch2turnoffTime = (switch2secondsToTurnoff > 0) ? new Date(now.getTime()+switch2secondsToTurnoff*1000) : null;

                switch1checkbox.setEnabled(true);
                switch1checkbox.setChecked(states.getInt(0) != 0);
                switch2checkbox.setEnabled(true);
                switch2checkbox.setChecked(states.getInt(1) != 0);
                switch1status.setText(generateStatusText(switch1turnoffTime));
                switch2status.setText(generateStatusText(switch2turnoffTime));

                scheduleCountDownTimer();
            }
            catch (JSONException e) {
            }
        }
    }

    void signIn() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        this.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                googleAccount = result.getSignInAccount();
                Log.e(TAG, "successfully signed in as "+googleAccount.getDisplayName());
                onSignedIn();
            } else {
                Log.e(TAG, "sign-in failed");
            }
        }
    }

    protected void requestSwitchState() {
        new RetrieveSwitchStateTask(this).execute();
    }


    private int getSecondsFromString(String turnoff) {
        int secondsToTurnoff = 0;
        try {
            secondsToTurnoff = Integer.parseInt(turnoff);
        }
        catch(NumberFormatException ex) {
        }

        return secondsToTurnoff;
    }

    private String generateStatusText(Date turnoffTime) {
        if(turnoffTime != null) {
            long secondsToTurnoff = (turnoffTime.getTime() - new Date().getTime()) / 1000;
            if (secondsToTurnoff > 0) {
                long minutes = secondsToTurnoff / 60;
                long secondsRest = secondsToTurnoff - minutes * 60;

                return "Automatic turn-off in " + minutes + " min " + secondsRest + " sec";
            }
        }

        return "Turned off";
    }


    private void scheduleCountDownTimer() {
        if(scheduledFuture != null && !scheduledFuture.isCancelled())
            scheduledFuture.cancel(false);

        Date now = new Date();
        if((switch1turnoffTime != null && switch1turnoffTime.after(now)) ||
           (switch2turnoffTime != null && switch2turnoffTime.after(now))) {

            scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "Timer worker");

                                Date now = new Date();

                                if(switch1turnoffTime != null && switch1turnoffTime.after(now)) {
                                    switch1status.setText(generateStatusText(switch1turnoffTime));
                                } else {
                                    switch1checkbox.setChecked(false);
                                    switch1turnoffTime = null;
                                }
                                if(switch2turnoffTime != null && switch2turnoffTime.after(now)) {
                                    switch2status.setText(generateStatusText(switch2turnoffTime));
                                } else {
                                    switch2checkbox.setChecked(false);
                                    switch2turnoffTime = null;
                                }

                                if(switch1turnoffTime == null && switch2turnoffTime == null) {
                                    scheduledFuture.cancel(false);
                                    requestSwitchState();
                                }
                            }
                        });

                    }
                }, 0, 1, TimeUnit.SECONDS);

        }
    }

    public void onRefreshBtnClick(View view) {
        requestSwitchState();
    }

    public void onSwitchCheckboxClick(View view) {
        CheckBox checkbox = (CheckBox)view;

        int index = (checkbox == switch1checkbox) ? 0 : 1;
        int state = checkbox.isChecked() ? 1 : 0;

        new SendSwitchStateTask(index, state, this).execute();
    }

}
