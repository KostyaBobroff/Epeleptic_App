package com.example.costa.epeleptic_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import urlshortener.UrlShortener;
import urlshortener.UrlShorteners;

public class MainActivity extends Activity {

    private static final String TAG = "bluetooth2";
    String PhoneTextPreference ,bluetoothIdPreference;
    Handler h,Cheker;
    public static DBHelper dbHelper;
    final int RECIEVE_MESSAGE = 1; // Статус для Handler
    public static BluetoothAdapter btAdapter ;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    String address;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button epil;
//    @Override
//    protected void onStart(){
//        super.onStart();
//        getPrefs();
//    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        PhoneTextPreference = prefs.getString("PhoneNumberPref", "");
        bluetoothIdPreference = prefs.getString("bluetoothId","");
    }

    public void onLoadBtnClicked(View v) {
        Intent intent=new Intent(this,Statistic.class);
        startActivity(intent);
    }

    public void onCleanBtnClicked(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("statistic", null, null);
        dbHelper.close();
        Toast.makeText(
                getApplicationContext(),
                "Данные удалены из базы!",
                Toast.LENGTH_LONG
        ).show();
    }

    public void onEpilepticBtnClicked(View v){

//        class RetrieveShortUrl extends AsyncTask<String, Void, String> {
//
//            protected String doInBackground(String... urls) {
//                try {
//                    URI longUri = new URI(urls[0]);
//                    UrlShortener shortener = UrlShorteners.googleUrlShortener("AIzaSyCoLv1XiRLxNUYhxFwyjK7USK0kQHU1-DY");
//                    URI shortUrl = shortener.shorten(longUri);
//                    return shortUrl.toString();
//                } catch (Exception e) {}
//                return null;
//            }
//
//            protected void onPostExecute(String shortUrl) {
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(PhoneTextPreference, null, shortUrl, null, null);
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "SMS отправлено!",
//                            Toast.LENGTH_LONG
//                    ).show();
//                }
//                catch (Exception e) {
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "Не удалось отправить SMS!",
//                            Toast.LENGTH_LONG
//                    ).show();
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        double latitude = LocationService.mLastLocation.getLatitude();
//        double longitude = LocationService.mLastLocation.getLongitude();
//
//        String longUrl = String.format(
//                "http://maps.google.com/maps/?q=loc:%s,%s",
//                Double.valueOf(latitude).toString(),
//                Double.valueOf(longitude).toString()
//        );
//
//        RetrieveShortUrl task = new RetrieveShortUrl();
//        task.execute(longUrl);
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
//        String year = yearFormat.format(new Date(System.currentTimeMillis()));
//        String epilepticMsg = "Эпилептический приступ";
//        SimpleDateFormat monthFormat = new SimpleDateFormat("dd.MM");
//        String month = monthFormat.format(new Date(System.currentTimeMillis()));
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//        String time = timeFormat.format(new Date(System.currentTimeMillis()));
//
//        cv.put("time", time);
//        cv.put("month", month);
//        cv.put("year", year);
//        cv.put("epileptic", epilepticMsg);
//        cv.put("longitude", longitude);
//        cv.put("latitude", latitude);
//        db.insert("statistic", null, cv);
//
//        dbHelper.close();
        if ((CheckLocation(getApplicationContext())==true) && (isConnect(getApplicationContext())==true)) {
            sendGpsLocation();
        } else{
            Toast.makeText(getApplicationContext(),"Проверьте включен ли GPS и Интернет",Toast.LENGTH_LONG).show();

        }
    }

    public void onPrefBtnClicked(View v){
        Intent settingsActivity = new Intent(getBaseContext(),
                Preferences.class);
        startActivity(settingsActivity);
    }

    public void onBluetoothBtnClicked (View v){
        Intent intent = new Intent(this,BluetoothActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LocationService.init(this);
        getPrefs();
        epil = (Button)findViewById(R.id.epilepticBtn);
      //  address = BluetoothActivity.MAC;
        address = bluetoothIdPreference;
        dbHelper = new DBHelper(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                isConnect(getApplicationContext());
//                CheckLocation(getApplicationContext());
//            }
//        }).start();
//        Cheker = new Handler(){
//
//        };
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        isConnect(this);
        CheckLocation(this);
//
//        class ChekerAsynctask extends AsyncTask<Context, Boolean, Boolean> {
//
//            @Override
//            protected Boolean doInBackground(Context... params) {
//               // params[0].getSer
//                final ConnectivityManager conMgr = (ConnectivityManager) params[0].getSystemService(Context.CONNECTIVITY_SERVICE);
//                final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
//                if (activeNetwork != null && activeNetwork.isConnected()) {
//                    //  return true;
//                    publishProgress(true);
//
//                } else {
//                    publishProgress(false);
//                  //  return false;
//                    //  epil.setEnabled(false);
//                 //   Toast.makeText(ctx,"Передача данных выключена.Пожалуйста включите ее",Toast.LENGTH_LONG).show();
//                    // return false;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                super.onPostExecute(aBoolean);
//                if(aBoolean==true){
//                    epil.setEnabled(true);
//                }else{
//                    epil.setEnabled(false);
//                }
//            }
//        }
//        ChekerAsynctask  chek=new ChekerAsynctask();
//        chek.execute(this);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
              //  int i=0;
            switch (msg.what) {

                case RECIEVE_MESSAGE:
                    byte[] readBuf = (byte[]) msg.obj;
                    for (int i=0;i<=msg.arg1;i++) {
                        if (readBuf[i]==49) {
                            if ((CheckLocation(getApplicationContext())==true) &&(isConnect(getApplicationContext())==true)) {
                                sendGpsLocation();
                            } else{
                                Toast.makeText(getApplicationContext(),"Проверьте включен ли GPS и Интернет",Toast.LENGTH_LONG).show();
                                break;
                            }
//                            class RetrieveShortUrl extends AsyncTask<String, Void, String> {
//
//                                protected String doInBackground(String... urls) {
//                                    try {
//                                        URI longUri = new URI(urls[0]);
//                                        UrlShortener shortener = UrlShorteners.googleUrlShortener("AIzaSyCoLv1XiRLxNUYhxFwyjK7USK0kQHU1-DY");
//                                        URI shortUrl = shortener.shorten(longUri);
//                                        return shortUrl.toString();
//                                    } catch (Exception e) {}
//                                    return null;
//                                }
//
//                                protected void onPostExecute(String shortUrl) {
//                                    try {
//                                        SmsManager smsManager = SmsManager.getDefault();
//                                        smsManager.sendTextMessage(PhoneTextPreference, null, shortUrl, null, null);
//                                        Toast.makeText(
//                                                getApplicationContext(),
//                                                "SMS отправлено!",
//                                                Toast.LENGTH_LONG
//                                        ).show();
//                                    }
//                                    catch (Exception e) {
//                                        Toast.makeText(
//                                                getApplicationContext(),
//                                                "Не удалось отправить SMS!",
//                                                Toast.LENGTH_LONG
//                                        ).show();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//                            double latitude = LocationService.mLastLocation.getLatitude();
//                            double longitude = LocationService.mLastLocation.getLongitude();
//
//                            String longUrl = String.format(
//                                    "http://maps.google.com/maps/?q=loc:%s,%s",
//                                    Double.valueOf(latitude).toString(),
//                                    Double.valueOf(longitude).toString()
//                            );
//
//                            RetrieveShortUrl task = new RetrieveShortUrl();
//                            task.execute(longUrl);
//
//                            SQLiteDatabase db = dbHelper.getWritableDatabase();
//                            ContentValues cv = new ContentValues();
//                            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
//                            String year = yearFormat.format(new Date(System.currentTimeMillis()));
//                            String epilepticMsg = "Эпилептический приступ";
//                            SimpleDateFormat monthFormat = new SimpleDateFormat("dd.MM");
//                            String month = monthFormat.format(new Date(System.currentTimeMillis()));
//                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//                            String time = timeFormat.format(new Date(System.currentTimeMillis()));
//
//                            cv.put("time", time);
//                            cv.put("month", month);
//                            cv.put("year", year);
//                            cv.put("epileptic", epilepticMsg);
//                            cv.put("longitude", longitude);
//                            cv.put("latitude", latitude);
//                            db.insert("statistic", null, cv);
//
//                            dbHelper.close();
//                            String number = PhoneTextPreference;
//                            String Latitude = Double.valueOf(LocationService.mLastLocation.getLatitude()).toString();
//                            String Longitude = Double.valueOf(LocationService.mLastLocation.getLongitude()).toString();
//                            String sms = "https://maps.yandex.ru/" + Longitude + "," + Latitude;
//                            try {
//                                SmsManager sender = SmsManager.getDefault();
//                                sender.sendTextMessage(number, null, sms, null, null);
//                                Toast.makeText(
//                                        getApplicationContext(),
//                                        "SMS отправлено!",
//                                        Toast.LENGTH_LONG
//                                ).show();
//                            } catch (Exception e) {
//                                Toast.makeText(
//                                        getApplicationContext(),
//                                        "Не удалось отправить SMS!",
//                                        Toast.LENGTH_LONG
//                                ).show();
//                                e.printStackTrace();
//                           }
//                            SQLiteDatabase db = dbHelper.getWritableDatabase();
//                            ContentValues cv = new ContentValues();
//                            SimpleDateFormat yearFormat = new SimpleDateFormat(".yyyy");
//                            String year = yearFormat.format(new Date(System.currentTimeMillis()));
//                            String epilepticMsg = "Эпилептический приступ";
//                            SimpleDateFormat monthFormat = new SimpleDateFormat("dd.MM");
//                            String month = monthFormat.format(new Date(System.currentTimeMillis()));
//                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
//                            String time = timeFormat.format(new Date(System.currentTimeMillis()));
//
//                            cv.put("time", time);
//                            cv.put("month", month);
//                            cv.put("year", year);
//                            cv.put("epileptic", epilepticMsg);
//                            db.insert("statistic", null, cv);
//
//                            dbHelper.close();
                        }
                    }
                    break;
            }
        };

    };

        if (!Objects.equals(address, "") && (btAdapter.isEnabled())){
            Toast.makeText(this,"Connect",Toast.LENGTH_LONG).show();
            Connection();
        }

    }

    private void  Connection(){

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(
                    getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }

        btAdapter.cancelDiscovery();
        Log.d(TAG, "...Соединяемся...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(getApplicationContext(), e2.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }
    public  boolean isConnect(Context ctx) {

        final ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            Toast.makeText(ctx,"Передача данных включена",Toast.LENGTH_LONG).show();
           // epil.setEnabled(true);
            return true;

        } else {
    //        epil.setEnabled(false);
            Toast.makeText(ctx,"Передача данных выключена.Пожалуйста включите ее",Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public boolean CheckLocation(Context context){
        // location.isProviderEnabled()
        LocationManager location = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        if(location.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getApplicationContext(),"GPS включен",Toast.LENGTH_LONG).show();
            return  true;
        } else{
            Toast.makeText(getApplicationContext(),"GPS не включен.Пожалуйста включите GPS!",Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void sendGpsLocation(){
        class RetrieveShortUrl extends AsyncTask<String, Void, String> {

            protected String doInBackground(String... urls) {
                try {
                    URI longUri = new URI(urls[0]);
                    UrlShortener shortener = UrlShorteners.googleUrlShortener("AIzaSyCoLv1XiRLxNUYhxFwyjK7USK0kQHU1-DY");
                    URI shortUrl = shortener.shorten(longUri);
                    return shortUrl.toString();
                } catch (Exception e) {}
                return null;
            }

            protected void onPostExecute(String shortUrl) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(PhoneTextPreference, null, shortUrl, null, null);
                    Toast.makeText(
                            getApplicationContext(),
                            "SMS отправлено!",
                            Toast.LENGTH_LONG
                    ).show();
                }
                catch (Exception e) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Не удалось отправить SMS!",
                            Toast.LENGTH_LONG
                    ).show();
                    e.printStackTrace();
                }
            }
        }

        double latitude = LocationService.mLastLocation.getLatitude();
        double longitude = LocationService.mLastLocation.getLongitude();

        String longUrl = String.format(
                "http://maps.google.com/maps/?q=loc:%s,%s",
                Double.valueOf(latitude).toString(),
                Double.valueOf(longitude).toString()
        );

        RetrieveShortUrl task = new RetrieveShortUrl();
        task.execute(longUrl);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String year = yearFormat.format(new Date(System.currentTimeMillis()));
        String epilepticMsg = "Эпилептический приступ";
        SimpleDateFormat monthFormat = new SimpleDateFormat("dd.MM");
        String month = monthFormat.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(new Date(System.currentTimeMillis()));

        cv.put("time", time);
        cv.put("month", month);
        cv.put("year", year);
        cv.put("epileptic", epilepticMsg);
        cv.put("longitude", longitude);
        cv.put("latitude", latitude);
        db.insert("statistic", null, cv);

        dbHelper.close();
    }
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[256]; // buffer store for the stream
            int bytes; // bytes returned from read()

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget(); // Отправляем в очередь сообщений Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String message) {

            Log.d(TAG, "...Данные для отправки: " + message + "...");

            byte[] msgBuffer = message.getBytes();

            try {

                mmOutStream.write(msgBuffer);

            } catch (IOException e) {

                Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");

            }


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) { }
        }

   }

}








