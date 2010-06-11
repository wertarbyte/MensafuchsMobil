/*
 * Created on 12.01.2008
 *
 */
package gui;

import backend.Day;

public interface DaySelector {
	public Day getSelectedDay();
	public boolean isAutoSelected();
}
