package com.solutiosoft.android.inithub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootupReceiver extends BroadcastReceiver {
	private static final String TAG = "BootupReceiver";

	public BootupReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			Log.d(TAG, "Boot up  broadcast received.........");
			
			AlarmReceiver ar = new AlarmReceiver();
			ar.startAlarm(context.getApplicationContext());
		}
		
	}
}
