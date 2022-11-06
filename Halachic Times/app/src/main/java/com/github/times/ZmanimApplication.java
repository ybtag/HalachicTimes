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
package com.github.times;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.app.LocaleCallbacks;
import com.github.app.LocaleHelper;
import com.github.preference.LocalePreferences;
import com.github.preference.ThemePreferences;
import com.github.times.location.AddressProvider;
import com.github.times.location.LocationApplication;
import com.github.times.location.LocationsProviderFactory;
import com.github.times.location.ZmanimLocations;
import com.github.times.util.CrashlyticsTree;

import timber.log.Timber;

/**
 * Zmanim application.
 *
 * @author Moshe Waisberg
 */
public class ZmanimApplication extends LocationApplication<ThemePreferences, AddressProvider, ZmanimLocations> {

    private LocaleCallbacks<LocalePreferences> localeCallbacks;

    @Override
    protected void attachBaseContext(Context newBase) {
        this.localeCallbacks = new LocaleHelper<>(newBase);
        Context context = localeCallbacks.attachBaseContext(newBase);
        super.attachBaseContext(context);
    }

    @Override
    public void onPreCreate() {
        super.onPreCreate();
        localeCallbacks.onCreate(this);
        Timber.plant(new CrashlyticsTree(BuildConfig.DEBUG));
    }

    @NonNull
    @Override
    protected LocationsProviderFactory<AddressProvider, ZmanimLocations> createProviderFactory(Context context) {
        return new ZmanimProviderFactoryImpl(context);
    }
}
