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
