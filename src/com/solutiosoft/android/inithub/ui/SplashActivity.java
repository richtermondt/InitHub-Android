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
import static com.solutiosoft.android.inithub.Constants.SUBJECT_ACTIVITY;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.solutiosoft.android.inithub.AlarmReceiver;
import com.solutiosoft.android.inithub.R;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.User;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	protected boolean active=true;
	protected int splashTime=2000;
	protected int timeIncrement=100;
	protected int sleepTime=100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Thread splashThread = new Thread() {
			@Override
			public void run(){
				// default activity
				
				String activity = LOGIN_ACTIVITY;
				try {
					InitHubDatabaseManager db = new InitHubDatabaseManager(getApplicationContext());
					User user = db.getUser();
					if(user != null){
						activity = SUBJECT_ACTIVITY;
						//Utils.startAlarm(getApplicationContext());
						AlarmReceiver ar = new AlarmReceiver();
						ar.startAlarm(getApplicationContext());
					}
					int elaspedTime=0;
					while(active && elaspedTime <= splashTime){
						sleep(sleepTime);
						if(active) {
							elaspedTime=elaspedTime + timeIncrement;
						}
					}
				} catch(InterruptedException e) {
					// do nothing
				} finally {
					Intent i = new Intent(activity);
					finish();
					startActivity(i);
					
				}
			}
		};
		splashThread.start();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			active=false;			
		}
		return true;
		
	}
	
	/*
	public void startAlarm2() {
		
		// TODO: need to make interval configurable
		// TODO: need to restart alarm on reboot
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String sync_frequency = sharedPrefs.getString("sync_frequency", "NULL");
		
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//alarmManager.cancel(pendingIntent);

		if(sync_frequency!= null){
			if(sync_frequency.equals("-1")){
				Log.d(TAG, "Canceling alarm.........");
				alarmManager.cancel(pendingIntent);
			}
			else {
				int i;
				try {
					i = Integer.parseInt(sync_frequency);
					//i = (Integer.parseInt(sync_frequency) * 60 * 1000);
					Log.d(TAG, "Starting alarm... Frequency: " + i + " minutes");
					i = Integer.parseInt(sync_frequency);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, (i * 60 * 1000), pendingIntent);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "NumberFormatException... This should not happen. Canceling alarm.");
					alarmManager.cancel(pendingIntent);
				}
				
			}
			
		}
		
		
		
		
	}
	 */

}
