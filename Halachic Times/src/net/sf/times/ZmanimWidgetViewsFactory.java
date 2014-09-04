package net.sf.times;

import net.sf.times.ZmanimAdapter.ZmanimItem;
import net.sf.times.location.ZmanimAddress;
import net.sf.times.location.ZmanimLocationListener;
import net.sf.times.location.ZmanimLocations;
import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.util.GeoLocation;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ZmanimWidgetViewsFactory implements RemoteViewsFactory, ZmanimLocationListener {

	/** The context. */
	private final Context mContext;
	/** Provider for locations. */
	private ZmanimLocations mLocations;
	/** The settings and preferences. */
	private ZmanimSettings mSettings;
	private ZmanimAdapter mAdapter;

	public ZmanimWidgetViewsFactory(Context context, Intent intent) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position);
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		ZmanimItem item = mAdapter.getItem(position);
		RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
		bindView(row, item);
		return row;
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		populateAdapter();
	}

	@Override
	public void onDestroy() {
		if (mLocations != null)
			mLocations.start(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		onDataSetChanged();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onAddressChanged(Location location, ZmanimAddress address) {
		onDataSetChanged();
	}

	@Override
	public void onElevationChanged(Location location) {
		onDataSetChanged();
	}

	private void populateAdapter() {
		Context context = mContext;

		if (mSettings == null)
			mSettings = new ZmanimSettings(context);
		if (mLocations == null) {
			ZmanimApplication app = (ZmanimApplication) context.getApplicationContext();
			mLocations = app.getLocations();
			mLocations.start(this);
		}
		GeoLocation gloc = mLocations.getGeoLocation();
		if (gloc == null)
			return;
		ComplexZmanimCalendar today = new ComplexZmanimCalendar(gloc);
		final boolean inIsrael = mLocations.inIsrael();

		ZmanimAdapter adapter = new ZmanimAdapter(context, mSettings, today, inIsrael);
		adapter.populate(false);
		mAdapter = adapter;
	}

	/**
	 * Bind the item to the remote view.
	 * 
	 * @param row
	 *            the remote list row.
	 * @param item
	 *            the zman item.
	 */
	private void bindView(RemoteViews row, ZmanimItem item) {
		row.setTextViewText(android.R.id.title, mContext.getText(item.titleId));
		row.setTextViewText(R.id.time, item.timeLabel);
		// if (item.elapsed) {
		// // Using {@code row.setBoolean(id, "setEnabled", enabled)} throws
		// error.
		// row.setTextColor(android.R.id.title, Color.DKGRAY);
		// row.setTextColor(R.id.time, Color.DKGRAY);
		// }
		// Enable clicking to open the main activity.
		row.setOnClickFillInIntent(R.id.widget_item, new Intent());
	}
}
