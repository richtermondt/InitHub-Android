package com.solutiosoft.android.inithub.ui;

import static com.solutiosoft.android.inithub.Constants.MESSAGE_ACTIVITY;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.solutiosoft.android.inithub.Constants;
import com.solutiosoft.android.inithub.R;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.provider.SubjectProvider;
import com.solutiosoft.android.inithub.service.SubjectIntentService;

public class SubjectActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "SubjectActivity";
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
		
	private View mSubjectListStatusView;
	private View mSubjectListView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		IntentFilter logoutIntentFilter = new IntentFilter();
		logoutIntentFilter.addAction(Constants.LOGOUT_INTENT_FILTER);
	    registerReceiver(mLogoutReceiver, logoutIntentFilter);	    
		
		setContentView(R.layout.subject_list);
		this.getListView().setDividerHeight(2);
		//Toast.makeText(SubjectActivity.this, "Loading...", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, SubjectIntentService.class);
	    startService(intent);
	    
		fillData();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_subject, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			Intent intent = new Intent(this, SubjectIntentService.class);
		    startService(intent);
		    break;
		case R.id.menu_settings:
			startActivity(new Intent(Constants.SETTING_ACTIVITY));
		    break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		 getListView().getItemAtPosition(position);
		//Toast.makeText(SubjectActivity.this, ".....id=" + id, Toast.LENGTH_LONG).show();
		Intent i = new Intent(MESSAGE_ACTIVITY);
		Bundle b = new Bundle();
		b.putLong(Constants.SUBJECT_ID, id);
		i.putExtras(b);
		//i.putExtra("subject_id", id);
		//finish();
		startActivity(i);
	}
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSubjectListStatusView.setVisibility(View.VISIBLE);
			mSubjectListStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSubjectListStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mSubjectListView.setVisibility(View.VISIBLE);
			mSubjectListView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSubjectListView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mSubjectListStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSubjectListView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	    String[] projection = { InitHubDatabaseManager.SUBJECT_ID, 
	    		InitHubDatabaseManager.SUBJECT_SHORT_DESC, 
	    		InitHubDatabaseManager.SUBJECT_INITIATIVE, 
	    		InitHubDatabaseManager.SUBJECT_LONG_DESC };
	    CursorLoader cursorLoader = new CursorLoader(this,
	    		SubjectProvider.CONTENT_URI, projection, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adapter.getCursor().close();
		unregisterReceiver(mLogoutReceiver);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		
	}
	
	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { InitHubDatabaseManager.SUBJECT_SHORT_DESC, InitHubDatabaseManager.SUBJECT_INITIATIVE, InitHubDatabaseManager.SUBJECT_LONG_DESC };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label, R.id.initiative, R.id.subject_long_desc };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.subject_row, null, from,
			to, 0);

		setListAdapter(adapter);
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
