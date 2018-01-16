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
package net.sf.times.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import net.sf.database.CursorFilter;
import net.sf.util.LocaleUtils;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.sf.times.location.AddressOpenHelper.TABLE_ADDRESSES;
import static net.sf.times.location.AddressOpenHelper.TABLE_CITIES;
import static net.sf.times.location.AddressOpenHelper.TABLE_ELEVATIONS;

/**
 * A class for handling geocoding and reverse geocoding. This geocoder uses the
 * Android SQLite database.
 *
 * @author Moshe Waisberg
 */
public class DatabaseGeocoder extends GeocoderBase {

    private static final String TAG = "DatabaseGeocoder";

    /** Database */
    private static final String DB_PROVIDER = "db";

    private static final String[] PROJECTION_ADDRESS = {
            BaseColumns._ID,
            AddressColumns.LOCATION_LATITUDE,
            AddressColumns.LOCATION_LONGITUDE,
            AddressColumns.LATITUDE,
            AddressColumns.LONGITUDE,
            AddressColumns.ADDRESS,
            AddressColumns.LANGUAGE,
            AddressColumns.FAVORITE
    };
    private static final int INDEX_ADDRESS_ID = 0;
    private static final int INDEX_ADDRESS_LOCATION_LATITUDE = 1;
    private static final int INDEX_ADDRESS_LOCATION_LONGITUDE = 2;
    private static final int INDEX_ADDRESS_LATITUDE = 3;
    private static final int INDEX_ADDRESS_LONGITUDE = 4;
    private static final int INDEX_ADDRESS_ADDRESS = 5;
    private static final int INDEX_ADDRESS_LANGUAGE = 6;
    private static final int INDEX_ADDRESS_FAVORITE = 7;

    private static final String[] PROJECTION_ELEVATION = {
            BaseColumns._ID,
            ElevationColumns.LATITUDE,
            ElevationColumns.LONGITUDE,
            ElevationColumns.ELEVATION,
            ElevationColumns.TIMESTAMP
    };
    private static final int INDEX_ELEVATION_ID = 0;
    private static final int INDEX_ELEVATION_LATITUDE = 1;
    private static final int INDEX_ELEVATION_LONGITUDE = 2;
    private static final int INDEX_ELEVATION_ELEVATION = 3;
    private static final int INDEX_ELEVATION_TIMESTAMP = 4;

    private static final String[] PROJECTION_CITY = {
            BaseColumns._ID,
            CitiesColumns.TIMESTAMP,
            CitiesColumns.FAVORITE};
    private static final int INDEX_CITY_ID = 0;
    private static final int INDEX_CITY_TIMESTAMP = 1;
    private static final int INDEX_CITY_FAVORITE = 2;

    private static final String WHERE_ID = BaseColumns._ID + "=?";

    private final Context context;
    private SQLiteOpenHelper dbHelper;

    /**
     * Creates a new database geocoder.
     *
     * @param context
     *         the context.
     */
    public DatabaseGeocoder(Context context) {
        this(context, LocaleUtils.getDefaultLocale(context));
    }

    /**
     * Creates a new database geocoder.
     *
     * @param context
     *         the context.
     * @param locale
     *         the locale.
     */
    public DatabaseGeocoder(Context context, Locale locale) {
        super(locale);
        this.context = context;
    }

    private SQLiteOpenHelper getDatabaseHelper() {
        if (dbHelper == null) {
            synchronized (this) {
                dbHelper = new AddressOpenHelper(context);
            }
        }
        return dbHelper;
    }

    /**
     * Get the readable addresses database.
     *
     * @return the database - {@code null} otherwise.
     */
    protected SQLiteDatabase getReadableDatabase() {
        try {
            return getDatabaseHelper().getReadableDatabase();
        } catch (SQLiteException e) {
            Log.e(TAG, "no readable db", e);
        }
        return null;
    }

    /**
     * Get the writable addresses database.
     *
     * @return the database - {@code null} otherwise.
     */
    protected SQLiteDatabase getWritableDatabase() {
        try {
            return getDatabaseHelper().getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e(TAG, "no writable db", e);
        }
        return null;
    }

    /** Close database resources. */
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }

    @Override
    public List<Address> getFromLocation(final double latitude, final double longitude, int maxResults) throws IOException {
        if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX)
            throw new IllegalArgumentException("latitude == " + latitude);
        if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX)
            throw new IllegalArgumentException("longitude == " + longitude);

        CursorFilter filter = new CursorFilter() {

            private final float[] mDistance = new float[1];

            @Override
            public boolean accept(Cursor cursor) {
                double locationLatitude = cursor.getDouble(INDEX_ADDRESS_LOCATION_LATITUDE);
                double locationLongitude = cursor.getDouble(INDEX_ADDRESS_LOCATION_LONGITUDE);
                Location.distanceBetween(latitude, longitude, locationLatitude, locationLongitude, mDistance);
                if (mDistance[0] <= SAME_LOCATION)
                    return true;

                double addressLatitude = cursor.getDouble(INDEX_ADDRESS_LATITUDE);
                double addressLongitude = cursor.getDouble(INDEX_ADDRESS_LONGITUDE);
                Location.distanceBetween(latitude, longitude, addressLatitude, addressLongitude, mDistance);
                return (mDistance[0] <= SAME_LOCATION);
            }
        };
        List<ZmanimAddress> q = queryAddresses(filter);
        List<Address> addresses = new ArrayList<Address>(q);

        return addresses;
    }

    @Override
    protected DefaultHandler createAddressResponseHandler(List<Address> results, int maxResults, Locale locale) {
        return null;
    }

    @Override
    public ZmanimLocation getElevation(final double latitude, final double longitude) throws IOException {
        if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX)
            throw new IllegalArgumentException("latitude == " + latitude);
        if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX)
            throw new IllegalArgumentException("longitude == " + longitude);

        CursorFilter filter = new CursorFilter() {
            private final float[] mDistance = new float[1];

            @Override
            public boolean accept(Cursor cursor) {
                double locationLatitude = cursor.getDouble(INDEX_ELEVATION_LATITUDE);
                double locationLongitude = cursor.getDouble(INDEX_ELEVATION_LONGITUDE);
                Location.distanceBetween(latitude, longitude, locationLatitude, locationLongitude, mDistance);
                return (mDistance[0] <= SAME_PLATEAU);
            }
        };
        List<ZmanimLocation> locations = queryElevations(filter);

        int locationsCount = locations.size();
        if (locationsCount == 0)
            return null;

        float distance;
        float[] distanceLoc = new float[1];
        double d;
        double distancesSum = 0;
        int n = 0;
        double[] distances = new double[locationsCount];
        double[] elevations = new double[locationsCount];

        for (ZmanimLocation loc : locations) {
            Location.distanceBetween(latitude, longitude, loc.getLatitude(), loc.getLongitude(), distanceLoc);
            distance = distanceLoc[0];
            elevations[n] = loc.getAltitude();
            d = distance * distance;
            distances[n] = d;
            distancesSum += d;
            n++;
        }

        if ((n == 1) && (distanceLoc[0] <= SAME_CITY))
            return locations.get(0);
        if (n <= 1)
            return null;

        double weightSum = 0;
        for (int i = 0; i < n; i++) {
            weightSum += (1 - (distances[i] / distancesSum)) * elevations[i];
        }

        ZmanimLocation elevated = new ZmanimLocation(DB_PROVIDER);
        elevated.setTime(System.currentTimeMillis());
        elevated.setLatitude(latitude);
        elevated.setLongitude(longitude);
        elevated.setAltitude(weightSum / (n - 1));
        elevated.setId(-1);
        return elevated;
    }

    @Override
    protected DefaultHandler createElevationResponseHandler(List<ZmanimLocation> results) {
        return null;
    }

    /**
     * Format the address.
     *
     * @param a
     *         the address.
     * @return the formatted address name.
     */
    protected static CharSequence formatAddress(ZmanimAddress a) {
        return a.getFormatted();
    }

    /**
     * Fetch addresses from the database.
     *
     * @param filter
     *         a cursor filter.
     * @return the list of addresses.
     */
    public List<ZmanimAddress> queryAddresses(CursorFilter filter) {
        final String language = locale.getLanguage();
        final String country = locale.getCountry();

        List<ZmanimAddress> addresses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if (db == null)
            return addresses;
        Cursor cursor = db.query(TABLE_ADDRESSES, PROJECTION_ADDRESS, null, null, null, null, null);
        if ((cursor == null) || cursor.isClosed()) {
            return addresses;
        }

        try {
            if (cursor.moveToFirst()) {
                String locationLanguage;
                Locale locale;
                ZmanimAddress address;

                do {
                    locationLanguage = cursor.getString(INDEX_ADDRESS_LANGUAGE);
                    if ((locationLanguage == null) || locationLanguage.equals(language)) {
                        if ((filter != null) && !filter.accept(cursor)) {
                            continue;
                        }

                        if (locationLanguage == null) {
                            locale = this.locale;
                        } else {
                            locale = new Locale(locationLanguage, country);
                        }

                        address = new ZmanimAddress(locale);
                        address.setFormatted(cursor.getString(INDEX_ADDRESS_ADDRESS));
                        address.setId(cursor.getLong(INDEX_ADDRESS_ID));
                        address.setLatitude(cursor.getDouble(INDEX_ADDRESS_LATITUDE));
                        address.setLongitude(cursor.getDouble(INDEX_ADDRESS_LONGITUDE));
                        address.setFavorite(cursor.getShort(INDEX_ADDRESS_FAVORITE) != 0);
                        addresses.add(address);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException se) {
            Log.e(TAG, "Query addresses: " + se.getLocalizedMessage(), se);
        } finally {
            cursor.close();
        }

        return addresses;
    }

    /**
     * Insert or update the address in the local database. The local database is
     * supposed to reduce redundant network requests.
     *
     * @param location
     *         the location.
     * @param address
     *         the address.
     */
    public void insertOrUpdateAddress(Location location, ZmanimAddress address) {
        if (address == null)
            return;
        long id = address.getId();
        if (id < 0L)
            return;
        // Cities have their own table.
        if (address instanceof City) {
            insertOrUpdateCity((City) address);
            return;
        }
        // Nothing to save.
        if (address instanceof Country) {
            return;
        }
        boolean insert = id == 0L;

        ContentValues values = new ContentValues();
        if (insert) {
            if (location == null) {
                values.put(AddressColumns.LOCATION_LATITUDE, address.getLatitude());
                values.put(AddressColumns.LOCATION_LONGITUDE, address.getLongitude());
            } else {
                values.put(AddressColumns.LOCATION_LATITUDE, location.getLatitude());
                values.put(AddressColumns.LOCATION_LONGITUDE, location.getLongitude());
            }
        }
        values.put(AddressColumns.ADDRESS, formatAddress(address).toString());
        values.put(AddressColumns.LANGUAGE, address.getLocale().getLanguage());
        values.put(AddressColumns.LATITUDE, address.getLatitude());
        values.put(AddressColumns.LONGITUDE, address.getLongitude());
        values.put(AddressColumns.TIMESTAMP, System.currentTimeMillis());
        values.put(AddressColumns.FAVORITE, address.isFavorite());

        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;
        if (insert) {
            id = db.insert(TABLE_ADDRESSES, null, values);
            if (id > 0L) {
                address.setId(id);
            }
        } else {
            String[] whereArgs = {Long.toString(id)};
            db.update(TABLE_ADDRESSES, values, WHERE_ID, whereArgs);
        }
    }

    /**
     * Delete the list of cached addresses.
     */
    public void deleteAddresses() {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;
        db.delete(TABLE_ADDRESSES, null, null);
    }

    /**
     * Fetch elevations from the database.
     *
     * @param filter
     *         a cursor filter.
     * @return the list of locations with elevations.
     */
    public List<ZmanimLocation> queryElevations(CursorFilter filter) {
        List<ZmanimLocation> locations = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if (db == null)
            return locations;
        Cursor cursor = db.query(TABLE_ELEVATIONS, PROJECTION_ELEVATION, null, null, null, null, null);
        if ((cursor == null) || cursor.isClosed()) {
            return locations;
        }

        try {
            if (cursor.moveToFirst()) {
                ZmanimLocation location;

                do {
                    if ((filter != null) && !filter.accept(cursor)) {
                        continue;
                    }

                    location = new ZmanimLocation(DB_PROVIDER);
                    location.setId(cursor.getLong(INDEX_ELEVATION_ID));
                    location.setLatitude(cursor.getDouble(INDEX_ELEVATION_LATITUDE));
                    location.setLongitude(cursor.getDouble(INDEX_ELEVATION_LONGITUDE));
                    location.setAltitude(cursor.getDouble(INDEX_ELEVATION_ELEVATION));
                    location.setTime(cursor.getLong(INDEX_ELEVATION_TIMESTAMP));
                    locations.add(location);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException se) {
            Log.e(TAG, "Query elevations: " + se.getLocalizedMessage(), se);
        } finally {
            cursor.close();
        }

        return locations;
    }

    /**
     * Insert or update the location with elevation in the local database. The
     * local database is supposed to reduce redundant network requests.
     *
     * @param location
     *         the location.
     */
    public void insertOrUpdateElevation(ZmanimLocation location) {
        if ((location == null) || !location.hasAltitude())
            return;
        long id = location.getId();
        if (id < 0L)
            return;

        ContentValues values = new ContentValues();
        values.put(ElevationColumns.LATITUDE, location.getLatitude());
        values.put(ElevationColumns.LONGITUDE, location.getLongitude());
        values.put(ElevationColumns.ELEVATION, location.getAltitude());
        values.put(ElevationColumns.TIMESTAMP, System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;
        if (id == 0L) {
            id = db.insert(TABLE_ELEVATIONS, null, values);
            if (id > 0L) {
                location.setId(id);
            }
        } else {
            String[] whereArgs = {Long.toString(id)};
            db.update(TABLE_ELEVATIONS, values, WHERE_ID, whereArgs);
        }
    }

    /**
     * Delete the list of cached elevations.
     */
    public void deleteElevations() {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;
        db.delete(TABLE_ELEVATIONS, null, null);
    }

    /**
     * Fetch cities from the database.
     *
     * @param filter
     *         a cursor filter.
     * @return the list of cities.
     */
    public List<City> queryCities(CursorFilter filter) {
        List<City> cities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if (db == null)
            return cities;
        Cursor cursor = db.query(TABLE_CITIES, PROJECTION_CITY, null, null, null, null, null);
        if ((cursor == null) || cursor.isClosed()) {
            return cities;
        }

        try {
            if (cursor.moveToFirst()) {
                City city;

                do {
                    if ((filter != null) && !filter.accept(cursor)) {
                        continue;
                    }

                    city = new City(locale);
                    city.setId(cursor.getLong(INDEX_CITY_ID));
                    city.setFavorite(cursor.getShort(INDEX_CITY_FAVORITE) != 0);
                    cities.add(city);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException se) {
            Log.e(TAG, "Query cities: " + se.getLocalizedMessage(), se);
        } finally {
            cursor.close();
        }

        return cities;
    }

    /**
     * Insert or update the city in the local database.
     *
     * @param city
     *         the city.
     */
    public void insertOrUpdateCity(City city) {
        if (city == null)
            return;

        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;

        ContentValues values = new ContentValues();
        values.put(CitiesColumns.TIMESTAMP, System.currentTimeMillis());
        values.put(CitiesColumns.FAVORITE, city.isFavorite());

        long id = city.getId();
        if (id == 0L) {
            id = City.generateCityId(city);

            values.put(BaseColumns._ID, id);
            id = db.insert(TABLE_CITIES, null, values);
            if (id > 0L) {
                city.setId(id);
            }
        } else {
            String[] whereArgs = {Long.toString(id)};
            db.update(TABLE_CITIES, values, WHERE_ID, whereArgs);
        }
    }

    /**
     * Delete the list of cached cities and re-populate.
     */
    public void deleteCities() {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            return;
        db.delete(TABLE_CITIES, null, null);
    }

}
