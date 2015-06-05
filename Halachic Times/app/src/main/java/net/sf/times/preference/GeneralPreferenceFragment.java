package net.sf.times.preference;

import android.annotation.TargetApi;
import android.os.Build;

import net.sf.times.R;

/**
 * This fragment shows the preferences for the General header.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends AbstractPreferenceFragment {

    @Override
    protected int getPreferencesXml() {
        return R.xml.general_preferences;
    }
}