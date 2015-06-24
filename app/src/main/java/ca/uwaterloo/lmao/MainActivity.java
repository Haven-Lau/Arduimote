package ca.uwaterloo.lmao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Comparator;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private Set<BluetoothDevice> pairedDevices = null;
    private ArrayAdapter<String> BTArrayAdapter = null;
    private ListView connectController = null;
    private Handler mHandler = new Handler();
    private Runnable immersiveView = new Runnable() {
        public void run() {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
    }


    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mHandler.post(immersiveView);
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
            // Clear BlueTooth list
            BTArrayAdapter.clear();

            // Get paired devices
            pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

            for (BluetoothDevice device : pairedDevices) {
                    BTArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
            }

            // Sort the ListView
            BTArrayAdapter.sort(new Comparator<String>() {
                public int compare(String object1, String object2) {
                    int res = String.CASE_INSENSITIVE_ORDER.compare(
                            object1.toString(), object2.toString());
                    return res;
                }
            });

            // Build an alert dialog
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Connect Controller");
            connectController = (ListView) convertView
                    .findViewById(R.id.listView1);
            connectController.setAdapter(BTArrayAdapter);
            final Dialog dialog = alertDialog.show();

            connectController.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // Get BlueTooth Address
                    String temp = (String) connectController.getAdapter().getItem(
                            position);
                    String address = temp.substring(temp.length() - 17,
                            temp.length());

                    // Connect Device
                    MainActivityFragment.connect(address,getApplicationContext());

                    // Exit Dialog
                    dialog.dismiss();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
