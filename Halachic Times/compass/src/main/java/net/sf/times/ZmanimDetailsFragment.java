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
package net.sf.times;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import net.sf.times.ZmanimAdapter.ZmanimItem;
import net.sf.times.compass.R;
import net.sourceforge.zmanim.hebrewcalendar.JewishDate;

import java.util.Calendar;

/**
 * Shows a list of all opinions for a halachic time (<em>zman</em>).
 *
 * @author Moshe Waisberg
 */
public class ZmanimDetailsFragment<A extends ZmanimDetailsAdapter, P extends ZmanimDetailsPopulater<A>> extends ZmanimFragment<A, P> {

    /** The master id. */
    private int masterId;

    /**
     * Constructs a new details list.
     *
     * @param context
     *         the context.
     * @param attrs
     *         the XMl attributes.
     * @param defStyle
     *         the default style.
     */
    public ZmanimDetailsFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructs a new details list.
     *
     * @param context
     *         the context.
     * @param attrs
     *         the XML attributes.
     */
    public ZmanimDetailsFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructs a new details list.
     *
     * @param context
     *         the context.
     */
    public ZmanimDetailsFragment(Context context) {
        super(context);
    }

    /**
     * Get the master id for populating the details.
     *
     * @return the master id.
     */
    public int getMasterId() {
        return masterId;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected A createAdapter() {
        if (masterId == 0)
            return null;

        return (A) new ZmanimDetailsAdapter(context, settings);
    }

    @Override
    public A populateTimes(Calendar date) {
        return populateTimes(date, masterId);
    }

    /**
     * Populate the list with detailed times.
     *
     * @param date
     *         the date.
     * @param id
     *         the time id.
     */
    @SuppressWarnings("deprecation")
    public A populateTimes(Calendar date, int id) {
        masterId = id;

        P populater = getPopulater();
        A adapter = null;

        if (populater != null) {
            populater.setItemId(id);
            adapter = super.populateTimes(date);
        }

        Resources res = getResources();

        switch (settings.getTheme()) {
            case R.style.Theme_Zmanim_Dark:
                switch (id) {
                    case R.string.dawn:
                        list.setBackgroundColor(res.getColor(R.color.dawn));
                        break;
                    case R.string.tallis:
                    case R.string.tallis_only:
                        list.setBackgroundColor(res.getColor(R.color.tallis));
                        break;
                    case R.string.sunrise:
                        list.setBackgroundColor(res.getColor(R.color.sunrise));
                        break;
                    case R.string.shema:
                        list.setBackgroundColor(res.getColor(R.color.shema));
                        break;
                    case R.string.prayers:
                        list.setBackgroundColor(res.getColor(R.color.prayers));
                        break;
                    case R.string.midday:
                        list.setBackgroundColor(res.getColor(R.color.midday));
                        break;
                    case R.string.earliest_mincha:
                        list.setBackgroundColor(res.getColor(R.color.earliest_mincha));
                        break;
                    case R.string.mincha:
                        list.setBackgroundColor(res.getColor(R.color.mincha));
                        break;
                    case R.string.plug_hamincha:
                        list.setBackgroundColor(res.getColor(R.color.plug_hamincha));
                        break;
                    case R.string.sunset:
                        list.setBackgroundColor(res.getColor(R.color.sunset));
                        break;
                    case R.string.twilight:
                        list.setBackgroundColor(res.getColor(R.color.twilight));
                        break;
                    case R.string.nightfall:
                    case R.string.shabbath_ends:
                    case R.string.festival_ends:
                        list.setBackgroundColor(res.getColor(R.color.nightfall));
                        break;
                    case R.string.midnight:
                        list.setBackgroundColor(res.getColor(R.color.midnight));
                    default:
                        list.setBackgroundDrawable(null);
                        break;
                }
                break;
            case R.style.Theme_Zmanim_Light:
                switch (id) {
                    case R.string.dawn:
                        list.setBackgroundColor(res.getColor(R.color.dawn_solid));
                        break;
                    case R.string.tallis:
                    case R.string.tallis_only:
                        list.setBackgroundColor(res.getColor(R.color.tallis_solid));
                        break;
                    case R.string.sunrise:
                        list.setBackgroundColor(res.getColor(R.color.sunrise_solid));
                        break;
                    case R.string.shema:
                        list.setBackgroundColor(res.getColor(R.color.shema_solid));
                        break;
                    case R.string.prayers:
                        list.setBackgroundColor(res.getColor(R.color.prayers_solid));
                        break;
                    case R.string.midday:
                        list.setBackgroundColor(res.getColor(R.color.midday_solid));
                        break;
                    case R.string.earliest_mincha:
                        list.setBackgroundColor(res.getColor(R.color.earliest_mincha_solid));
                        break;
                    case R.string.mincha:
                        list.setBackgroundColor(res.getColor(R.color.mincha_solid));
                        break;
                    case R.string.plug_hamincha:
                        list.setBackgroundColor(res.getColor(R.color.plug_hamincha_solid));
                        break;
                    case R.string.sunset:
                        list.setBackgroundColor(res.getColor(R.color.sunset_solid));
                        break;
                    case R.string.twilight:
                        list.setBackgroundColor(res.getColor(R.color.twilight_solid));
                        break;
                    case R.string.nightfall:
                    case R.string.shabbath_ends:
                    case R.string.festival_ends:
                        list.setBackgroundColor(res.getColor(R.color.nightfall_solid));
                        break;
                    case R.string.midnight:
                        list.setBackgroundColor(res.getColor(R.color.midnight_solid));
                        break;
                    default:
                        list.setBackgroundDrawable(null);
                        break;
                }
                break;
            case R.style.Theme_Zmanim_NoGradient:
            default:
                list.setBackgroundDrawable(null);
                break;
        }

        return adapter;
    }

    @Override
    protected void setOnClickListener(View view, ZmanimItem item) {
        // No clicking allowed.
    }

    @Override
    protected void bindViews(ViewGroup list, A adapter) {
        if (list == null)
            return;
        list.removeAllViews();
        if (adapter == null)
            return;

        Context context = getContext();
        Calendar date = adapter.getCalendar().getCalendar();
        JewishDate jewishDate = new JewishDate(date);
        CharSequence dateHebrew;
        int jDayOfMonthPrevious = 0;
        int jDayOfMonth;

        final int count = adapter.getCount();
        int position = 0;
        ZmanimItem item;
        View row;

        while (position < count) {
            item = adapter.getItem(position);

            date.setTimeInMillis(item.time);
            jewishDate.setDate(date);
            jDayOfMonth = jewishDate.getJewishDayOfMonth();
            if (jDayOfMonth != jDayOfMonthPrevious) {
                dateHebrew = adapter.formatDate(context, jewishDate);
                bindViewGrouping(list, position, dateHebrew);
            }

            row = adapter.getView(position, null, list);
            bindView(list, position, row, item);
            position++;
            jDayOfMonthPrevious = jDayOfMonth;
        }
    }

    @Override
    protected void bindViewGrouping(ViewGroup list, int position, CharSequence label) {
//        if (position >= 0)
//            return;
        super.bindViewGrouping(list, position, label);
    }

    @Override
    protected P createPopulater() {
        return (P) new ZmanimDetailsPopulater<A>(context, settings);
    }
}