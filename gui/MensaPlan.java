/*
 * Created on 12.01.2008
 *
 */
package gui;


import java.util.Hashtable;

import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Ticker;

import backend.Mensa;
import backend.MenuStorage;
import backend.Offer;
import backend.StorageWatcher;

public class MensaPlan extends List implements StorageWatcher, MensaSelector {
	private Hashtable items;
	private static Ticker noItems = new Ticker("Wähle 'Auffrischen' zum herunterladen des Speiseplans");
	
	private SiteSelector siteSelector;
	
	public MensaPlan() {
		super("Mensa", List.IMPLICIT);
		items = new Hashtable();
		configureTicker();
	}
	
	private void configureTicker() {
		if (size() == 0) {
			setTicker(noItems);
		} else {
			setTicker(null);
		}
	}
	
	public void refresh(MenuStorage storage) {
		this.deleteAll();
		items.clear();
		
		int j = 0;
		Offer[] offers = storage.getOffers();
		for (int i=0; i < offers.length; i++) {
			Mensa m = offers[i].getMensa();
			
			boolean wanted = (siteSelector == null || siteSelector.isSelected(m.getSite()));
			
			if (! items.contains(m) && wanted) {
				items.put(new Integer(j), m);
				this.append("("+m.getSite().getShortName()+") "+m.getLongName(), null);
				j++;
			}
		}
		configureTicker();
	}
	
	public Mensa getSelectedMensa() {
		return (Mensa) items.get( new Integer( getSelectedIndex() ) );
	}

	public void setSiteSelector(SiteSelector siteSelector) {
		this.siteSelector = siteSelector;
	}
	
}
