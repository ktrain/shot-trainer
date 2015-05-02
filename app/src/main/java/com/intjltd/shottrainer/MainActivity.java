package com.intjltd.shottrainer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.internal.util.ByteUtil;
import com.thalmic.myo.scanner.ScanActivity;


public class MainActivity extends ActionBarActivity {

    private TextView lblState;
    private ProgressBar progress;
    private WebView imgSuccess;
    private TextView lblProtip;

    private ShotState currentState = ShotState.IDLE;

    private DeviceListener myoListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            //myo.setStreamEmg(Myo.StreamEmgType.ENABLED);
        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {

        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {

        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {

        }

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {

        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            Log.d("JSIZZLE", "received a pose: " + pose);
            if (pose == Pose.WAVE_OUT) {
                nextState();
            } else if (pose == Pose.WAVE_IN) {
                changeToState(ShotState.IDLE);
            } else if (pose == Pose.FIST) {
                finishedTrial();
            }
        }
    };

    public void nextState() {
        switch (currentState) {
            case IDLE:
                changeToState(ShotState.GRASPING);
                break;
            case GRASPING:
                changeToState(ShotState.LIFTING);
                break;
            case LIFTING:
                changeToState(ShotState.TOSSING);
                break;
            case TOSSING:
                changeToState(ShotState.FLICKING);
                break;
            case FLICKING:
                changeToState(ShotState.GRASPING);
                break;
        }
    }

    public void changeToState(ShotState state) {
        currentState = state;
        switch (state) {
            case IDLE:
                lblState.setText("Idle");
                progress.setProgress(0);
                imgSuccess.setVisibility(View.INVISIBLE);
                lblProtip.setText("");
                break;
            case GRASPING:
                lblState.setText("Grasping");
                progress.setProgress(0);
                imgSuccess.setVisibility(View.INVISIBLE);
                lblProtip.setText("");
                break;
            case LIFTING:
                lblState.setText("Lifting");
                progress.setProgress(1);
                break;
            case TOSSING:
                lblState.setText("Tossing");
                progress.setProgress(2);
                break;
            case FLICKING:
                lblState.setText("Flicking");
                progress.setProgress(3);
                finishedTrial();
                break;
        }
    }

    public void finishedTrial() {
        switch (currentState) {
            case GRASPING:
                lblProtip.setText(R.string.lblProtipGrasp);
                break;
            case LIFTING:
                lblProtip.setText(R.string.lblProtipLift);
                break;
            case TOSSING:
                lblProtip.setText(R.string.lblProtipToss);
                break;
            case FLICKING:
                imgSuccess.setVisibility(View.VISIBLE);
                imgSuccess.loadUrl("http://i.imgur.com/khDZWTq.gif");
                lblProtip.setText(R.string.lblProtipFlick);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblState = (TextView) findViewById(R.id.lblState);
        progress = (ProgressBar) findViewById(R.id.progress);
        imgSuccess = (WebView) findViewById(R.id.imgSuccess);
        lblProtip = (TextView) findViewById(R.id.lblProtip);

        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        hub.addListener(myoListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeListener(myoListener);

        if (isFinishing()) {
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_debug) {
            onDebugActionSelected();
            return true;
        }

        if (id == R.id.action_myoScan) {
            onScanActionSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDebugActionSelected() {
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    protected void onScanActionSelected() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}
