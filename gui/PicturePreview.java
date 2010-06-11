/*
 * Created on 16.01.2008
 *
 */
package gui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Spacer;

public class PicturePreview extends Form {

	public PicturePreview() {
		super("");
		reset();
	}
	
	public void reset() {
		setTitle("Vorschau");
		deleteAll();
	}
	
	public void addPicture(Image image) {
		append(new ImageItem("", image, ImageItem.LAYOUT_CENTER, ""));
		append(new Spacer(10, 10));
	}
}
