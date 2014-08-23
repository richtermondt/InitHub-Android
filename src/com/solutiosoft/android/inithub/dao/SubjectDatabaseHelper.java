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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SubjectDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SubjectDatabaseHelper";
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "InitHub";
    
    // subject table name
    public static final String TABLE_SUBJECT = "subject";
    
    // subject Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_SHORT_DESC = "short_desc";
    public static final String KEY_LONG_DESC = "long_desc";
    public static final String KEY_INITIATIVE = "initiative_short_desc";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    
	public SubjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG,"------------ SubjectDatabaseHelper contructor");
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"------------ onCreate");
		String CREATE_SUBJECT_TABLE = "CREATE TABLE " + TABLE_SUBJECT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHORT_DESC + " TEXT,"
                + KEY_LONG_DESC + " TEXT," + KEY_INITIATIVE + "TEXT,"
                + KEY_FIRST_NAME + "TEXT," + KEY_LAST_NAME + "TEXT," + ")";
        db.execSQL(CREATE_SUBJECT_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
		 
        // Create tables again
        onCreate(db);
		
	}

}
