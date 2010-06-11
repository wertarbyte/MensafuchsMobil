/*
 * Created on 30.01.2008
 *
 */
package gui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;

public class AboutBox extends Form {
	private ImageItem img;
	private StringItem about;
	
	public AboutBox(String version) {
		super("MensafuchsMobil");
		img = new ImageItem(version, Pics.MENSAFUCHS, ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_VCENTER, "MensafuchsMobil-Logo");
		append(img);
		about = new StringItem("Informationen: ", "MensafuchsMobil wurde von Stefan Tomanek entwickelt. Weitere Informationen auf der Webseite http://mensafuchs.de/.");
		append(about);
	}

}
