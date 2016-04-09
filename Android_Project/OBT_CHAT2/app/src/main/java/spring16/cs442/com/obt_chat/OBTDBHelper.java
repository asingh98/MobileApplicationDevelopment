package spring16.cs442.com.obt_chat;

import android.content.Context;
		import android.database.sqlite.SQLiteDatabase;
		import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
		import android.util.Log;

public class OBTDBHelper extends SQLiteOpenHelper {
	public OBTDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		checkDataBase (name);
	}

	//function to check if database exists or not starts from here
	private boolean checkDataBase(String strDBName)
	{
		SQLiteDatabase checkDB = null;
		try
		{
			checkDB = SQLiteDatabase.openDatabase(strDBName, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteException e)
		{
			//database does't exist yet.
		}

		if(checkDB != null)
		{
			checkDB.close();
		}
		return checkDB != null;
	}
	//function to check if database exists or not ends over here

	@Override
	public void onCreate(SQLiteDatabase _db) {
		_db.execSQL(OTBDBAdapter.USER_MASTER_TABLE_CREATE);
		_db.execSQL(OTBDBAdapter.USER_LIST_TABLE_CREATE);
		_db.execSQL(OTBDBAdapter.USER_CONVERSATION_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
		Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to "
				+ _newVersion + ", which will destroy all old data");
		_db.execSQL ("DROP TABLE IF EXISTS " + "User_Master_Golden");
		_db.execSQL("DROP TABLE IF EXISTS " + "User_List");
		_db.execSQL("DROP TABLE IF EXISTS " + "User_Conversation");
		onCreate(_db);
	}

}