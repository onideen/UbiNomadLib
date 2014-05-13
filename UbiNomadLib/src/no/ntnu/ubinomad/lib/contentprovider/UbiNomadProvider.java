package no.ntnu.ubinomad.lib.contentprovider;

import java.util.List;

import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.AggregatorPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.RawPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.Users;
import no.ntnu.ubinomad.lib.BuildConfig;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;

public class UbiNomadProvider extends ContentProvider {

	private static final String TAG = "UbiNomadProvider";

	// Helper constants for use with the UriMatcher
	private static final int USER_LIST = 1;
	private static final int USER_ID = 2;
	private static final int AGGREGATOR_LIST = 5;
	private static final int AGGREGATOR_PLACE_ID = 6;
	private static final int RAW_PLACE_LIST = 7;
	private static final int RAW_PLACE_ID = 8;

	private static final UriMatcher URI_MATCHER;
	private UbiNomadOpenHelper mHelper = null;
	private ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();

	// Prepare the UriMatcher
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "users", USER_LIST);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "users/#", USER_ID);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "aggregator_places", AGGREGATOR_LIST);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "aggregator_places/#", AGGREGATOR_PLACE_ID);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "raw_places", RAW_PLACE_LIST);
		URI_MATCHER.addURI(UbiNomadContract.AUTHORITY, "raw_places/#", RAW_PLACE_ID);
	}

	@Override
	public boolean onCreate() {
		mHelper = new UbiNomadOpenHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case USER_LIST:
			return Users.CONTENT_TYPE;
		case USER_ID:
			return Users.CONTENT_USER_TYPE;
		case AGGREGATOR_LIST:
			return AggregatorPlaces.CONTENT_TYPE;
		case AGGREGATOR_PLACE_ID:
			return AggregatorPlaces.CONTENT_PLACE_TYPE;
		case RAW_PLACE_LIST:
			return RawPlaces.CONTENT_TYPE;
		case RAW_PLACE_ID:
			return RawPlaces.CONTENT_PLACE_TYPE;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		doAnalytics(uri, "insert");

		SQLiteDatabase db = mHelper.getWritableDatabase();
		long id;
		switch (URI_MATCHER.match(uri)) {
		case USER_LIST:
			id = db.insert(DbSchema.TABLE_USERS, null, values);
			return getUriForId(id, uri);
		case AGGREGATOR_LIST:
			id = db.insert(DbSchema.TABLE_AGGREGATOR_PLACES, null, values);
			return getUriForId(id, uri);
		case RAW_PLACE_LIST:
			id = db.insert(DbSchema.TABLE_RAW_PLACES, null, values);
			return getUriForId(id, uri);

			
		default:
			throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
		}
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String sortOrder) {
		doAnalytics(uri, "query");
		SQLiteDatabase db = mHelper.getWritableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		boolean useAuthorityUri = false;

		switch (URI_MATCHER.match(uri)) {
		case USER_LIST:
			builder.setTables(DbSchema.TABLE_USERS);
			if (TextUtils.isEmpty(sortOrder)){
				sortOrder = Users.SORT_ORDER_DEFAULT;
			}
			break;
		case USER_ID:
			builder.setTables(DbSchema.TABLE_USERS);
			// Limit query to one row at most
			builder.appendWhere(Users._ID + " = " + uri.getLastPathSegment());
			break;
		case AGGREGATOR_LIST:
			builder.setTables(DbSchema.TABLE_AGGREGATOR_PLACES);
			if (TextUtils.isEmpty(sortOrder)){
				sortOrder = AggregatorPlaces.SORT_ORDER_DEFAULT;
			}
			break;
		case AGGREGATOR_PLACE_ID:
			builder.setTables(DbSchema.TABLE_AGGREGATOR_PLACES);
			// Limit query to one row at most
			builder.appendWhere(AggregatorPlaces._ID + " = " + uri.getLastPathSegment());
			break;
		case RAW_PLACE_LIST:
			builder.setTables(DbSchema.TABLE_RAW_PLACES);
			if (TextUtils.isEmpty(sortOrder)){
				sortOrder = RawPlaces.SORT_ORDER_DEFAULT;
			}
			break;
		case RAW_PLACE_ID:
			builder.setTables(DbSchema.TABLE_RAW_PLACES);
			// Limit query to one row at most
			builder.appendWhere(RawPlaces._ID + " = " + uri.getLastPathSegment());
			break;
		
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		// Logging the query
		logQuery(builder,  projection, selection, sortOrder);

		Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		// If we want to be notified of any changes
		if (useAuthorityUri) {
			cursor.setNotificationUri(getContext().getContentResolver(), UbiNomadContract.CONTENT_URI);
		} else {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		doAnalytics(uri, "update");
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int updateCount = 0;
		String idStr;
		String where;
		switch (URI_MATCHER.match(uri)) {
		case USER_LIST:
			updateCount = db.update(DbSchema.TABLE_USERS, values, selection, selectionArgs);
			break;
		case USER_ID:
			idStr = uri.getLastPathSegment();
			where = Users._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			updateCount = db.update(DbSchema.TABLE_USERS, values, where, selectionArgs);
			break;
		case AGGREGATOR_LIST:
			updateCount = db.update(DbSchema.TABLE_AGGREGATOR_PLACES, values, selection, selectionArgs);
			break;
		case AGGREGATOR_PLACE_ID:
			idStr = uri.getLastPathSegment();
			where = AggregatorPlaces._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			updateCount = db.update(DbSchema.TABLE_AGGREGATOR_PLACES, values, where, selectionArgs);
			break;
		case RAW_PLACE_LIST:
			updateCount = db.update(DbSchema.TABLE_RAW_PLACES, values, selection, selectionArgs);
			break;
		case RAW_PLACE_ID:
			idStr = uri.getLastPathSegment();
			where = RawPlaces._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			updateCount = db.update(DbSchema.TABLE_RAW_PLACES, values, where, selectionArgs);
			break;
			
		default: 
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		// Notify all listeners of changes
		if (updateCount  > 0 && !isInBatchMode()) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return updateCount;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		doAnalytics(uri, "dekete");
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int deleteCount = 0;
		String idStr;
		String where;
		switch (URI_MATCHER.match(uri)) {
		case USER_LIST:
			deleteCount = db.delete(DbSchema.TABLE_USERS, selection, selectionArgs);
			break;
		case USER_ID:
			idStr = uri.getLastPathSegment();
			where = Users._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			deleteCount = db.delete(DbSchema.TABLE_USERS, where, selectionArgs);
			break;
		case AGGREGATOR_LIST:
			deleteCount = db.delete(DbSchema.TABLE_AGGREGATOR_PLACES, selection, selectionArgs);
			break;
		case AGGREGATOR_PLACE_ID:
			idStr = uri.getLastPathSegment();
			where = AggregatorPlaces._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			deleteCount = db.delete(DbSchema.TABLE_AGGREGATOR_PLACES, where, selectionArgs);
			break;
		case RAW_PLACE_LIST:
			deleteCount = db.delete(DbSchema.TABLE_RAW_PLACES, selection, selectionArgs);
			break;
		case RAW_PLACE_ID:
			idStr = uri.getLastPathSegment();
			where = RawPlaces._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection; 
			}
			deleteCount = db.delete(DbSchema.TABLE_RAW_PLACES, where, selectionArgs);
			break;
			
		default: 
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		// Notify all listeners of changes
		if (deleteCount  > 0 && !isInBatchMode()) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return deleteCount;
	}

	private Uri getUriForId(long id, Uri uri) {
		if (id > 0) {
			Uri itemUri = ContentUris.withAppendedId(uri, id);
			if (!isInBatchMode()) {
				// Notify all listeners of changes:
				getContext().getContentResolver().notifyChange(itemUri, null);
			}
			return itemUri;
		}
		else {
			// id <= 0, something went wrong
			throw new SQLException("Problem while inserting into uri: " + uri);
		}
	}


	private boolean isInBatchMode() {
		return mIsInBatchMode.get() != null && mIsInBatchMode.get();
	}

	/**
	 * I do not really use analytics, but if you export
	 * your content provider it makes sense to do so, to get
	 * a feeling for client usage. Especially if you want to
	 * _change_ something which might break existing clients,
	 * please check first if you can safely do so.
	 */
	private void doAnalytics(Uri uri, String event) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, event + " -> " + uri);
			Log.v(TAG, "caller: " + detectCaller());
		}
	}

	/** 
	 * You can use this for Analytics. 
	 * 
	 * Be aware though: This might be costly if many apps 
	 * are running.
	 */
	private String detectCaller() {
		// found here:nnew 
		// https://groups.google.com/forum/#!topic/android-developers/0HsvyTYZldA
		int pid = Binder.getCallingPid();
		return getProcessNameFromPid(pid);
	}

	/**
	 * Returns the name of the process the pid belongs to. Can be null if neither
	 * an Activity nor a Service could be found.
	 * @param givenPid
	 * @return
	 */
	private String getProcessNameFromPid(int givenPid) {
		ActivityManager am = (ActivityManager) getContext().getSystemService(Activity.ACTIVITY_SERVICE);
		if (am == null) return null;
		List<ActivityManager.RunningAppProcessInfo> lstAppInfo = am.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo ai : lstAppInfo) {
			if (ai.pid == givenPid) {
				return ai.processName;
			}
		}
		// added to take care of calling services as well:
		List<ActivityManager.RunningServiceInfo> srvInfo = am.getRunningServices(Integer.MAX_VALUE);
		for (ActivityManager.RunningServiceInfo si : srvInfo) {
			if (si.pid == givenPid) {
				return si.process;
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	private void logQuery(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "query: " + builder.buildQuery(projection, selection, null, null, sortOrder, null));
		}
	}

}