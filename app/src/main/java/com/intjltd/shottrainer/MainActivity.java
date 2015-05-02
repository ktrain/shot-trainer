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

import java.util.LinkedList;


public class MainActivity extends ActionBarActivity {

    private TextView lblState;
    private ProgressBar progress;
    private WebView imgSuccess;
    private ImageView imgMistake;
    private TextView lblProtip;

    private ShotState currentState = ShotState.IDLE;
    private long stateChangeTimestamp = 0;
    private double minPitch = Double.MAX_VALUE;
    private double maxPitch = Double.MIN_VALUE;
    private int numSamples = 0;

    private DeviceListener myoListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            //myo.setStreamEmg(Myo.StreamEmgType.ENABLED);
        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            double yaw = Quaternion.yaw(rotation);
            double pitch = Quaternion.pitch(rotation);
            double roll = Quaternion.roll(rotation);

            switch (currentState) {
                case IDLE:
                    numSamples++;

                    // check if pitch is within acceptable bounds
                    if (pitch < -0.5 || 0.0 < pitch) {
                        numSamples = 0;
                    } else if (pitch < minPitch) {
                        minPitch = pitch;
                    } else if (pitch > maxPitch) {
                        maxPitch = pitch;
                    }

                    // check if the min and max vary too much
                    if (Math.abs(maxPitch - minPitch) > 0.2) {
                        numSamples = 0;
                    }

                    if (numSamples >= 50) {
                        changeToState(ShotState.GATHERED);
                        myo.vibrate(Myo.VibrationType.SHORT);
                    }
                    break;
                case GATHERED:
                    if (pitch > 1.0) {
                        changeToState(ShotState.LIFTING);
                    }
                    break;
                case LIFTING:
                    if (pitch < 0.9) {
                        if (roll > 2.0) {
                            theyMessedUp("Keep the ball in front of your head.");
                        } else if (System.currentTimeMillis() - stateChangeTimestamp < 500) {
                            theyMessedUp("Not enough follow-through.");
                        } else {
                            theyMessedUp("Nice form!");
                        }
                    }
                    break;
            }
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
            } else if (pose == Pose.DOUBLE_TAP) {
                finishedTrial();
            }
        }
    };

    public void nextState() {
        stateChangeTimestamp = System.currentTimeMillis();
        switch (currentState) {
            case IDLE:
                changeToState(ShotState.GATHERED);
                break;
            case GATHERED:
                changeToState(ShotState.LIFTING);
                break;
            case LIFTING:
                changeToState(ShotState.TOSSING);
                break;
            case TOSSING:
                changeToState(ShotState.FLICKING);
                break;
            case FLICKING:
                changeToState(ShotState.GATHERED);
                break;
        }
    }

    public void theyMessedUp(String message) {
        currentState = ShotState.IDLE;
        stateChangeTimestamp = System.currentTimeMillis();
    }

    public void changeToState(ShotState state) {
        currentState = state;
        switch (state) {
            case IDLE:
                lblState.setText("Idle");
                progress.setProgress(0);
                imgSuccess.setVisibility(View.INVISIBLE);
                imgMistake.setVisibility(View.INVISIBLE);
                lblProtip.setText("");
                break;
            case GATHERED:
                lblState.setText("GATHERED");
                progress.setProgress(0);
                imgSuccess.setVisibility(View.INVISIBLE);
                imgMistake.setVisibility(View.INVISIBLE);
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
            case GATHERED:
                imgMistake.setVisibility(View.VISIBLE);
                lblProtip.setText(R.string.lblProtipGrasp);
                break;
            case LIFTING:
                imgMistake.setVisibility(View.VISIBLE);
                lblProtip.setText(R.string.lblProtipLift);
                break;
            case TOSSING:
                imgMistake.setVisibility(View.VISIBLE);
                lblProtip.setText(R.string.lblProtipToss);
                break;
            case FLICKING:
                imgSuccess.setVisibility(View.VISIBLE);
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
        imgMistake = (ImageView) findViewById(R.id.imgMistake);
        lblProtip = (TextView) findViewById(R.id.lblProtip);

        imgSuccess.loadUrl("http://i.imgur.com/khDZWTq.gif");

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
