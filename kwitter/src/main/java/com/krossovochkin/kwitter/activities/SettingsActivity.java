package com.krossovochkin.kwitter.activities;

import android.app.Activity;
import android.os.Bundle;

import com.krossovochkin.kwitter.R;
import com.krossovochkin.kwitter.fragments.SettingsFragment;

/**
 * Created by Vasya Drobushkov <vasya.drobushkov@gmail.com> on 13.08.14.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .commit();
    }
}
