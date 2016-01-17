package com.example.costa.epeleptic_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import urlshortener.UrlShortener;
import urlshortener.UrlShorteners;

/**
 * Created by Costa on 17.01.16.
 */
public class SMSProvider {
    static void smsSend(final Context context, final String number){
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
                    smsManager.sendTextMessage(number, null, shortUrl, null, null);
                    Toast.makeText(
                            context,
                            "SMS отправлено!",
                            Toast.LENGTH_LONG
                    ).show();
                }
                catch (Exception e) {
                    Toast.makeText(context,
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

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
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

        MainActivity.dbHelper.close();
    }
}
