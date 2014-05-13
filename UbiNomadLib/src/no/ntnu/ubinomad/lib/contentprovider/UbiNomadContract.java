package no.ntnu.ubinomad.lib.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * The contract between clients and the UbiNomad Content Provider
 * 
 * @author Vegar Engen
 */

public interface UbiNomadContract {


	/**
	 * The authority of the UbiNomad content provider.
	 */
	public static final String AUTHORITY = "no.ntnu.ubinomad";
	
	/**
	 * The content URI for the top-level UbiNomad authority.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	/**
	 * A selection clause for ID based queries.
	 */
	public static final String SELECTION_ID_BASED = BaseColumns._ID + " = ? ";
	
	/**
	 * Constants for the users table of the UbiNomad
	 */
	public static interface Users extends BaseColumns {
		/**
		 * The content URI for this table. 
		 */
		public static final Uri CONTENT_URI =  Uri.withAppendedPath(UbiNomadContract.CONTENT_URI, "users");
		
		/**
		 * The mime type of a directory of users.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/no.ntnu.ubinomad_users";
		
		/**
		 * The mime type of a single user.
		 */
		public static final String CONTENT_USER_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/no.ntnu.ubinomad_users";
		
		/**
		 * Define the name of the columns
		 */
		public static final String KEY_EMAIL 		= "email";
		public static final String KEY_PASSWORD 	= "password";
		public static final String KEY_NAME 		= "name";
		public static final String KEY_PROVIDER 	= "provider";
		public static final String KEY_CHECKED_IN 	= "checked_id";

		/**
		 * Define the column ordinal
		 */
		public static final int ID_COL	 		= 	0;
		public static final int EMAIL_COL		=	1;
		public static final int PASSWORD_COL	=	2;
		public static final int NAME_COL		=	3;
		public static final int PROVIDER_COL	=	4;
		public static final int CHECKED_IN_COL	=	5;
		
		
		
		/**
		 * A projection of all columns in the items table.
		 */
		public static final String[] PROJECTION_ALL = {_ID, KEY_EMAIL, KEY_PASSWORD, KEY_NAME, KEY_PROVIDER, KEY_CHECKED_IN};

		/**
		 * The default sort order for queries containing NAME fields.
		 */
		public static final String SORT_ORDER_DEFAULT = KEY_EMAIL + " ASC";
	}
	
	/**
	 * Constants for the places table of the UbiNomad
	 */
	public static interface AggregatorPlaces extends BaseColumns {
		/**
		 * The content URI for this table. 
		 */
		public static final Uri CONTENT_URI =  Uri.withAppendedPath(UbiNomadContract.CONTENT_URI, "aggregator_places");
		
		/**
		 * The mime type of a directory of places.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/no.ntnu.ubinomad_aggregator_places";
		
		/**
		 * The mime type of a single place.
		 */
		public static final String CONTENT_PLACE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/no.ntnu.ubinomad_aggregator_places";
			
		/**
		 * The name column names of this place
		 */
		public static final String KEY_NAME 		= "name";
		public static final String KEY_ICON_URL 	= "icon_url";
		public static final String KEY_LATITUDE 	= "latitude";
		public static final String KEY_LONGITUDE	= "longitude";

		/**
		 * Define the column ordinal
		 */
		public static final int _ID_COL 		= 	0;
		public static final int NAME_COL		=	1;
		public static final int ICON_URL_COL	=	2;
		public static final int LATITUDE_COL	=	3;
		public static final int LONGITUDE_COL	=	4;

		/**
		 * A projection of all columns in the items table.
		 */
		public static final String[] PROJECTION_ALL = {_ID, KEY_NAME, KEY_ICON_URL, KEY_LATITUDE, KEY_LONGITUDE};
		
		/**
		 * The default sort order for queries containing NAME fields.
		 */
		public static final String SORT_ORDER_DEFAULT = KEY_NAME + " ASC";


	}
	/**
	 * Constants for the provider places table of the UbiNomad
	 */
	public static interface RawPlaces extends BaseColumns {
		/**
		 * The content URI for this table. 
		 */
		public static final Uri CONTENT_URI =  Uri.withAppendedPath(UbiNomadContract.CONTENT_URI, "raw_places");
		
		/**
		 * The mime type of a directory of provider places.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/no.ntnu.ubinomad_raw_places";
		
		/**
		 * The mime type of a single provider place.
		 */
		public static final String CONTENT_PLACE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/no.ntnu.ubinomad_raw_places";
			
		/**
		 * The name column names of this provider place
		 */
		public static final String KEY_AGGREGATOR_PLACE_ID	= "place_id";
		public static final String KEY_PROVIDER 			= "provider";
		public static final String KEY_RAW_REFERENCE 		= "raw_reference";
		public static final String KEY_NAME 				= "name";
		public static final String KEY_LATITUDE 			= "latitude";
		public static final String KEY_LONGITUDE			= "longitude";
		public static final String KEY_ICON_URL 			= "icon_url";

		/**
		 * Define the column ordinal
		 */
		public static final int _ID_COL 				= 	0;
		public static final int AGGREGATOR_PLACE_ID_COL	=	1;
		public static final int PROVIDER_COL			=	2;
		public static final int RAW_REFERENCE_COL		=	3;
		public static final int NAME_COL				=	4;
		public static final int LATITUDE_COL			=	5;
		public static final int LONGITUDE_COL			=	6;
		public static final int ICON_URL_COL			=	7;
		
		/**
		 * A projection of all columns in the items table.
		 */
		public static final String[] PROJECTION_ALL = {_ID, KEY_AGGREGATOR_PLACE_ID, KEY_PROVIDER, KEY_RAW_REFERENCE, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE, KEY_ICON_URL};
		
		/**
		 * The default sort order for queries containing NAME fields.
		 */
		public static final String SORT_ORDER_DEFAULT = KEY_NAME + " ASC";


	}

}
