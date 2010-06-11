package gui;

import java.util.Hashtable;

import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Ticker;

import backend.Day;
import backend.MenuStorage;
import backend.Offer;
import backend.StorageWatcher;

/*
 * Created on 20.12.2007
 *
 */

public class MenuPlan extends List implements StorageWatcher {
	private MensaSelector ms;
	private DaySelector ds;
	private Hashtable items;
	
	public MenuPlan() {
		super("Menu", List.IMPLICIT);
		items = new Hashtable();
	}
	
	private void prepareTicker() {
		if (ds.getSelectedDay() == null) {
			setTicker(new Ticker("Keine Daten verf�bar"));
		} else	if (ds.getSelectedDay().equals(Day.today())) {
			setTicker(null);
		} else {
			setTicker(new Ticker(ds.getSelectedDay().toFormattedString()));
		}
	}
	
	public void setMensaSelector(MensaSelector ms) {
		this.ms = ms;
	}
	
	public void setDaySelector(DaySelector ds) {
		this.ds = ds;
	}
	
	public void refresh(MenuStorage storage) {
		this.deleteAll();
		items.clear();
		
		prepareTicker();
		
		if (ms != null && ms.getSelectedMensa() != null) 
			this.setTitle(ms.getSelectedMensa().getLongName());
		
		Offer[] offers = storage.getOffers();
		Day now = ds.getSelectedDay();
		
		int j = 0;
		for (int i=0; i < offers.length; i++) {
			if (ms == null || (ms.getSelectedMensa() != null && ms.getSelectedMensa().equals(offers[i].getMensa()))) {
				if ( now != null && now.equals( offers[i].getDay() ) ) {
					items.put(new Integer(j), offers[i]);
					this.append( offers[i].getDescription(), null);
					j++;
				}
			}
		}
	}

	public Offer getSelectedOffer() {
		return (Offer) items.get( new Integer( getSelectedIndex() ) );
	}

}
