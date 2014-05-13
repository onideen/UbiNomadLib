package no.ntnu.ubinomad.lib.contentprovider;

import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.AggregatorPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.RawPlaces;
import no.ntnu.ubinomad.lib.contentprovider.UbiNomadContract.Users;
import android.provider.BaseColumns;

/**
 * Constants used in creation of the database
 * 
 * @author Vegar Engen
 */

public interface DbSchema {

	static final int DB_VERSION = 12;
	static final String DB_NAME = "ubinomadprovider.db";

	static final String TABLE_USERS 			= "users";
	static final String TABLE_AGGREGATOR_PLACES = "aggregator_places";
	static final String TABLE_RAW_PLACES 		= "raw_places";
	
	
	// BE AWARE: Normally you would store the LOOKUP_KEY
	// of a contact from the device. But this would
	// have needless complicated the sample. Thus I
	// omitted it.
	
	static final String DDL_CREATE_TABLE_RAW_PLACE = "create table "
			+ TABLE_RAW_PLACES + "("
			+ RawPlaces._ID + " integer primary key autoincrement, "
			+ RawPlaces.KEY_AGGREGATOR_PLACE_ID + " integer , "
			+ RawPlaces.KEY_NAME + " text not null, "
			+ RawPlaces.KEY_PROVIDER + " text not null, "
			+ RawPlaces.KEY_RAW_REFERENCE + " text not null, "
			+ RawPlaces.KEY_LATITUDE + " double, "
			+ RawPlaces.KEY_LONGITUDE + " double, "
			+ RawPlaces.KEY_ICON_URL + " text, "
			+ String.format("UNIQUE (%s, %s) ON CONFLICT REPLACE", RawPlaces.KEY_PROVIDER, RawPlaces.KEY_RAW_REFERENCE)			
			+ ");";
	
	
	// Create places table
	static final String DDL_CREATE_TABLE_AGGREGATOR_PLACES = "create table "
			+ TABLE_AGGREGATOR_PLACES + "(" 
			+ AggregatorPlaces._ID + " integer primary key autoincrement, " 
			+ AggregatorPlaces.KEY_NAME + " text not null, "
			+ AggregatorPlaces.KEY_ICON_URL + " text, "
			+ RawPlaces.KEY_LATITUDE + " double, "
			+ RawPlaces.KEY_LONGITUDE + " double "
			+ ");";
	
	// Create places Users
	static final String DDL_CREATE_TABLE_USERS = "create table "
			+ TABLE_USERS + "(" 
			+ Users._ID + " integer primary key autoincrement, " 
			+ Users.KEY_NAME + " text not null,"
			+ Users.KEY_PROVIDER + " text, "
			+ Users.KEY_EMAIL + " text, "
			+ Users.KEY_PASSWORD + " text, "
			+ String.format("%s INTEGER REFERENCES %s(%s) ", Users.KEY_CHECKED_IN, TABLE_AGGREGATOR_PLACES, AggregatorPlaces._ID)
			+ ");";

	
	static final String DDL_DROP_TABLE_USERS =
			"DROP TABLE IF EXISTS " + TABLE_USERS;

	static final String DDL_DROP_TBL_PLACES =
			"DROP TABLE IF EXISTS " + TABLE_AGGREGATOR_PLACES;
	
	static final String DDL_DROP_TBL_PROVIDER_PLACES =
			"DROP TABLE IF EXISTS " + TABLE_RAW_PLACES;
	
	static final String DML_WHERE_ID_CLAUSE = BaseColumns._ID + " = ?";


}
