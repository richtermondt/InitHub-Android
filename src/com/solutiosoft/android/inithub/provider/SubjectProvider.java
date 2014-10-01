package com.solutiosoft.android.inithub.provider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;

public class SubjectProvider extends ContentProvider {
	private static final String TAG = "SubjectProvider";
	private InitHubDatabaseManager database;
	
	private static final int SUBJECTS = 10;
	private static final int SUBJECT_ID = 20;
	private static final String AUTHORITY = "com.solutiosoft.android.inithub";
	private static final String BASE_PATH = "subjects";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
  + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
      + "/subjects";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/subject";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, SUBJECTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SUBJECT_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case SUBJECTS:
	      id = sqlDB.insert(InitHubDatabaseManager.TABLE_SUBJECT, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    Uri itemUri = ContentUris.withAppendedId(uri, id);
	    getContext().getContentResolver().notifyChange(uri, null);
	    
	    return itemUri;

	}

	@Override
	public boolean onCreate() {
		database = new InitHubDatabaseManager(getContext());
	    return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(InitHubDatabaseManager.TABLE_SUBJECT);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
		  break;
		case SUBJECT_ID:
		  // Adding the ID to the original query
		  queryBuilder.appendWhere(InitHubDatabaseManager.SUBJECT_REMOTE_ID + "="
			  + uri.getLastPathSegment());
		  break;
		default:
		  throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
			selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	private void checkColumns(String[] projection) {
		String[] available = { InitHubDatabaseManager.SUBJECT_ID, InitHubDatabaseManager.SUBJECT_SHORT_DESC, 
				InitHubDatabaseManager.SUBJECT_LONG_DESC, InitHubDatabaseManager.SUBJECT_INITIATIVE, 
				InitHubDatabaseManager.SUBJECT_FIRST_NAME, InitHubDatabaseManager.SUBJECT_LAST_NAME};
		if (projection != null) {
		  HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		  HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
		  // Check if all columns which are requested are available
		  if (!availableColumns.containsAll(requestedColumns)) {
			throw new IllegalArgumentException("Unknown columns in projection");
		  }}
	}
}
