package com.example.costa.epeleptic_app;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Costa on 12.12.15.
 */
public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

