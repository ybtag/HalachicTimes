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
package net.sf.times.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.format.DateUtils;

/**
 * A helper class to manage database creation and version management for
 * addresses.
 * 
 * @author Moshe Waisberg
 */
public class AddressOpenHelper extends SQLiteOpenHelper {

	/** Database name for times. */
	private static final String DB_NAME = "times";
	/** Database version. */
	private static final int DB_VERSION = 2;
	/** Database table for addresses. */
	public static final String TABLE_ADDRESSES = "addresses";

	/**
	 * Constructs a new helper.
	 * 
	 * @param context
	 *            the context.
	 */
	public AddressOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ").append(TABLE_ADDRESSES).append('(');
		sql.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sql.append(AddressColumns.LOCATION_LATITUDE).append(" DOUBLE NOT NULL,");
		sql.append(AddressColumns.LOCATION_LONGITUDE).append(" DOUBLE NOT NULL,");
		sql.append(AddressColumns.LATITUDE).append(" DOUBLE NOT NULL,");
		sql.append(AddressColumns.LONGITUDE).append(" DOUBLE NOT NULL,");
		sql.append(AddressColumns.ADDRESS).append(" TEXT NOT NULL,");
		sql.append(AddressColumns.LANGUAGE).append(" TEXT,");
		sql.append(AddressColumns.TIMESTAMP).append(" INTEGER");
		sql.append(");");
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_ADDRESSES + ";");
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);

		// Delete all records older than 1 year.
		String whereClause = "(" + AddressColumns.TIMESTAMP + " IS NULL) OR (" + AddressColumns.TIMESTAMP + " < " + (System.currentTimeMillis() - DateUtils.YEAR_IN_MILLIS) + ")";
		String[] whereArgs = null;
		db.delete(TABLE_ADDRESSES, whereClause, whereArgs);
	}
}
