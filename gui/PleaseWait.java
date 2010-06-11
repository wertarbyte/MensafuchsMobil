/*
 * Created on 13.01.2008
 *
 */
package gui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ImageItem;

public class PleaseWait extends Form {
	private ImageItem mfImage;
	private Gauge progress;
	
	public PleaseWait() {
		super("Bitte warten...");
		
		mfImage = new ImageItem("", Pics.MENSAFUCHS,  ImageItem.LAYOUT_CENTER | ImageItem.LAYOUT_NEWLINE_BEFORE | ImageItem.LAYOUT_NEWLINE_AFTER, "Mensafuchs");
		progress = new Gauge("",false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
		progress.setLayout(Gauge.LAYOUT_EXPAND | Gauge.LAYOUT_CENTER);
		append(mfImage);
		append(progress);
	}

	public void setCause(String string) {
		progress.setLabel(string);
	}
	
	
}
