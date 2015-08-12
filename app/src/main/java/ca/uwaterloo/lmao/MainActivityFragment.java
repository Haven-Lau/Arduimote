package ca.uwaterloo.lmao;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothAdapter mAdapter = null;
    private static OutputStream outStream = null;
    private int message = 0;
    private int aButton = 1;
    private int bButton = 1;
    private int cButton = 1;
    private int dButton = 1;
    private int eButton = 1;
    private int fButton = 1;
    private int gyButton = 0;
    private float xcoord = 0;
    private float ycoord = 0;
    private int f1;
    private int s2;
    private int t3;
    private int f4;
    private int CS;
    private int case_ = 0;
    private int xPower = 0; // processed x-y power
    private int yPower = 0;
    private float initX = 0; // initial touch position
    private float initY = 0;
    private float origX = 0; // joystick button locations
    private float origY = 0;
    private long data_ = 0;
    private double joyPower = 0;
    private double joyAngle = 0;
    private static BluetoothSocket mSocket = null;
    private static Timer timer;
    private final Handler handler = new Handler();
    private static BluetoothDevice device;
    private ImageView btButton;
    private ImageView disconnectButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView serO = (TextView) rootView.findViewById(R.id.serialOut);

        // Get Bluetooth adapter
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        // Set aButton = 1 when A is pressed
        Button a = (Button)rootView.findViewById(R.id.a);
        a.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        aButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        aButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set bButton = 1 when B is pressed
        Button b = (Button)rootView.findViewById(R.id.b);
        b.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        bButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set cButton = 1 when C is pressed
        Button c = (Button)rootView.findViewById(R.id.c);
        c.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        cButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set dButton = 1 when D is pressed
        Button d = (Button)rootView.findViewById(R.id.d);
        d.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        dButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set eButton = 1 when E is pressed
        Button e = (Button)rootView.findViewById(R.id.e);
        e.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        eButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        eButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set fButton = 1 when F is pressed
        Button f = (Button)rootView.findViewById(R.id.f);
        f.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fButton = 0;
                        return true;
                    case MotionEvent.ACTION_UP:
                        fButton = 1;
                        return true;
                }
                return false;
            }
        });

        // Set fButton = 1 when F is pressed
        //final Button gyroButton = (Button)rootView.findViewById(R.id.gyroButton);
        //gyroButton.setOnTouchListener(new View.OnTouchListener() {
        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {
        //        switch (event.getAction()) {
        //            case MotionEvent.ACTION_DOWN:
        //                if(gyButton == 1){
        //                    // already on, turning off
        //                    gyroButton.setTextColor(Color.parseColor("#ff787979"));
        //                    gyButton = 0;
        //                }else{
        //                    // already off, turning on
        //                    gyroButton.setTextColor(Color.parseColor("#9932CC"));
        //                    gyButton = 1;
        //                }
        //                return true;
        //        }
        //        return false;
        //    }
        //});

        btButton = (ImageView)rootView.findViewById(R.id.btButton);
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.explodeDialog2();
            }
        });

        disconnectButton = (ImageView)rootView.findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectDevice();
            }
        });

        // Changes x-coordinate and y-coordinate when joystick button is touched
        final ImageButton joy = (ImageButton)rootView.findViewById(R.id.joybutton);
        final TextView textView = (TextView) rootView.findViewById(R.id.coordinates);
        joy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = event.getRawX();
                        initY = event.getRawY();
                        origX = joy.getX();
                        origY = joy.getY();
                        joy.setImageResource(R.mipmap.circlejoystick_s_pressed);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        xcoord = event.getRawX();
                        ycoord = event.getRawY();
                        joyPower = hypot(xcoord - initX, ycoord - initY); // get distance
                        joyAngle = atan2(initY-ycoord,xcoord-initX); // get angle
                        if (joyPower > 255) // limiting radius to 255
                            joyPower = 255;
                        xPower = (int)(joyPower * cos(joyAngle)*2); // -509 to +509
                        yPower = (int)(joyPower * sin(joyAngle)*2); // -509 to +509
                        textView.setText("X: " + String.valueOf(xPower + 509) + " Y: " + String.valueOf(yPower + 509));
                        joy.setX(origX + xPower / 2);
                        joy.setY(origY - yPower / 2);
                        return true;
                    case MotionEvent.ACTION_UP:
                        xcoord = 509;
                        ycoord = 509;
                        initX = 0;
                        initY = 0;
                        xPower = 0;
                        yPower = 0;
                        joy.setX(origX);
                        joy.setY(origY);
                        joy.setImageResource(R.mipmap.circlejoystick_s);
                        textView.setText("X: " + String.valueOf((int)(xcoord)) + " Y: " + String.valueOf((int)(ycoord)));
                        return true;
                }
                return false;
            }
        });

        // Get Sensor Manager
        SensorManager sensorManager = (SensorManager) rootView.getContext()
                .getSystemService(rootView.getContext().SENSOR_SERVICE);

        // Get Sensor
        Sensor accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Sensor Event Listener
        SensorEventListener acceleListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // x = event.values[0], y = event.values[1], z = event.values[2]
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // No need code here
            }
        };

        // Register Listener to sensor
        sensorManager.registerListener(acceleListener, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        return rootView;
    }

    private void checkBTState() {
        if (mAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
        } else {
            if (!mAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
            }
        }
    }

    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] {UUID.class});
                return (BluetoothSocket)m.invoke(device,MY_UUID);
            } catch (Exception e) { }
        }
        return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    public void connect (String address, Context context) {
        device = null;
        try {
            device = mAdapter.getRemoteDevice(address);
        }catch (Exception e){
            Toast.makeText(context,"Invalid Address",Toast.LENGTH_LONG).show();
            device = null;
        }
        if (device == null)
            return;

        try {
            mSocket = createBluetoothSocket(device);
        } catch (Exception e) {

        }

        mAdapter.cancelDiscovery();

        try {
            // try connecting to socket
            mSocket.connect();
        } catch (Exception e) {
            try {
                mSocket.close();
            } catch (Exception e1) {
            }
        }

        try {
            outStream = mSocket.getOutputStream();
            timer = new Timer();
            timer.schedule(new SendMessage(), 0, 10);
            Toast.makeText(context, "Connection Established", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

        }
    }

    public static boolean isConnected() {
        // Check if the phone has already connected to bluetooth device
        return device != null;
    }

    private void disconnectDevice() {
        if (device != null && timer != null) {
            device = null;
            timer.cancel();
            timer.purge();
            timer = null;
            try {
                outStream.close();
            } catch (Exception e) {
            }
            outStream = null;
            mSocket = null;
            Toast.makeText(getActivity(), "Bluetooth Device Disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    private class SendMessage extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (outStream != null) {
                        // encode data into 1 big block
                        data_ = 0;
                        data_ = data_ | (long) (xPower + 509) << 22;
                        data_ = data_ | (long) (yPower + 509) << 12;
                        data_ = data_ | (long) (aButton) << 11;
                        data_ = data_ | (long) (cButton) << 10;
                        data_ = data_ | (long) (bButton) << 9;
                        data_ = data_ | (long) (dButton) << 8;
                        data_ = data_ | (long) (1) << 7;
                        data_ = data_ | (long) (fButton) << 6;
                        data_ = data_ | (long) (eButton) << 5;
                        // break down into 4 bytes for transmission
                        f1 = (int) (data_ >> 24);
                        s2 = (int) ((data_ & 0x00FF0000) >> 16);
                        t3 = (int) ((data_ & 0x0000FF00) >> 8);
                        f4 = (int) ((data_ & 0x000000FF));
                        CS = 8;
                        // XOR checksum
                        CS ^= f1;
                        CS ^= s2;
                        CS ^= t3;
                        CS ^= f4;

                        try {
                            message = 0x06;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0x85;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0x08;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = f1;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = s2;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = t3;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = f4;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = 0;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_++;
                        try {
                            message = CS;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        case_ = 0;
                    }
                }
            });
        }
    }
}

