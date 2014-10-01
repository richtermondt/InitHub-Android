package com.solutiosoft.android.inithub;

import java.util.ArrayList;

import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.service.MessageIntentService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "AlarmReceiver";
	private static final int ALARM_ID = 234324243;
	
	public AlarmReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		
		// we need to call the intent service here
		Log.d(TAG, "*********** AlarmReceiver.onReceive");
		InitHubDatabaseManager db = new InitHubDatabaseManager(context);
		ArrayList<Integer> subjects = db.getSubscribedSubjects();
		Intent newIntent = new Intent(context, MessageIntentService.class);
		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList(Constants.MESSAGE_SERVICE_SUBJECTS, subjects);
		bundle.putInt(Constants.MESSAGE_SERVICE_ACTION, Constants.MESSAGE_ACTION_PROCESS_SUBJECTS);
		//bundle.putLong(Constants.SUBJECT_ID, subjectId);
		newIntent.putExtras(bundle);
		context.startService(newIntent);
		
		wl.release();
		
	}
	
	public void startAlarm(Context cxt) {
		
		// TODO: need to make interval configurable
		// TODO: need to restart alarm on reboot
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
		String sync_frequency = sharedPrefs.getString(Constants.SYNC_FREQUENCY, null);
		
		Intent intent = new Intent(cxt, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(cxt, ALARM_ID, intent, 0);
		AlarmManager alarmManager = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
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
					Log.d(TAG, "Starting alarm... Frequency: " + i + " minutes");
					i = Integer.parseInt(sync_frequency);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, (60 * 1000), pendingIntent);
				} catch (NumberFormatException e) {
					Log.d(TAG, "NumberFormatException... This should not happen. Canceling alarm.");
					alarmManager.cancel(pendingIntent);
				}
				
			}
			
		}
		
		
	}
	
	public void cancelAlarm(Context cxt){
		Intent intent = new Intent(cxt, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(cxt, ALARM_ID, intent, 0);
		AlarmManager alarmManager = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);		
	}
}
