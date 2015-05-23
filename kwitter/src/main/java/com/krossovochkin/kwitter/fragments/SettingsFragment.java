package com.krossovochkin.kwitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.krossovochkin.kwitter.R;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 13.08.14.
 */
public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        initPreferences();
    }

    private void initPreferences() {
        if (getPreferenceManager() != null) {
            Preference prefLibs = getPreferenceManager().findPreference(getString(R.string.pref_libs_key));
            if (prefLibs != null) {
                prefLibs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showAboutLibsActivity();
                        return true;
                    }
                });
            } else {
                throw new IllegalStateException("no preference found");
            }
        }
    }

    private void showAboutLibsActivity() {
        //Create an intent with context and the Activity class
        Intent i = new Intent(getActivity(), LibsActivity.class);
        //Pass the fields of your application to the lib so it can find all external lib information
        i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
        //Define the libs you want (only those who don't include the information, and are managed by the AboutLibraries library) (OPTIONAL if all used libraries offer the information)
        i.putExtra(Libs.BUNDLE_LIBS, new String[]{
                "picasso",
                "androidviewpagerindicator"
        });

        //Display the library version (OPTIONAL)
        i.putExtra(Libs.BUNDLE_VERSION, true);
        //Display the library license (OPTIONAL
        i.putExtra(Libs.BUNDLE_LICENSE, true);

        //Pass your theme (OPTIONAL)
        i.putExtra(Libs.BUNDLE_THEME, R.style.AppTheme);

        startActivity(i);
    }
}
