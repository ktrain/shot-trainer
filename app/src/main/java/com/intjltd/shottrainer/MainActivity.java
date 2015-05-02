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

    private EditText logger;

    private EditText txtOrientation;
    private EditText txtAccelerometer;
    private EditText txtGyroscope;
    private EditText txtEmgData;

//    private EditText txtOrientationX;
//    private EditText txtOrientationY;
//    private EditText txtOrientationZ;
//    private EditText txtOrientationW;
//    private EditText txtAccelerometerX;
//    private EditText txtAccelerometerY;
//    private EditText txtAccelerometerZ;
//    private EditText txtGyroscopeX;
//    private EditText txtGyroscopeY;
//    private EditText txtGyroscopeZ;

    private void addText(CharSequence line) {
        logger.setText(logger.getText() + "\n" + line);
    }

    private void setOutput(EditText txtField, double value) {
        txtField.setText(String.valueOf(value));

//        txtField.setTextColor(Color.BLACK);
//        if (value < 0) {
//            txtField.setTextColor(Color.RED);
//        } else if (value > 0) {
//            txtField.setTextColor(Color.GREEN);
//        }
    }

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
//            setOutput(txtOrientationX, rotation.x());
//            setOutput(txtOrientationY, rotation.y());
//            setOutput(txtOrientationZ, rotation.z());
//            setOutput(txtOrientationW, rotation.w());
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
            txtAccelerometer.setText(
                "x: " + accel.x() + "\n" +
                "y: " + accel.y() + "\n" +
                "z: " + accel.z()
            );
//            setOutput(txtAccelerometerX, accel.x());
//            setOutput(txtAccelerometerY, accel.y());
//            setOutput(txtAccelerometerZ, accel.z());
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
            txtGyroscope.setText(
                "x: " + gyro.x() + "\n" +
                "y: " + gyro.y() + "\n" +
                "z: " + gyro.z()
            );
//            setOutput(txtGyroscopeX, gyro.x());
//            setOutput(txtGyroscopeY, gyro.y());
//            setOutput(txtGyroscopeZ, gyro.z());
        }

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {
            txtEmgData.setText(ByteUtil.bytesToHex(emg));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger = (EditText) findViewById(R.id.txtLogger);

//        txtOrientationX = (EditText) findViewById(R.id.txtOrientationX);
//        txtOrientationY = (EditText) findViewById(R.id.txtOrientationY);
//        txtOrientationZ = (EditText) findViewById(R.id.txtOrientationZ);
//        txtOrientationW = (EditText) findViewById(R.id.txtOrientationW);
//        txtAccelerometerX = (EditText) findViewById(R.id.txtAccelerometerX);
//        txtAccelerometerY = (EditText) findViewById(R.id.txtAccelerometerY);
//        txtAccelerometerZ = (EditText) findViewById(R.id.txtAccelerometerZ);
//        txtGyroscopeX = (EditText) findViewById(R.id.txtGyroscopeX);
//        txtGyroscopeY = (EditText) findViewById(R.id.txtGyroscopeY);
//        txtGyroscopeZ = (EditText) findViewById(R.id.txtGyroscopeZ);

        txtOrientation = (EditText) findViewById(R.id.txtOrientation);
        txtAccelerometer = (EditText) findViewById(R.id.txtAccelerometer);
        txtGyroscope = (EditText) findViewById(R.id.txtGyroscope);
        txtEmgData = (EditText) findViewById(R.id.txtEmgData);

        findViewById(R.id.btnStart).requestFocus();

        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
