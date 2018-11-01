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
package com.github.times.location;

import android.content.Context;

import com.github.times.location.impl.LocationsProviderFactoryImpl;
import com.github.util.LogUtils;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Default location application.
 *
 * @author Moshe Waisberg
 */
public class DefaultLocationApplication extends LocationApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new LogUtils.LogTree(BuildConfig.DEBUG));
    }

    @NonNull
    @Override
    protected LocationsProviderFactory createProviderFactory(Context context) {
        return new LocationsProviderFactoryImpl(context);
    }
}
