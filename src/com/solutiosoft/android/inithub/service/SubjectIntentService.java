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
package com.solutiosoft.android.inithub.service;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.solutiosoft.android.inithub.ApiHelper;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.Subject;
import com.solutiosoft.android.inithub.provider.SubjectProvider;

public class SubjectIntentService extends IntentService {
	private static final String TAG = "SubjectIntentService";

	public SubjectIntentService() {
		super("SubjectIntentService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ApiHelper api = ApiHelper.getInstance(this.getApplicationContext());
		ArrayList<Subject> list = api.getSubjectList();
		InitHubDatabaseManager db = new InitHubDatabaseManager(this.getApplicationContext());
		Log.d(TAG, "%%%%%%%%%% here 1");
		if(list != null){
			Log.d(TAG, "%%%%%%%%%% here 2");
			for(Subject subject : list){
				Log.d(TAG, "%%%%%%%%%% here 3");
				if(!db.subjectExists(subject.getRemoteId())){
					Log.d(TAG, "%%%%%%%%%% here 4");
					ContentValues values = new ContentValues();
				    values.put(InitHubDatabaseManager.SUBJECT_SHORT_DESC, subject.getShortDesc());
				    values.put(InitHubDatabaseManager.SUBJECT_LONG_DESC, subject.getLongDesc());
				    values.put(InitHubDatabaseManager.SUBJECT_FIRST_NAME, subject.getFirstName());
				    values.put(InitHubDatabaseManager.SUBJECT_LAST_NAME, subject.getLastName());
				    values.put(InitHubDatabaseManager.SUBJECT_INITIATIVE, subject.getInitiative());
				    values.put(InitHubDatabaseManager.SUBJECT_REMOTE_ID, subject.getRemoteId());
				    getContentResolver().insert(SubjectProvider.CONTENT_URI, values);
				}
			}
		}
		
	}
}
