package no.ntnu.ubinomad.lib.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UbiNomadOpenHelper extends SQLiteOpenHelper {

	private static final String NAME = DbSchema.DB_NAME;
	private static final int VERSION = DbSchema.DB_VERSION;
	
	public UbiNomadOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbSchema.DDL_CREATE_TABLE_USERS);
		db.execSQL(DbSchema.DDL_CREATE_TABLE_AGGREGATOR_PLACES);
		db.execSQL(DbSchema.DDL_CREATE_TABLE_RAW_PLACE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbSchema.DDL_DROP_TABLE_USERS);
		db.execSQL(DbSchema.DDL_DROP_TBL_PLACES);
		db.execSQL(DbSchema.DDL_DROP_TBL_PROVIDER_PLACES);
		onCreate(db);		
	}

}
