/*
 * Source file of the Halachic Times project.
 * Copyright (c) 2012. All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/2.0
 *
 * Contributors can be contacted by electronic mail via the project Web pages:
 * 
 * http://sourceforge.net/projects/halachictimes
 * 
 * http://halachictimes.sourceforge.net
 *
 * Contributor(s):
 *   Moshe Waisberg
 * 
 */
package net.sf.times.preference;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import net.sf.times.R;

import java.util.List;

/**
 * Application preferences that populate the settings.
 *
 * @author Moshe Waisberg
 */
public class ZmanimPreferenceActivity extends PreferenceActivity {

    private final String packageName;

    /**
     * Constructs a new preferences.
     */
    public ZmanimPreferenceActivity() {
        packageName = getClass().getPackage().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Zmanim_Settings);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.startsWith(packageName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    finish();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        // Recreate the parent activity in case a theme has changed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent parentIntent = getParentActivityIntent();
            if (parentIntent == null) {
                try {
                    PackageManager pm = getPackageManager();
                    ActivityInfo info = pm.getActivityInfo(getComponentName(), 0);
                    String parentActivity = info.parentActivityName;
                    parentIntent = new Intent();
                    parentIntent.setClassName(this, parentActivity);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(parentIntent);
        }
        super.finish();
    }
}
