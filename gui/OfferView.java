/*
 * Created on 12.01.2008
 *
 */
package gui;


import java.util.Hashtable;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;

import backend.Offer;

public class OfferView extends Form {
	
	private StringItem dish;
	private StringItem mensa;
	private Gauge rating;
	
	private Hashtable icons;
	
	private Spacer getSpacer(int height) {
		Spacer s = new Spacer(1, height);
		s.setLayout(Spacer.LAYOUT_NEWLINE_AFTER | Spacer.LAYOUT_NEWLINE_BEFORE);
		return s;
	}
	
	public OfferView() {
		super("Gericht");
		icons = new Hashtable();
		mensa = new StringItem("", "");
		mensa.setLayout(StringItem.LAYOUT_CENTER);
		dish = new StringItem("", "");
		dish.setLayout(StringItem.LAYOUT_DEFAULT | StringItem.LAYOUT_LEFT);
		
		rating = new Gauge("Bewertung: ", false, 5*100, 0);
		rating.setLayout(Gauge.LAYOUT_EXPAND);
		
		
		append(mensa);
		append(getSpacer(5));
		append(dish);
		append(getSpacer(2));
		append(rating);
	}
	
	public void setOffer(Offer o) {
		removeIcons();
		addIcons(o);
		setTitle(o.getDay().toFormattedString()+" ["+o.getMensa().getShortName()+"]");
		dish.setText( o.getDescription() );
		
		mensa.setText(o.getMensa().getLongName());
		
		rating.setLabel("Bewertung ("+o.getVotes()+" Stimme"+(o.getVotes()!=1 ? "n" : "")+") :");
		rating.setValue((int) (o.getRating()*100));
		
		/*
		PseudoPic[] pics = o.getPictures();
		for (int i=0; i<pics.length; i++) {
			Image img = pics[i].getImage();
			ImageItem ii = new ImageItem("", img, 0, "", 0);
			ii.setLayout(ImageItem.LAYOUT_CENTER);
			append(ii);
			icons.put(ii, ii);
		}
		*/
	}
		
	private void removeIcons() {
		synchronized (icons) {
			int i = 0;
			while (i<size()) {
				if ( icons.contains(get(i)) ) {
					icons.remove(get(i));
					delete(i);
				} else {
					i++;
				}
			}
		}
	}
	
	private void insertIcon(Image img, String title) {
		synchronized (icons) {
			ImageItem ii = new ImageItem("", img, ImageItem.LAYOUT_CENTER, title);
			icons.put(ii, ii);
			insert(4, ii);
		}
	}
	
	private void addIcons(Offer o) {
		synchronized (icons) {
			if (o.isInGroup("Schwein")) insertIcon(Pics.PORK, "Schwein");
			if (o.isInGroup("Vegetarisch")) insertIcon(Pics.VEGETARIAN, "Vegetarisch");
			if (o.isInGroup("Rind")) insertIcon(Pics.BEEF, "Rind");
			if (o.isInGroup("Fisch")) insertIcon(Pics.FISH, "Fisch");
			if (o.isInGroup("Wild")) insertIcon(Pics.GAME, "Wild");
			if (o.isInGroup("Lamm")) insertIcon(Pics.SHEEP, "Lamm");
			if (o.isInGroup("Geflügel")) insertIcon(Pics.CHICKEN, "Geflügel");
		}
		if (o.getPictures().length >0) insertIcon(Pics.CAMERA, "Photo");
		if (o.isNewMeal()) insertIcon(Pics.UNKNOWN, "Neu");
	}

}
