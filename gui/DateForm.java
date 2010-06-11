/*
 * Created on 27.01.2008
 *
 */
package gui;

import java.util.Date;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Form;

import backend.Day;

public class DateForm extends Form implements DaySelector {

	private DateField date;
	
	public DateForm() {
		super("Datum hinzufügen");
		date = new DateField("Datum: ", DateField.DATE);
		Day t = Day.today();
		date.setDate(t.toDate());
		append(date);
	}

	public Day getSelectedDay() {
		Date d = date.getDate();
		return Day.fromDate(d);
	}

	public boolean isAutoSelected() {
		return false;
	}	
}
