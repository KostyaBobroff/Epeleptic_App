package com.example.costa.epeleptic_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.UUID;


public class  BluetoothActivity extends Activity {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                adapter.add(bluetoothDevice.getAddress());
            }
        }
    };
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton toggleButton;
    private ListView BtAdapters;
    private ArrayAdapter adapter;
    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;
    public static String MAC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        toggleButton = (ToggleButton) findViewById(R.id.BtToggle);

        BtAdapters = (ListView) findViewById(R.id.BluetoothAdapters);

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        BtAdapters.setAdapter(adapter);
        bluetoothAdapter=MainActivity.btAdapter;
     //   bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
     //   MainActivity.btAdapter = bluetoothAdapter;
        BtAdapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView AdaptView = (TextView) itemClicked;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor ed=prefs.edit();
                MAC = AdaptView.getText().toString();
                ed.putString("bluetoothId",MAC);
                ed.apply();
                Intent BackToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(BackToMainActivity);
            }
        });
    }


    public void onToggleClicked(View view) {

        adapter.clear();

        ToggleButton toggleButton = (ToggleButton) view;

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "У вашего устройства нет Bluetooth",
                    Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
        } else {
            if (toggleButton.isChecked()){ //
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Ваше устройство уже было включено."
                           // +
                             //       "\n" + "Scanning for remote Bluetooth devices...",
                            ,Toast.LENGTH_SHORT).show();
                    discoverDevices();
                    makeDiscoverable();
                }
            } else {
                bluetoothAdapter.disable();
                adapter.clear();
                Toast.makeText(getApplicationContext(), "Теперь устройство отключено.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth включен...." +
                                "\n" + "Поиск устройств...",
                        Toast.LENGTH_SHORT).show();
                makeDiscoverable();
                discoverDevices();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth не включен.",
                        Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        } else if (requestCode == DISCOVERABLE_BT_REQUEST_CODE){

            if (resultCode == DISCOVERABLE_DURATION){
                Toast.makeText(getApplicationContext(), "Ваше устройство видимо в течениии  " +
                                DISCOVERABLE_DURATION + " секунд",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Не удается включить режим видимости на вашем устройстве.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void discoverDevices(){
        if (bluetoothAdapter.startDiscovery()) {
            Toast.makeText(getApplicationContext(), "Обнаружение других устройств...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Функцию обнаружение устройств не удалось запустить.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void makeDiscoverable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

}
