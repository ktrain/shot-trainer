package com.intjltd.shottrainer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

    private DeviceListener myoListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            myo.setStreamEmg(Myo.StreamEmgType.ENABLED);
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
