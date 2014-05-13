package no.ntnu.ubinomad.lib.models;

import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.Users;
import no.ntnu.ubinomad.lib.exceptions.NoContextException;
import no.ntnu.ubinomad.lib.interfaces.AggregatorPlace;
import no.ntnu.ubinomad.lib.interfaces.Place;
import no.ntnu.ubinomad.lib.models.User;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class User extends AbstractBaseModel<User> {

	public User(Context context) {
		setContext(context);
		// TODO Auto-generated constructor stub
	}
	public User() {}

	private final Uri uri = UbiNomadContract.Users.CONTENT_URI;
	private final String[] PROJECTION_ALL = UbiNomadContract.Users.PROJECTION_ALL;

	
	private static final long serialVersionUID = 1285287602633231527L;
	private static final String TAG = "Users model";

	private String 			name;
	private String 			provider;
	private String 			email;
	private String 			password;
	private AggregatorPlace 	checkin;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public AggregatorPlace getCheckin() {
		return checkin;
	}
	
	private void setCheckin(long placeId) {
		AggregatorPlace place;
		place = new GenericAggregatorPlace(getContext()).getById(placeId);
		if (place != null){
			setCheckin(place);			
		}
	}

	public void setCheckin(AggregatorPlace checkin) {
		this.checkin = checkin;
	}
	public long getCheckInId() {
		if (checkin == null) return 0;

		return checkin.getId();
	}

	public User getByEmail(String email) throws RemoteException {
		if (getContext() == null) {
			throw new NoContextException("In order to connect to database a context is needed");
		}
		ContentProviderClient contentProvider = getContext().getContentResolver().acquireContentProviderClient(UbiNomadContract.CONTENT_URI);

		String[] projection = UbiNomadContract.Users.PROJECTION_ALL;

		Cursor cursor = contentProvider.query(Users.CONTENT_URI, projection, String.format("%s=\"%s\"", Users.KEY_EMAIL ,email) , null, null);

		if (cursor.moveToFirst()) {
			return fromCursor(cursor);
		}
		return null;
	}
	
	@Override
	protected User fromCursor(Cursor cursor) {
		User user = new User(getContext());
		user.setId(cursor.getLong(Users.ID_COL));
		user.setEmail(cursor.getString(Users.EMAIL_COL));
		user.setPassword(cursor.getString(Users.PASSWORD_COL));
		user.setName(cursor.getString(Users.NAME_COL));
		user.setProvider(cursor.getString(Users.PROVIDER_COL));
		// Find check-in place
		long placeId = cursor.getLong(Users.CHECKED_IN_COL);
		Log.d(TAG, "PlaceId: " + placeId);
		if (placeId > 0) {
			user.setCheckin(placeId);
		}
		return user;
	}
		
	@Override
	protected ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		if (getId() > 0){
			values.put(Users._ID, getId());
		}
		values.put(Users.KEY_EMAIL, email);
		values.put(Users.KEY_PASSWORD, password);
		values.put(Users.KEY_NAME, name);
		values.put(Users.KEY_PROVIDER, provider);
		if (checkin != null) values.put(Users.KEY_CHECKED_IN, checkin.getId());
		return values;
	}
	@Override
	protected Uri getUri() {
		return uri;
	}
	@Override
	protected String[] allProjection() {
		return PROJECTION_ALL;
	}
	
	
	
	public static User getMe(Context context){
		User u = new User(context).getById(1);
		if (u == null){
			u = new User(context);
			u.setEmail("test@test.com");
			u.setName("Tester");
			u.setPassword("");
			u.save();
		}
		return u;
	}

}
