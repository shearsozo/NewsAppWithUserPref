package tourguide.android.example.com.newsapplist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import static tourguide.android.example.com.newsapplist.MainActivity.ORDER_BY_PREFERENCE_KEY;
import static tourguide.android.example.com.newsapplist.MainActivity.ORDER_DATE_PREFERENCE_KEY;
import static tourguide.android.example.com.newsapplist.MainActivity.SECTIONNAME_PREFERENCE_KEY;

/**
 * Setting Activity helps narrow down the search results
 * When user chooses a search preference, the MainActivity news results are updated based on the user preference
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }


    public static class NewsAppPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.settings_main);
            String currentOrderByPreference = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ORDER_BY_PREFERENCE_KEY, getActivity().getIntent().getStringExtra("orderby"));

            PreferencesData preferenceData = getActivity().getIntent().getParcelableExtra(MainActivity.PREFERENCE_INFO);
            CharSequence[] sectionNameEntries = preferenceData.getSectionNames().toArray(new CharSequence[preferenceData.getSectionNames().size()]);
            CharSequence[] sectionNameValues = preferenceData.getSectionIds().toArray(new CharSequence[preferenceData.getSectionIds().size()]);

            handleSearchPreference(currentOrderByPreference, SECTIONNAME_PREFERENCE_KEY, sectionNameEntries, sectionNameValues);

            CharSequence[] entries = {"Relevance", "Newest"};
            CharSequence[] entryValues = {"relevance", "newest"};

            handleSearchPreference(currentOrderByPreference, "orderby_preference", entries, entryValues);
            String currentOrderDatePreference = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ORDER_DATE_PREFERENCE_KEY, getActivity().getIntent().getStringExtra("orderdate"));
            CharSequence[] entries2 = {"Published", "News Paper Edition", "Last Modified"};
            CharSequence[] entryValues2 = {"published", "newspaper-edition", "last-modified"};

            handleSearchPreference(currentOrderDatePreference, "orderdate_preference", entries2, entryValues2);

            Preference orderByPref = findPreference(getString(R.string.orderby_preference));
            bindPreferenceSummaryToValue(orderByPref);
            Preference orderDatePref = findPreference(getString(R.string.orderdate_preference));
            bindPreferenceSummaryToValue(orderDatePref);
            Preference sectionPref = findPreference(getString(R.string.section_preference));
            bindPreferenceSummaryToValue(sectionPref);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = ((ListPreference) preference).getEntry().toString();
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            CharSequence[] entries = listPreference.getEntries();

            if(index >= 0) {
                stringValue = entries[index].toString();
            }
            preference.setSummary(stringValue);
            return true;
        }

        /**
         * @param currentPreference - value of the current preference
         * @param preferenceKey     - key of the preference that needs to be updated
         * @param entries           - entries that are part of the preference list
         * @param entryValues       - corresponding entry values of the preference list
         */
        private void handleSearchPreference(String currentPreference, final String preferenceKey,
                                            CharSequence[] entries, CharSequence[] entryValues) {
            ListPreference sharedPreference = (ListPreference) findPreference(preferenceKey);


            sharedPreference.setEntries(entries);
            sharedPreference.setEntryValues(entryValues);

            sharedPreference.setDefaultValue(currentPreference);

            // When the user changes the preference update sharedpreferences and show parent activity
//            Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
//
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    String sortOrder = (String) newValue;
//                    SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(preferenceKey, MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(preferenceKey, sortOrder);
//                    editor.apply();
//                    finish();
//                    return true;
//                }
//            };
//            sharedPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
        }

    }
}
