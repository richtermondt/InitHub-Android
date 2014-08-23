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

import static com.solutiosoft.android.inithub.Constants.LOGIN_ACTIVITY;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.solutiosoft.android.inithub.R;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.User;

public class InboxActivity extends Activity {
	
    private static final String TAG = "InboxActivity";
    
	/**
	 * Keep track of the logout task
	 */
    
	private UserLogoutTask mLogouthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_signout:
			mLogouthTask = new UserLogoutTask();
			mLogouthTask.execute((Void) null);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Represents an asynchronous logout.
	 */
	public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			InitHubDatabaseManager db = new InitHubDatabaseManager(getApplicationContext());
			User user = db.getUser();
			db.deleteUser(user);
			return true;	
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				finish();
				startActivity(new Intent(LOGIN_ACTIVITY));
			} else {
				Log.e(TAG, "Error deleting user record");
			}
		}


	}
	

}
