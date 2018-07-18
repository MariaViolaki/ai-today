package com.example.android.aitoday;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class ArticlePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference articlesNumber = findPreference(getString(R.string.articles_number_key));
            updatePreferenceSummary(articlesNumber);
            Preference orderBy = findPreference(getString(R.string.articles_order_key));
            updatePreferenceSummary(orderBy);
        }

        // Get current preference value to display it as summary
        private void updatePreferenceSummary(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // Handle the ListPreference
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if (index >= 0) {
                    CharSequence[] entries = listPreference.getEntries();
                    preference.setSummary(entries[index]);
                }
            } else {
                // Handle the EditTextPreference
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}