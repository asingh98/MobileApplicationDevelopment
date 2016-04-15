package spring16.cs442.com.obt_chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OTBDBAdapter extends SQLiteOpenHelper {
	static String DATABASE_NAME = "otb";
	static int DATABASE_VERSION = 1;
	static final String USER_MASTER_TABLE_CREATE = "create table User_Master_Golden ( "
			//+ "User_Id" + " integer primary key autoincrement, "
			+ "User_Password text, "
			+ "User_Name text, "
			+ "User_Display_Name text primary key, "
			+ "User_Email_Id text, "
			+ "User_Last_Login integer, "
			+ "User_Profile_Status text,"
			+ "User_Photo text); ";
	static final String USER_LIST_TABLE_CREATE = "create table User_List ( "
			+ "Device_Id" + " text, "
			+ "User_Display_Name" + " integer, "
			+ "Chat_User_Display_Name text, "
			+ "Chat_User_Status text, "
			+ "Chat_User_Photo text, "
			+ "Chat_Last_Msg_Timestamp integer); ";
	static final String USER_CONVERSATION_TABLE_CREATE = "create table User_Conversation ( "
			+ "User_Display_Name" + " integer, "
			+ "Chat_User_Display_Name" + " text, "
			+ "MessageId" + " integer primary key autoincrement, "
			+ "Message" + " text, "
			+ "Sent_Received" + " text, "
			+ "Time_stamp" + " integer); ";
	public SQLiteDatabase db;
	//private final Context context;
	//private OBTDBHelper dbHelper;
	public OTBDBAdapter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		DATABASE_NAME = name;
		DATABASE_VERSION = version;
	}

	@Override
	public void onCreate(SQLiteDatabase _db) {
		_db.execSQL(OTBDBAdapter.USER_MASTER_TABLE_CREATE);
		_db.execSQL(OTBDBAdapter.USER_LIST_TABLE_CREATE);
		_db.execSQL(OTBDBAdapter.USER_CONVERSATION_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
		Log.w ("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to "
				+ _newVersion + ", which will destroy all old data");
		_db.execSQL ("DROP TABLE IF EXISTS " + "User_Master_Golden");
		_db.execSQL("DROP TABLE IF EXISTS " + "User_List");
		_db.execSQL("DROP TABLE IF EXISTS " + "User_Conversation");
		onCreate(_db);
	}

	public long insertUser(String strUserName, String strPassword, String strUserDisplayName, String strUserEmailID) {
		long lngRetValue;

		if (getUserDisplayNameCount(strUserDisplayName)<=0) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues newValues = new ContentValues();
			newValues.put("User_Password", strPassword);
			newValues.put("User_Name", strUserName);
			newValues.put("User_Display_Name", strUserDisplayName);
			newValues.put ("User_Email_Id", strUserEmailID);
			newValues.put ("User_Profile_Status", "Hey there!! I'm using OBT CHAT+");
			//newValues.put ("User_Profile_Photo", R.drawable.default_user_photo.);
			lngRetValue = db.insert ("User_Master_Golden", null, newValues);
			db.close();
		}
		else
		{
			lngRetValue  = -100;
		}

		return lngRetValue;
	}

	public long deleteUser(String strUserDisplayName) {
		long lngRetValue;
		SQLiteDatabase db = this.getWritableDatabase();

		String where = "User_Display_Name=?";

		lngRetValue = db.delete ("User_Master_Golden", where,
				new String[]{strUserDisplayName});
		db.close();
		return lngRetValue;
	}

	public UserMaster getUserDetails(String strUserDisplayName) {
		SQLiteDatabase db = this.getReadableDatabase ();
		Cursor cursor = db.query ("User_Master_Golden", null, "User_Display_Name=?",
				new String[]{strUserDisplayName}, null, null, null);
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return null;
		}
		cursor.moveToFirst ();
		UserMaster umCurrentUserMaster= new UserMaster ();
		//umCurrentUserMaster.intUserID = intUserID;
		umCurrentUserMaster.strPassword = cursor.getString(cursor.getColumnIndex("User_Password"));
		umCurrentUserMaster.strUserName = cursor.getString(cursor.getColumnIndex("User_Name"));
		umCurrentUserMaster.strUserDisplayName = cursor.getString(cursor.getColumnIndex("User_Display_Name"));
		umCurrentUserMaster.strUserEmailID = cursor.getString(cursor.getColumnIndex("User_email_Id"));
		umCurrentUserMaster.intUserLastLogin = cursor.getInt (cursor.getColumnIndex ("User_Last_Login"));
		umCurrentUserMaster.strPhoto = cursor.getString (cursor.getColumnIndex ("User_Profile_Photo"));
		umCurrentUserMaster.strProfileStatus = cursor.getString (cursor.getColumnIndex ("User_Profile_Status"));
		cursor.close();
		db.close();
		return umCurrentUserMaster;
	}

	public int getUserDisplayNameCount(String strUserDisplayName) {
		SQLiteDatabase db = this.getReadableDatabase ();
		Cursor cursor = db.query ("User_Master_Golden", null, "User_Display_Name=?",
				new String[]{strUserDisplayName}, null, null, null);
		int intCnt = cursor.getCount();
		cursor.close();
		db.close();
		return intCnt;
	}

	public int validateUserPassword(String strPassword, String strUserDisplayName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query ("User_Master_Golden", null, "User_Display_Name=?",
				new String[]{strUserDisplayName}, null, null, null);
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return -1;//User does not exist
		}
		cursor.moveToFirst ();
		if (cursor.getString(cursor.getColumnIndex("User_Password")).equals (strPassword))
		{
			db.close();
			return 0;
		}
		else
		{
			db.close();
			return -2;
		}
	}

	public long  updateUser(String strUserName,
							String strPassword, String strUserDisplayName,
							String strUserEmailID, String strPhoto, String strProfileStatus) {
		long lngRetValue;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("User_Password", strPassword);
		updatedValues.put("User_Name", strUserName);
		updatedValues.put ("User_Display_Name", strUserDisplayName);
		updatedValues.put ("User_Email_Id", strUserEmailID);
		updatedValues.put ("User_Profile_Status", strProfileStatus);
		updatedValues.put ("User_Profile_Photo", strPhoto);

		String where = "User_Display_Name = ?";
		lngRetValue = db.update ("User_Master_Golden", updatedValues, where, new String[]{strUserDisplayName});
		db.close();
		return lngRetValue;
	}

	public long updateUserLastLogin(String strUserDisplayName) {
		long lngRetValue;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put ("User_Last_Login", "datetime()");

		String where = "User_Display_Name = ?";
		lngRetValue = db.update("User_Master_Golden", updatedValues, where, new String[] { strUserDisplayName });
		db.close();
		return lngRetValue;
	}
}

class UserMaster
{
	//Integer intUserID;
	String strPassword;
	String strUserName;
	String strUserDisplayName;
	String strUserEmailID;
	int intUserLastLogin;
	String strProfileStatus;
	String strPhoto;
}