package ca.uwaterloo.lmao;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static java.lang.Math.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothAdapter mAdapter = null;
    private static OutputStream outStream = null;
    private String address = "";
    private int message = 0;
    private int aButton = 0;
    private int bButton = 0;
    private int cButton = 0;
    private int dButton = 0;
    private int eButton = 0;
    private int fButton = 0;
    private float xcoord = 0;
    private float ycoord = 0;
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
    private static TimerTask sendMessage;
    private final Handler handler = new Handler();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Send message
        timer = new Timer();
        sendMessage = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        TextView serO = (TextView) rootView.findViewById(R.id.serialOut);
                        data_ = 0;
                        data_ = data_ | (long)(xPower + 509) << 22;
                        data_ = data_ | (long)(yPower + 509) << 12;
                        data_ = data_ | (long)(bButton) << 11;
                        data_ = data_ | (long)(dButton) << 10;
                        data_ = data_ | (long)(aButton) << 9;
                        data_ = data_ | (long)(cButton) << 8;
                        data_ = data_ | (long)(fButton) << 6;
                        data_ = data_ | (long)(eButton) << 5;

                        serO.setText(String.valueOf(data_));

                        int f1 = (int) (data_ >> 24);
                        int s2 = (int) ((data_ & 0x00FF0000) >> 16);
                        int t3 = (int) ((data_ & 0x0000FF00) >> 8);
                        int f4 = (int) ((data_ & 0x000000FF));
                        int CS = 4;

                        CS^=f1;
                        CS^=s2;
                        CS^=t3;
                        CS^=f4;

                        try {
                            message = 0x06 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = 0x85 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = 0x04 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = f1 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = s2 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = t3 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = f4 ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }
                        try {
                            message = CS ;
                            outStream.write(message);
                        } catch (Exception e) {
                        }

                    }
                });
            }
        };

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
                        aButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        aButton = 0;
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
                        bButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        bButton = 0;
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
                        cButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        cButton = 0;
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
                        dButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        dButton = 0;
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
                        eButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        eButton = 0;
                        return true;
                }
                return false;
            }
        });

        // Set fButton = 1 when F is pressed
        Button f = (Button)rootView.findViewById(R.id.f);
        f.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fButton = 1;
                        return true;
                    case MotionEvent.ACTION_UP:
                        fButton = 0;
                        return true;
                }
                return false;
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
                        xcoord = 0;
                        ycoord = 0;
                        initX = 0;
                        initY = 0;
                        xPower = 0;
                        yPower = 0;
                        joy.setX(origX);
                        joy.setY(origY);
                        joy.setImageResource(R.mipmap.circlejoystick_s);
                        textView.setText("X: " + String.valueOf(xcoord) + " Y: " + String.valueOf(ycoord));
                        return true;
                }
                return false;
            }
        });
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
        Log.d("123",mAdapter.isEnabled() + "");
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

    public static void connect (String address, Context context) {
        BluetoothDevice device = null;
        try {
            device = mAdapter.getRemoteDevice(address);
        }catch (Exception e){
            Toast.makeText(context,"Invalid Address",Toast.LENGTH_LONG).show();
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
            timer.schedule(sendMessage, 0, 10);
            Toast.makeText(context, "Connection Established", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

        }
    }
}