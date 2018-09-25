package tourguide.android.example.com.newsapplist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Setting Activity helps narrow down the search results
 * When user chooses a search preference, the MainActivity news results are updated based on the user preference
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        addPreferencesFromResource(R.xml.settings_main);
        String currentOrderByPreference = PreferenceManager.getDefaultSharedPreferences(this).getString("orderby_preference", getIntent().getStringExtra("orderby"));

        CharSequence[] entries = {"Relevance", "Newest"};
        CharSequence[] entryValues = {"relevance", "newest"};
        System.out.println("Current Preference Value: [" + currentOrderByPreference + "]");

        handleSearchPreference(currentOrderByPreference, "orderby_preference", entries, entryValues);

        String currentOrderDatePreference = PreferenceManager.getDefaultSharedPreferences(this).getString("orderdate_preference", getIntent().getStringExtra("orderdate"));

        CharSequence[] entries2 = {"Published", "News Paper Edition", "Last Modified"};
        CharSequence[] entryValues2 = {"published", "newspaper-edition", "last-modified"};

        handleSearchPreference(currentOrderDatePreference, "orderdate_preference", entries2, entryValues2);
    }

    /**
     *
     * @param currentPreference - value of the current preference
     * @param preferenceKey - key of the preference that needs to be updated
     * @param entries - entries that are part of the preference list
     * @param entryValues - corresponding entry values of the preference list
     */
    private void handleSearchPreference(String currentPreference, final String preferenceKey,
                                        CharSequence[] entries, CharSequence[] entryValues) {
        ListPreference sharedPreference = (ListPreference) findPreference(preferenceKey);


        sharedPreference.setEntries(entries);
        sharedPreference.setEntryValues(entryValues);

        sharedPreference.setDefaultValue(currentPreference);

        // When the user changes the preference update sharedpreferences and show parent activity
        Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String sortOrder = (String) newValue;
                SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(preferenceKey, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(preferenceKey, sortOrder);
                editor.apply();
                finish();
                return true;
            }
        };
        sharedPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
    }

    public static class NewsAppPreferenceFragment extends PreferenceFragment {

    }
}
