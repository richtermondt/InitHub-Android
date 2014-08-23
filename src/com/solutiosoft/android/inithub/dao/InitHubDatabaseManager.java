/**
 *   This file is part of InitHub-Android.
 *
 *   InitHub-Android is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   InitHub-Android is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with InitHub-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.solutiosoft.android.inithub.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.solutiosoft.android.inithub.entities.Subject;
import com.solutiosoft.android.inithub.entities.User;

@SuppressWarnings("unused")
public class InitHubDatabaseManager extends SQLiteOpenHelper {
	private final static String TAG = "InitHubDatabaseManager";
	//private static InitHubDatabaseManager mInstance = null;
	//private Context mCxt;
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "InitHub";
    
    // Contacts table name
    private static final String TABLE_USER = "user";
    
    // user Table Columns names
    private static final String USER_COLUMN_ID = "id";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_API_KEY = "api_key";
    
    public static final String TABLE_SUBJECT = "subject";
    
    // subject Table Columns names
        
    public static final String SUBJECT_ID = "_id";
    public static final String SUBJECT_REMOTE_ID = "remote_id";
    public static final String SUBJECT_SHORT_DESC = "short_desc";
    public static final String SUBJECT_LONG_DESC = "long_desc";
    public static final String SUBJECT_INITIATIVE = "initiative_short_desc";
    public static final String SUBJECT_FIRST_NAME = "first_name";
    public static final String SUBJECT_LAST_NAME = "last_name";
    
    
    // message table
    public static final String TABLE_MESSAGE = "message";
    
    public static final String MESSAGE_ID = "_id";
    public static final String MESSAGE_REMOTE_ID = "remote_id";
    public static final String MESSAGE_SUBJECT_ID = "remote_subject_id";
    public static final String MESSAGE_COMMENT = "comment";
    public static final String MESSAGE_FIRST_NAME = "first_name";
    public static final String MESSAGE_LAST_NAME = "last_name";
    public static final String MESSAGE_CREATE_DATE = "create_date";
    
    /*
    public static InitHubDatabaseManager getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new InitHubDatabaseManager(ctx.getApplicationContext());
        }
        return mInstance;
    }
	*/
	public InitHubDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.mCxt = context;
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + USER_COLUMN_ID + " INTEGER PRIMARY KEY," + USER_COLUMN_EMAIL + " TEXT,"
                + USER_COLUMN_API_KEY + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
		String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + "("
				+ SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                + SUBJECT_REMOTE_ID + " INTEGER," + SUBJECT_SHORT_DESC + " TEXT,"
                + SUBJECT_LONG_DESC + " TEXT," + SUBJECT_INITIATIVE + " TEXT,"
                + SUBJECT_FIRST_NAME + " TEXT," + SUBJECT_LAST_NAME + " TEXT" + ")";
        db.execSQL(CREATE_SUBJECT_TABLE);
        
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "("
        		+ MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ MESSAGE_REMOTE_ID + " INTEGER," + MESSAGE_COMMENT + " TEXT,"
        		+ MESSAGE_FIRST_NAME + " TEXT," + MESSAGE_LAST_NAME + " TEXT,"
        		+ MESSAGE_CREATE_DATE + " DATE," + MESSAGE_SUBJECT_ID + " INTEGER)"; 
        db.execSQL(CREATE_MESSAGE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
 
        // Create tables again
        onCreate(db);
		
	}
	
	// Adding new contact
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_API_KEY, user.getApiKey()); 
 
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }
    
    public User getUser() {
    	User user = null;
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.getCount() == 1){
        	cursor.moveToFirst();
            user = new User();
            user.setID(Integer.parseInt(cursor.getString(0)));
            user.setEmail(cursor.getString(1));
            user.setApiKey(cursor.getString(2));
        } 
        
        cursor.close();
        db.close();
        
        return user;
    }
    
    // Updating single contact
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_API_KEY, user.getApiKey());
 
        // updating row
        int ret = db.update(TABLE_USER, values, USER_COLUMN_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
        
        db.close();
        
        return ret;
    }
 
    // Deleting single contact
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, USER_COLUMN_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
        db.close();
    }
 
    // Getting contacts Count
    public int getUserCount() {
    	int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        db.close();
 
        // return count
        return count;
    }
    
    public boolean subjectExists(int remote_id){
    	int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_SUBJECT + " WHERE " + SUBJECT_REMOTE_ID + " = " + remote_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        db.close();

        if(count > 0) {
        	return true;
        }
        else {
        	return false;
        }

    }
    
    public boolean messageExists(int remote_id){
    	int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGE + " WHERE " + MESSAGE_REMOTE_ID + " = " + remote_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        db.close();

        if(count > 0) {
        	return true;
        }
        else {
        	return false;
        }

    }
    
    public String getApiTokenCredentials(){
    	// set api auth
    	String returnVal = null;
        User user = getUser();
        if(user != null){
        	returnVal = "apikey " + user.getEmail() + ":" + user.getApiKey();
        }        
         
        return returnVal;
        
    }
    
    public ArrayList<Integer> getSubscribedSubjects(){
    	ArrayList<Integer> returnList = new ArrayList<Integer>();
        String countQuery = "SELECT  " + TABLE_SUBJECT + "." + SUBJECT_ID + " FROM " + TABLE_MESSAGE + ", " 
    	+ TABLE_SUBJECT + " WHERE "
    	+ TABLE_SUBJECT + "." + SUBJECT_REMOTE_ID + "="
    	+ TABLE_MESSAGE + "." + MESSAGE_SUBJECT_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        while (cursor.moveToNext()) {
        	returnList.add(cursor.getInt(0));
        }
        
        cursor.close();
		db.close();
		
    	return returnList;
    }
    
	public int getRemoteSubjectId(long subjectId){
		int rsId = 0;	   
		String countQuery = "SELECT " + SUBJECT_REMOTE_ID + " FROM " + TABLE_SUBJECT + " WHERE " + SUBJECT_ID + " = " + subjectId;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			rsId = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		
		return rsId;
		   
	}
	
	public Subject getSubjectByRemoteId(int remoteId){
		Subject subject = null;
        String selectQuery = "SELECT " + SUBJECT_SHORT_DESC 
        		+ ", " + SUBJECT_LONG_DESC
        		+ ", " + SUBJECT_FIRST_NAME 
        		+ ", " + SUBJECT_LAST_NAME 
        		+ " FROM " + TABLE_SUBJECT
        		+ " WHERE " + SUBJECT_REMOTE_ID + " = " + remoteId;
        
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if(cursor.getCount() == 1){
        	cursor.moveToFirst();
        	subject = new Subject();
        	subject.setShortDesc(cursor.getString(0));
        	subject.setLongDesc(cursor.getString(1));
        	subject.setFirstName(cursor.getString(2));
        	subject.setLastName(cursor.getString(3));
        	subject.setRemoteId(remoteId);
        } 
        
        cursor.close();
        db.close();
        
        return subject;
	}

	public void wipeAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_SUBJECT, null, null);
        db.delete(TABLE_MESSAGE, null, null);
        db.close();
		
	}
	
}
