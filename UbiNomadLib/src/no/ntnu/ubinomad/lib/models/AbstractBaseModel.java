package no.ntnu.ubinomad.lib.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract;
import no.ntnu.ubinomad.lib.exceptions.NoContextException;
import no.ntnu.ubinomad.lib.interfaces.BaseModel;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;

public abstract class AbstractBaseModel<T> implements Serializable, BaseModel<T> {

	protected static final String TAG = "BaseModel";
	
	private long id;
	private Context context;
	
	@Override
	public long getId() {
		return id;
	}

	protected abstract Uri getUri();
	protected abstract String[] allProjection();

	protected abstract ContentValues toContentValues();
	
	protected abstract T fromCursor(Cursor cursor);
	
	protected void setId(long id) {
		this.id = id;
	}
	
	@Override
	public boolean save() {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentValues values = toContentValues();
		if (values.containsKey(BaseColumns._ID)){
			return update();
		}
		else {
			try {
				ContentProviderClient contentProvider = context.getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);
				Uri insertUri;
				insertUri = contentProvider.insert(getUri(), values);
				setId(Long.parseLong(insertUri.getLastPathSegment()));
				return true;
			} catch (RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
				return false;
			}
		}
	}
	
	@Override
	public boolean update() {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentValues values = toContentValues();
		
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);
		try {
			contentProvider.update(Uri.withAppendedPath(getUri(), values.getAsString(BaseColumns._ID)), values, null, null);
			return true;
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}		

	}

	@Override
	public T getById(long id){
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		String[] projection = allProjection();
		Uri uri = Uri.withAppendedPath(getUri(), String.valueOf(id));

		Cursor cursor;
		try {
			cursor = contentProvider.query(uri, projection, null, null, null);
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
		
		if (cursor.moveToFirst()) {
			return fromCursor(cursor);
		}
		
		return null;
	}

	@Override
	public boolean delete(){
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		String[] projection = allProjection();
		Uri uri = Uri.withAppendedPath(getUri(), String.valueOf(id));

		int numberOfDeleted = 0;
		try {
			numberOfDeleted = contentProvider.delete(uri, null, null);
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage(), e);
			return false;
		}
		
		if (numberOfDeleted == 1) {
			return true;
		}
		
		return false;

		
	}
	
	@Override
	public List<T> getAll() {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		List<T> ts = new ArrayList<T>();
		String[] projection = allProjection();
		Cursor cursor;
		try {
			cursor = contentProvider.query(getUri(), projection,null,null, null);
			if (cursor.moveToFirst()) {
				do {					
					T t = fromCursor(cursor);
					
					// Add to list 
					ts.add(t);
				}while(cursor.moveToNext());
			}	
			return ts;
		} catch (RemoteException e) {
			Log.e(TAG, "REMOTE ERROR", e);
			return null;
		}
	}
	
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
	
}