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

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.solutiosoft.android.inithub.ApiHelper;
import com.solutiosoft.android.inithub.Constants;
import com.solutiosoft.android.inithub.Utils;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.Message;
import com.solutiosoft.android.inithub.entities.Subject;
import com.solutiosoft.android.inithub.provider.MessageProvider;
import com.solutiosoft.android.inithub.ui.MessageActivity;


public class MessageIntentService extends IntentService {
	private static final String TAG = "MessageIntentService";
	
	public MessageIntentService() {
		super("MessageIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "+++++++++++++ onHandleIntent");
		
		Bundle bundle = intent.getExtras();
		int action = bundle.getInt(Constants.MESSAGE_SERVICE_ACTION);
		
		// process subject arraylist action
		if(action == Constants.MESSAGE_ACTION_PROCESS_SUBJECTS){
			ArrayList<Integer> subjects = bundle.getIntegerArrayList(Constants.MESSAGE_SERVICE_SUBJECTS);
			
			for(Integer subjectId: subjects){
				processSubjectMessages(subjectId, true);
			
			}		    
			
		}
		// default action
		else {
			long subjectId = bundle.getLong(Constants.SUBJECT_ID);
			processSubjectMessages(subjectId, false);

		}	
		
	}
	
	private void processSubjectMessages(long subjectId, boolean generateNotification){
		int newMsgs = 0;
		Log.d(TAG, "+++++++++++++ subject_id=" + subjectId);
		InitHubDatabaseManager db = new InitHubDatabaseManager(this.getApplicationContext());
		int remoteSubjectId = db.getRemoteSubjectId(subjectId);
		ApiHelper api = ApiHelper.getInstance(this.getApplicationContext());
		ArrayList<Message> list = api.getMessageList(remoteSubjectId);
		Log.d(TAG, "+++++++++++++ remote_subject_id=" + remoteSubjectId);
		if(list != null){
			for(Message message : list){
				if(!db.messageExists(message.getRemoteId())){
					newMsgs++;
					String creat_date = Utils.formatCreateDate(message.getCreateDate(), "yyyy-MM-dd'T'HH:mm:ss");
					ContentValues values = new ContentValues();
					values.put(InitHubDatabaseManager.MESSAGE_COMMENT, message.getComment());
				    values.put(InitHubDatabaseManager.MESSAGE_REMOTE_ID, message.getRemoteId());
				    values.put(InitHubDatabaseManager.MESSAGE_FIRST_NAME, message.getFirstName());
				    values.put(InitHubDatabaseManager.MESSAGE_LAST_NAME, message.getLastName());
				    values.put(InitHubDatabaseManager.MESSAGE_SUBJECT_ID, message.getRemoteSubjectId());
				    values.put(InitHubDatabaseManager.MESSAGE_CREATE_DATE, creat_date);	
				    getContentResolver().insert(MessageProvider.CONTENT_URI, values);
				}
			}
			
			if(newMsgs > 0 && generateNotification){
				Subject subject = db.getSubjectByRemoteId(remoteSubjectId);
				Intent newIntent = new Intent(this, MessageActivity.class);
				Bundle b = new Bundle();
				b.putLong(Constants.SUBJECT_ID, subjectId);
				newIntent.putExtras(b);
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
				
				// Build notification
				Notification noti = new Notification.Builder(this)
				        .setContentTitle("New Messages (" + newMsgs + ")")
				        .setContentText("Subject: " + subject.getShortDesc())
				        .setSmallIcon(R.drawable.stat_notify_chat)
				        .setContentIntent(pIntent)
				        .getNotification();
				
				NotificationManager notificationManager = (NotificationManager) 
						  getSystemService(NOTIFICATION_SERVICE); 
				
				// Hide the notification after its selected
				noti.flags |= Notification.FLAG_AUTO_CANCEL;

				notificationManager.notify(0, noti);
				
			}
			
		}
		//return true;
	}
	
}
