/*
 * Created on 20.01.2009
 *
 */
package gui;

import java.util.Hashtable;

import javax.microedition.lcdui.List;

import backend.MenuStorage;
import backend.Offer;
import backend.Site;
import backend.StorageWatcher;

public class SitePlan extends List implements SiteSelector,StorageWatcher {
	private Hashtable items;
	
	public SitePlan() {
		super("Orte", List.MULTIPLE);
		items = new Hashtable();
	}

	public void refresh(MenuStorage storage) {
		this.deleteAll();
		items.clear();

		int j = 0;
		Offer[] offers = storage.getOffers();
		for (int i=0; i < offers.length; i++) {
			Site s = offers[i].getMensa().getSite();
			if (! items.contains(s)) {
				items.put(new Integer(j), s);
				this.append(s.getLongName(), null);
				j++;
			}
		}
	}

	public boolean isSelected(Site s) {
		boolean[] boxes = new boolean[size()];
		getSelectedFlags(boxes);
		for (int i=0; i < boxes.length; i++) {
			Site si = (Site) items.get(new Integer(i));
			
			if (boxes[i] && s.equals(si) ) {
				return true;
			}
		}
		return false;
	}

}
