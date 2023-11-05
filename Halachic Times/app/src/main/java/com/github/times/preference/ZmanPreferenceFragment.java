/*
 * Copyright 2012, Moshe Waisberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.times.preference;

import static com.github.times.preference.RingtonePreference.PERMISSION_RINGTONE;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.core.content.PermissionChecker;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.TwoStatePreference;

import com.github.preference.SimplePreferences;
import com.github.times.R;
import com.github.times.remind.ZmanimReminder;
import com.github.times.remind.ZmanimReminderService;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment shows the preferences for a zman screen.
 */
@Keep
public class ZmanPreferenceFragment extends com.github.preference.AbstractPreferenceFragment {

    public static final String EXTRA_XML = "xml";
    public static final String EXTRA_OPINION = "opinion";
    public static final String EXTRA_REMINDER = "reminder";

    private static final int REQUEST_PERMISSIONS = 0x702E; // TONE

    @XmlRes
    private int xmlId = 0;
    private Preference preferenceReminderSunday;
    private Preference preferenceReminderMonday;
    private Preference preferenceReminderTuesday;
    private Preference preferenceReminderWednesday;
    private Preference preferenceReminderThursday;
    private Preference preferenceReminderFriday;
    private Preference preferenceReminderSaturday;
    private ZmanimPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleZmanimPreferences.init(requireContext());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        Bundle args = requireArguments();
        String opinionKey = args.getString(EXTRA_OPINION);
        String reminderKey = args.getString(EXTRA_REMINDER);

        initOpinion(opinionKey);
        initReminder(reminderKey);
    }

    @Override
    protected int getPreferencesXml() {
        if (xmlId == 0) {
            Bundle args = requireArguments();
            String xmlName = args.getString(EXTRA_XML);

            final Context context = requireContext();
            String pkgName = context.getPackageName();
            Resources res = context.getResources();
            this.xmlId = res.getIdentifier(xmlName, "xml", pkgName);
        }
        return xmlId;
    }

    protected ZmanimPreferences getPreferences() {
        ZmanimPreferences preferences = this.preferences;
        if (preferences == null) {
            final Context context = getContext();
            preferences = new SimpleZmanimPreferences(context);
            this.preferences = preferences;
        }
        return preferences;
    }

    private void initOpinion(String opinionKey) {
        if (!TextUtils.isEmpty(opinionKey)) {
            if (opinionKey.indexOf(';') > 0) {
                String[] tokens = opinionKey.split(";");
                for (String token : tokens) {
                    initOpinionPreference(token);
                }
            } else {
                initOpinionPreference(opinionKey);
            }
        }
    }

    private void initReminder(String reminderKey) {
        Preference preferenceReminder = initList(reminderKey);
        if (preferenceReminder == null) {
            preferenceReminder = initTime(reminderKey);
        }
        if (preferenceReminder != null) {
            final Preference.OnPreferenceChangeListener listener = preferenceReminder.getOnPreferenceChangeListener();
            preferenceReminder.setOnPreferenceChangeListener((preference, newValue) -> {
                final Context context = preference.getContext();
                requestReminderPermissions(context);
                remind(context);
                if (listener != null) return listener.onPreferenceChange(preference, newValue);
                return true;
            });
            initReminderDays(preferenceReminder);
        }
    }

    @Nullable
    private Preference initOpinionPreference(String key) {
        Preference preference;

        preference = initList(key);
        if (preference != null) {
            preference.setOnPreferenceChangeListener((pref, newValue) -> {
                maybeChooseMultipleOpinions(newValue);
                return true;
            });
            return preference;
        }

        preference = initRingtone(key);
        if (preference != null) {
            return preference;
        }

        preference = initTime(key);
        if (preference != null) {
            return preference;
        }

        return null;
    }

    protected void initReminderDays(Preference reminderTime) {
        String namePrefix = reminderTime.getKey();
        this.preferenceReminderSunday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_SUNDAY_SUFFIX);
        this.preferenceReminderMonday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_MONDAY_SUFFIX);
        this.preferenceReminderTuesday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_TUESDAY_SUFFIX);
        this.preferenceReminderWednesday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_WEDNESDAY_SUFFIX);
        this.preferenceReminderThursday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_THURSDAY_SUFFIX);
        this.preferenceReminderFriday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_FRIDAY_SUFFIX);
        this.preferenceReminderSaturday = initReminderDay(namePrefix + ZmanimPreferences.REMINDER_SATURDAY_SUFFIX);
    }

    @Nullable
    protected Preference initReminderDay(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        Preference preference = findPreference(key);
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
            return preference;
        }
        return null;
    }

    @Override
    protected boolean onCheckBoxPreferenceChange(TwoStatePreference preference, Object newValue) {
        if (preference.equals(preferenceReminderSunday)
            || preference.equals(preferenceReminderMonday)
            || preference.equals(preferenceReminderTuesday)
            || preference.equals(preferenceReminderWednesday)
            || preference.equals(preferenceReminderThursday)
            || preference.equals(preferenceReminderFriday)
            || preference.equals(preferenceReminderSaturday)) {
            remind(preference.getContext());
        }

        return super.onCheckBoxPreferenceChange(preference, newValue);
    }

    /**
     * Run the reminder service.
     * Tries to postpone the reminder until after any preferences have changed.
     */
    private void remind(Context context) {
        Intent intent = new Intent(ZmanimReminder.ACTION_UPDATE);
        ZmanimReminderService.enqueueWork(context, intent);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        if (this.preferences instanceof SimplePreferences) {
            return ((SimplePreferences) this.preferences).preferences;
        }
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void maybeChooseMultipleOpinions(Object newValue) {
        final String opinionBaalHatanya = ZmanimPreferences.Values.OPINION_BAAL_HATANYA;
        if (opinionBaalHatanya.equals(newValue)) {
            maybeChooseOpinionsBaalHatanya();
        }
    }

    private void maybeChooseOpinionsBaalHatanya() {
        final Context context = getContext();
        new AlertDialog.Builder(context)
            .setTitle(R.string.opinion_baal_hatanya)
            .setMessage(R.string.opinion_baal_hatanya_all)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chooseBaalHatanyaOpinions();
                }
            })
            .show();
    }

    /**
     * Select all relevant preferences to use Baal HaTanya's opinion.
     */
    private void chooseBaalHatanyaOpinions() {
        final Context context = getContext();
        final String opinion = ZmanimPreferences.Values.OPINION_BAAL_HATANYA;
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit()
            .putString(ZmanimPreferences.KEY_OPINION_HOUR, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_DAWN, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_TALLIS, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_SUNRISE, ZmanimPreferences.Values.OPINION_SEA)
            .putString(ZmanimPreferences.KEY_OPINION_SHEMA, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_TFILA, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_EAT, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_BURN, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_NOON, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_EARLIEST_MINCHA, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_MINCHA, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_PLUG_MINCHA, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_SUNSET, ZmanimPreferences.Values.OPINION_SEA)
            .putString(ZmanimPreferences.KEY_OPINION_NIGHTFALL, opinion)
            .putString(ZmanimPreferences.KEY_OPINION_SHABBATH_ENDS_AFTER, ZmanimPreferences.Values.OPINION_NIGHT)
            .putString(ZmanimPreferences.KEY_OPINION_SHABBATH_ENDS_NIGHTFALL, ZmanimPreferences.Values.OPINION_8_5)
            .putString(ZmanimPreferences.KEY_OPINION_GUARDS, ZmanimPreferences.Values.OPINION_3)
            .putInt(ZmanimPreferences.KEY_OPINION_CANDLES, 30)
            .putInt(ZmanimPreferences.KEY_OPINION_SHABBATH_ENDS_MINUTES, 0)
            .apply();
    }

    private void requestReminderPermissions(Context context) {
        List<String> permissions = new ArrayList<>();
        if (PermissionChecker.checkCallingOrSelfPermission(context, PERMISSION_RINGTONE) != PermissionChecker.PERMISSION_GRANTED) {
            permissions.add(PERMISSION_RINGTONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!nm.areNotificationsEnabled()) {
                permissions.add(ZmanimReminder.PERMISSION_NOTIFICATIONS);
            }
        }
        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
        }
    }
}
