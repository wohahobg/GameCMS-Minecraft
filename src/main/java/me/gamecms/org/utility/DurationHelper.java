package me.gamecms.org.utility;

public class DurationHelper {
	
	public static Long getTickDurationFromFormat(String format, int duration) {
		
		long time;

		int second = 1;
		int minute = 60;
		int hour = 60 * 60;
		int day = 60 * 60 * 24;
		int week = 60 * 60 * 24 * 7;
		int mounth = 60 * 60 * 24 * 30;
		int year = 60 * 60 * 24 * 360;

		switch (format) {

		case "s":
			time = duration * second;
			break;
		case "m":
			time = duration * minute;
			break;
		case "h":
			time = duration * hour;
			break;
		case "d":
			time = duration * day;
			break;
		case "w":
			time = duration * week;
			break;
		case "M":
			time = duration * mounth;
			break;
		case "y":
			time = duration * year;
			break;
		default:
			return null;
		}

		return 20 * time;
		
	}
	
	public static Long getLongDurationFromFormat(String format, int duration) {
		
		long time;

		int second = 1;
		int minute = 60;
		int hour = 60 * 60;
		int day = 60 * 60 * 24;
		int week = 60 * 60 * 24 * 7;
		int mounth = 60 * 60 * 24 * 30;
		int year = 60 * 60 * 24 * 360;

		switch (format) {

		case "s":
			time = duration * second;
			break;
		case "m":
			time = duration * minute;
			break;
		case "h":
			time = duration * hour;
			break;
		case "d":
			time = duration * day;
			break;
		case "w":
			time = duration * week;
			break;
		case "M":
			time = duration * mounth;
			break;
		case "y":
			time = duration * year;
			break;
		default:
			return null;
		}

		return 1000 * time;
		
	}
	
}
