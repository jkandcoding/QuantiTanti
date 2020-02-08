package com.example.android.quantitanti.sharedpreferences;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.android.quantitanti.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_expenses, rootKey);
    }
}
