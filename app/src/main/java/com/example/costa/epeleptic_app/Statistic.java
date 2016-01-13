package com.example.costa.epeleptic_app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Statistic extends Activity {

    ListView statisticView;
    ArrayAdapter adapter;
    ArrayList<Map<String, Double>> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.statistic);
        } catch(Exception e){
            Log.d("text", e.toString());
        }

        statisticView = (ListView) findViewById(R.id.statisticView);

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Cursor c = db.query("statistic", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int timeColIndex = c.getColumnIndex("time");
            int monthColIndex = c.getColumnIndex("month");
            int yearColIndex = c.getColumnIndex("year");
            int latitudeColIndex = c.getColumnIndex("latitude");
            int longitudeColIndex = c.getColumnIndex("longitude");

            String resString;
            locations = new ArrayList();
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
            do {
                String timeSql, monthSql, yearSql;
                timeSql = c.getString(timeColIndex);
                monthSql = c.getString(monthColIndex);
                yearSql = c.getString(yearColIndex);
                resString = timeSql +  " " + monthSql + " " + yearSql;

                double longitude = c.getDouble(longitudeColIndex);
                double latitude = c.getDouble(latitudeColIndex);
                Map<String, Double> l = new HashMap();
                l.put("longitude", longitude);
                l.put("latitude", latitude);
                locations.add(l);
                adapter.add(resString);
            } while (c.moveToNext());

            statisticView.setAdapter(adapter);

            statisticView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, Double> location = locations.get(position);
                    String uri = String.format(
                            "geo:%f,%f?q=%f,%f",
                            location.get("latitude"), location.get("longitude"),
                            location.get("latitude"), location.get("longitude")
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "Приступов не было обнаружено", Toast.LENGTH_LONG).show();
        }
        c.close();
        db.close();
    }

}
