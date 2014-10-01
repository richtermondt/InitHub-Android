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
import android.text.TextUtils;

import com.solutiosoft.android.inithub.Constants;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;

public class MessageProvider extends ContentProvider {
	
	private static final String TAG = "MessageProvider";
	private InitHubDatabaseManager database;
	
	private static final int MESSAGES = 10;
	private static final int MESSAGE_ID = 20;
	private static final String AUTHORITY = "com.solutiosoft.android.inithub.messages";
	private static final String BASE_PATH = "messages";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
  + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
      + "/messages";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/message";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, MESSAGES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", MESSAGE_ID);
	}
	
	public MessageProvider() {
	}

	@Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
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
	public synchronized Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case MESSAGES:
	      id = sqlDB.insert(InitHubDatabaseManager.TABLE_MESSAGE, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    Uri itemUri = ContentUris.withAppendedId(uri, id);
	    getContext().getContentResolver().notifyChange(uri, null);
	    //return Uri.parse(BASE_PATH + "/" + id);
	    return itemUri;

	}

	@Override
	public boolean onCreate() {
		//database = InitHubDatabaseManager.getInstance(getContext());
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
		queryBuilder.setTables(InitHubDatabaseManager.TABLE_MESSAGE);
		
		//Log.d(TAG, "--------------- URI=" + uri);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case MESSAGES:
			int subId  = database.getRemoteSubjectId(Long.parseLong(uri.getQueryParameter(Constants.SUBJECT_ID)));
			//Log.d(TAG, "$$$$$$$$$$$$$$ subId=" + subId);
			queryBuilder.appendWhere(InitHubDatabaseManager.MESSAGE_SUBJECT_ID + "="
					+ subId);
		  break;
		case MESSAGE_ID:
		  // Adding the ID to the original query
			queryBuilder.appendWhere(InitHubDatabaseManager.MESSAGE_REMOTE_ID + "="
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
	public synchronized int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		//throw new UnsupportedOperationException("Not yet implemented");

		   	int uriType = sURIMatcher.match(uri);
		    SQLiteDatabase sqlDB = database.getWritableDatabase();
		    int rowsUpdated = 0;
		    switch (uriType) {
		    case MESSAGES:
		      rowsUpdated = sqlDB.update(InitHubDatabaseManager.TABLE_MESSAGE, 
		          values, 
		          selection,
		          selectionArgs);
		      break;
		    case MESSAGE_ID:
		      String id = uri.getLastPathSegment();
		      if (TextUtils.isEmpty(selection)) {
		        rowsUpdated = sqlDB.update(InitHubDatabaseManager.TABLE_MESSAGE, 
		            values,
		            InitHubDatabaseManager.MESSAGE_ID + "=" + id, 
		            null);
		      } else {
		        rowsUpdated = sqlDB.update(InitHubDatabaseManager.TABLE_MESSAGE, 
		            values,
		            InitHubDatabaseManager.MESSAGE_ID + "=" + id 
		            + " and " 
		            + selection,
		            selectionArgs);
		      }
		      break;
		    default:
		      throw new IllegalArgumentException("Unknown URI: " + uri);
		    }
		    getContext().getContentResolver().notifyChange(uri, null);
		    return rowsUpdated;
		
	}
	
	private void checkColumns(String[] projection) {
		String[] available = { InitHubDatabaseManager.MESSAGE_ID, InitHubDatabaseManager.MESSAGE_COMMENT, 
				InitHubDatabaseManager.MESSAGE_FIRST_NAME, InitHubDatabaseManager.MESSAGE_LAST_NAME, 
				InitHubDatabaseManager.MESSAGE_SUBJECT_ID, InitHubDatabaseManager.MESSAGE_CREATE_DATE, 
				InitHubDatabaseManager.MESSAGE_REMOTE_ID};
		if (projection != null) {
		  HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		  HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
		  // Check if all columns which are requested are available
		  if (!availableColumns.containsAll(requestedColumns)) {
			throw new IllegalArgumentException("Unknown columns in projection");
		  }}
	}
}
