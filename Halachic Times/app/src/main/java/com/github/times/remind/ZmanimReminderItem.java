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
package com.github.times.remind;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.times.BuildConfig;
import com.github.times.ZmanimItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.github.times.ZmanimItem.NEVER;

/**
 * Reminder item for a notification.
 *
 * @author Moshe Waisberg
 */
public class ZmanimReminderItem {

    /**
     * Extras' name for the reminder id.
     */
    public static final String EXTRA_ID = BuildConfig.APPLICATION_ID + ".REMINDER_ID";
    /**
     * Extras' name for the reminder title.
     */
    public static final String EXTRA_TITLE = BuildConfig.APPLICATION_ID + ".REMINDER_TITLE";
    /**
     * Extras' name for the reminder text.
     */
    public static final String EXTRA_TEXT = BuildConfig.APPLICATION_ID + ".REMINDER_TEXT";
    /**
     * Extras' name for the reminder time.
     */
    public static final String EXTRA_TIME = BuildConfig.APPLICATION_ID + ".REMINDER_TIME";

    public final int id;
    public final CharSequence title;
    public final CharSequence text;
    public final long time;

    public ZmanimReminderItem(int id, @NonNull CharSequence title, CharSequence text, long time) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.time = time;
    }

    public boolean isEmpty() {
        return (id == 0) || (time == NEVER) || (title == null);
    }

    @Nullable
    public static ZmanimReminderItem from(@NonNull Context context, @Nullable ZmanimItem item) {
        if (item != null) {
            return new ZmanimReminderItem(item.titleId, context.getText(item.titleId), item.summary, item.time);
        }
        return null;
    }

    @Nullable
    public static ZmanimReminderItem from(@Nullable Bundle extras) {
        if (extras == null) {
            return null;
        }
        if (extras.containsKey(EXTRA_ID)) {
            int id = extras.getInt(EXTRA_ID);
            if (id == 0) {
                return null;
            }
            CharSequence contentTitle = extras.getCharSequence(EXTRA_TITLE);
            CharSequence contentText = extras.getCharSequence(EXTRA_TEXT);
            long when = extras.getLong(EXTRA_TIME, 0L);
            if ((contentTitle != null) && (when > 0L)) {
                return new ZmanimReminderItem(id, contentTitle, contentText, when);
            }
        }
        return null;
    }

    @Nullable
    public static ZmanimReminderItem from(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        return from(intent.getExtras());
    }

    public void put(@Nullable Bundle extras) {
        if (extras == null) {
            return;
        }
        extras.putInt(EXTRA_ID, id);
        extras.putCharSequence(EXTRA_TITLE, title);
        extras.putCharSequence(EXTRA_TEXT, text);
        extras.putLong(EXTRA_TIME, time);
    }

    public void put(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_TIME, time);
    }
}
