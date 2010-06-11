/*
 * Created on 12.01.2008
 *
 */
package gui;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.List;

import backend.Day;
import backend.MenuStorage;
import backend.Offer;
import backend.StorageWatcher;

public class DayList extends List implements StorageWatcher, DaySelector {
	private Hashtable dates;
	
	public DayList() {
		super("Datum", List.IMPLICIT);
		
		dates = new Hashtable();
	}

	public void refresh(MenuStorage m) {
		dates.clear();
		deleteAll();
		
		Offer[] os = m.getOffers();
		// dates.put(new Integer(0), null);
		append("Automatisch", null);
		int j=1;
		for (int i=0; i < os.length; i++) {
			Day d = os[i].getDay();
			if (! dates.contains(d) ) {
				dates.put(new Integer(j), d);
				append(d.toFormattedString(), null);
				j++;
			}
		}
	}
	
	public Day getSelectedDay() {
		if (getSelectedIndex() == 0) {
			// select the most probable date
			Day today = Day.today();
			// our candidat
			Day cand = null;
			Enumeration en = dates.elements();
			while (en.hasMoreElements()) {
				Day d = (Day) en.nextElement();
				// return the current Day if we have it
				if (d.equals(today)) {
					return d;
				}
				
				if (d.priorTo(today)) {
					if (cand == null || d.laterThan(cand)) {
						cand = d;
					}
				} else {
					// later than today
					if (cand == null || d.priorTo(cand)) {
						cand = d;
					}
				}
			}
			return cand;
		} else {
			Day d = (Day) dates.get( new Integer(getSelectedIndex()) );
			return d;
		}
	}

	public boolean isAutoSelected() {
		return getSelectedIndex() == 0;
	}
}
