package com.example.android.quantitanti.sharedpreferences;

import android.os.Bundle;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.android.quantitanti.R;

import java.util.Comparator;
import java.util.Currency;
import java.util.Set;
import java.util.TreeSet;

public class SettingsFragment extends PreferenceFragmentCompat {

    public Set<Currency> allAvailableCurrencies = new TreeSet<>(new SbufferComparator());

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_expenses, rootKey);

        DropDownPreference dropDownPreference = findPreference("currency");

        allAvailableCurrencies.addAll(Currency.getAvailableCurrencies());

        CharSequence[] entries = new CharSequence[allAvailableCurrencies.size()];
        CharSequence[] entryValues = new CharSequence[allAvailableCurrencies.size()];

        int i = 0;
        for (Currency currency : allAvailableCurrencies) {
            entries[i] = currency.getCurrencyCode() + " - " + currency.getDisplayName();
            entryValues[i] = currency.getCurrencyCode();
            i++;
        }

        assert dropDownPreference != null;
        dropDownPreference.setEntries(entries);
        dropDownPreference.setEntryValues(entryValues);
    }

    static class SbufferComparator implements Comparator<Currency> {

        @Override
        public int compare(Currency s1, Currency s2) {
            return s1.getCurrencyCode().compareTo(s2.getCurrencyCode());
        }
    }
}
