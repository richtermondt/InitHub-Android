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
