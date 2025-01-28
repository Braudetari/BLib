package common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Some LocalDate Utils
 */
public class DateUtil {
	/**
	 * Get LocalDate from String
	 * @param string
	 * @return LocalDate
	 */
	public static LocalDate DateFromString(String string) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate date = null;
		try {
			date = LocalDate.parse(string, formatter);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not convert String to LocalDate");
		}
		return date;
	}
	
	/**
	 * Get String from LocalDate
	 * @param date
	 * @return String
	 */
	public static String DateToString(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return date.format(formatter);
	}
}
