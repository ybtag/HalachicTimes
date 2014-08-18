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

import java.util.List;

import android.content.Context;

/**
 * Location adapter for "favorite" locations.
 * 
 * @author Moshe Waisberg
 */
public class FavoritesLocationAdapter extends SpecificLocationAdapter {

	public FavoritesLocationAdapter(Context context, List<LocationItem> items) {
		super(context, items);
	}

	@Override
	protected boolean isSpecific(ZmanimAddress address) {
		return address.isFavorite();
	}
}
