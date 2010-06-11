/*
 * Created on 15.01.2008
 *
 */
package gui;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;

public class RateScreen extends Form {
	
	private ChoiceGroup choice;
	private TextField comment;
	
	public RateScreen() {
		super("Bewerten");

		choice = new ChoiceGroup("Deine Bewertung: ", Choice.POPUP);
		comment = new TextField("Kommentar: ", "", 2000, TextField.ANY & TextField.INITIAL_CAPS_SENTENCE);
		
		Image starImg = Pics.STAR;
		
		choice.append("-", starImg);
		choice.append("1 Stern", starImg);
		for(int i=2; i<=5; i++) {
			choice.append(i+" Sterne", starImg);
		}
		append(choice);
		append(comment);
	}
	
	public int getRating() {
		return choice.getSelectedIndex();
	}
	
	public String getComment() {
		return comment.getString();
	}
	
	public void reset() {
		comment.setString("");
		choice.setSelectedIndex(0, true);
	}

}
