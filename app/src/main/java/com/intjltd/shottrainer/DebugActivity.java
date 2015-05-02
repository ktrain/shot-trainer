package com.intjltd.shottrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.internal.util.ByteUtil;
import com.thalmic.myo.scanner.ScanActivity;


public class DebugActivity extends ActionBarActivity {

    private EditText txtOrientation;
    private EditText txtAccelerometer;
    private EditText txtGyroscope;
    private EditText txtEmgData;

    private DeviceListener myoListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            myo.setStreamEmg(Myo.StreamEmgType.ENABLED);
        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            txtOrientation.setText(
                "x: " + rotation.x() + "\n" +
                "y: " + rotation.y() + "\n" +
                "z: " + rotation.z() + "\n" +
                "w: " + rotation.w()
            );
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
            txtAccelerometer.setText(
                "x: " + accel.x() + "\n" +
                "y: " + accel.y() + "\n" +
                "z: " + accel.z()
            );
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
            txtGyroscope.setText(
                "x: " + gyro.x() + "\n" +
                "y: " + gyro.y() + "\n" +
                "z: " + gyro.z()
            );
        }

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {
            txtEmgData.setText(ByteUtil.bytesToHex(emg));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        txtOrientation = (EditText) findViewById(R.id.txtOrientation);
        txtAccelerometer = (EditText) findViewById(R.id.txtAccelerometer);
        txtGyroscope = (EditText) findViewById(R.id.txtGyroscope);
        txtEmgData = (EditText) findViewById(R.id.txtEmgData);

        Hub hub = Hub.getInstance();
        hub.addListener(myoListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeListener(myoListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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

        if (id == R.id.action_myoScan) {
            onScanActionSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onScanActionSelected() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}
