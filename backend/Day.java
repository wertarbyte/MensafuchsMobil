/*
 * Created on 12.01.2008
 *
 */
package backend;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.sun.cldc.util.j2me.CalendarImpl;

public class Day {
	private int year;
	private int month;
	private int day;

	private Day(int year, int month, int day) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public static Day construct(int year, int month, int day) {
		Calendar c = CalendarImpl.getInstance(TimeZone.getDefault());
		c.setTime(new Date(0));
		try {
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
			c.set(Calendar.DAY_OF_MONTH, day);
			return Day.fromDate(c.getTime());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static Day today() {
		return fromDate(new Date());
	}
	
	public static Day fromString(String dateString) {
		String[] c = Util.split(dateString, '-');
		int year = Integer.parseInt(c[0]);
		int month = Integer.parseInt(c[1]);
		int day = Integer.parseInt(c[2]);
		return new Day(year, month, day);
	}
	
	public String toString() {
		return year+"-"+month+"-"+day;
	}
	
	public String toFormattedString() {
		StringBuffer sb = new StringBuffer();
		Calendar c = CalendarImpl.getInstance(TimeZone.getDefault());
		c.setTime(toDate());
		switch (c.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY: sb.append("Montag"); break;
			case Calendar.TUESDAY: sb.append("Dienstag"); break;
			case Calendar.WEDNESDAY: sb.append("Mittwoch"); break;
			case Calendar.THURSDAY: sb.append("Donnerstag"); break;
			case Calendar.FRIDAY: sb.append("Freitag"); break;
			case Calendar.SATURDAY: sb.append("Samstag"); break;
			case Calendar.SUNDAY: sb.append("Sonntag"); break;
		}
		sb.append(", ");
		sb.append(c.get(Calendar.DAY_OF_MONTH));
		sb.append(".");
		sb.append( c.get(Calendar.MONTH)+(1-Calendar.JANUARY) );
		sb.append(".");
		sb.append( c.get(Calendar.YEAR) );
		return sb.toString();
	}
	
	public static Day fromDate(Date d) {
		Calendar c = CalendarImpl.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new Day(year, month, day);
	}
	
	public Date toDate() {
		Calendar c = CalendarImpl.getInstance(TimeZone.getDefault());
		c.setTime(new Date(0));
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}
	
	public boolean equals(Object arg) {
		if (arg.getClass().equals(this.getClass())) {
			Day d = (Day) arg;
			return (day == d.day && month == d.month && year == d.year);
		} else {
			return false;
		}
	}
	
	public boolean priorTo(Day b) {
		return toDate().getTime() < b.toDate().getTime();
	}

	public boolean laterThan(Day b) {
		return toDate().getTime() > b.toDate().getTime();
	}
	
	public int hashCode() {
		return year ^ month ^ day;
	}
	
	public int compareTo(Object o) {
		Day d = (Day) o;
		if (d.equals(this)) {
			return 0;
		}
		if (d.laterThan(this)) {
			return -1;
		} else {
			return 1;
		}
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}
	
}
