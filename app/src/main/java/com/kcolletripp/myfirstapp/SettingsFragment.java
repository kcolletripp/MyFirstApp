package com.kcolletripp.myfirstapp;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by kcolletripp on 18/03/16.
 */
public class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getView().setBackgroundColor(Color.WHITE);
            getView().setClickable(true);
        }

}
