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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	public static String formatCreateDate(String date_string, String format){
		
		try {
			//Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date_string);
			Date date = new SimpleDateFormat(format).parse(date_string);
			String newstring = new SimpleDateFormat("EEE, MMM d yyyy HH:mm").format(date);
			return newstring;
			
		} catch (ParseException e) {
			return date_string; 
		}
	}
	
}
