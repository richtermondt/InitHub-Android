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
package com.solutiosoft.android.inithub.ui;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.solutiosoft.android.inithub.Constants;
import com.solutiosoft.android.inithub.R;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.provider.MessageProvider;
import com.solutiosoft.android.inithub.service.MessageIntentService;

public class MessageActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MessageActivity";
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private long subject_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter logoutIntentFilter = new IntentFilter();
		logoutIntentFilter.addAction(Constants.LOGOUT_INTENT_FILTER);
	    registerReceiver(mLogoutReceiver, logoutIntentFilter);	
	    
		setContentView(R.layout.message_list);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		subject_id = b.getLong(Constants.SUBJECT_ID);
		Intent intent = new Intent(this, MessageIntentService.class);
		Bundle bundle = new Bundle();
		bundle.putLong(Constants.SUBJECT_ID, subject_id);
		intent.putExtras(bundle);
	    startService(intent);
	    fillData(subject_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_message, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_message_add:
			Intent add_intent = new Intent(Constants.MESSAGE_ADD_ACTIVITY);
			Bundle add_bundle = new Bundle();
			add_bundle.putLong(Constants.SUBJECT_ID, subject_id);
			add_intent.putExtras(add_bundle);
			//finish();
			startActivity(add_intent);
		    break;
		case R.id.menu_refresh:
			Toast.makeText(MessageActivity.this, R.string.message_refresh_message, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, MessageIntentService.class);
			Bundle bundle = new Bundle();
			bundle.putLong(Constants.SUBJECT_ID, subject_id);
			intent.putExtras(bundle);
		    startService(intent);
		    break;
		case R.id.menu_settings:
			Intent menu_intent = new Intent(Constants.SETTING_ACTIVITY);
			//finish();
			startActivity(menu_intent);
			break;
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
	    String[] projection = { InitHubDatabaseManager.MESSAGE_ID,
	    		InitHubDatabaseManager.MESSAGE_COMMENT,
	    		InitHubDatabaseManager.MESSAGE_FIRST_NAME,
	    		InitHubDatabaseManager.MESSAGE_LAST_NAME,
	    		InitHubDatabaseManager.MESSAGE_CREATE_DATE};
	    
	    Uri.Builder builder;
	    builder = new Builder();
	    builder = MessageProvider.CONTENT_URI.buildUpon().appendQueryParameter(Constants.SUBJECT_ID, bundle.getString(Constants.SUBJECT_ID));
	    Uri uri = builder.build();
	    CursorLoader cursorLoader = new CursorLoader(this,
	    		uri, projection, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		
	}
	
	private void fillData(long subjectId) {
		String subject_id = String.valueOf(subjectId);

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { InitHubDatabaseManager.MESSAGE_COMMENT, 
				InitHubDatabaseManager.MESSAGE_FIRST_NAME, 
				InitHubDatabaseManager.MESSAGE_LAST_NAME, 
				InitHubDatabaseManager.MESSAGE_CREATE_DATE };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.message_label,
				R.id.message_first_name,
				R.id.message_last_name,
				R.id.message_create_date};
		Bundle b = new Bundle();
		b.putString("subject_id", subject_id);
		getLoaderManager().initLoader(0, b, this);
		
		// TODO this needs to change to message ref
		adapter = new SimpleCursorAdapter(this, R.layout.message_row, null, from,
			to, 0);

		setListAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adapter.getCursor().close();
		unregisterReceiver(mLogoutReceiver);
	}
	
	private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  Log.d("onReceive","Logout in progress");
			    //At this point you should start the login activity and finish this one
			  finish();
		  }
		};

}
