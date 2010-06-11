package gui;


import java.io.IOException;
import java.util.Date;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import backend.Day;
import backend.MenuStorage;
import backend.Offer;
import backend.PseudoPic;
import backend.Rating;
import backend.UpdateCheck;
import backend.UpdateCheck.UpdateStatus;

/*
 * Created on 18.12.2007
 *
 */

public class MensafuchsMobil extends MIDlet implements CommandListener {
	private Stack cards;
	private MensaPlan mensa;
	private MenuPlan menu;
	private OfferView offer;
	private DayList days;
	private DateForm date;
	private PleaseWait wait;
	private RateScreen rate;
	private PicturePreview pic;
	private FileSelector files;
	private AboutBox about;
	
	private SitePlan sites;
	
	private MenuStorage storage;
	
	private static final Command dayCmd = new Command("Datum", Command.SCREEN, 2);
	private static final Command refreshCmd = new Command("Auffrischen", Command.SCREEN, 1);
	private static final Command rateCmd = new Command("Bewerten", Command.SCREEN, 3);
	private static final Command sendCmd = new Command("Senden", Command.OK, 1);
	private static final Command backCmd = new Command("Zurück", Command.BACK, 0);
	private static final Command exitCmd = new Command("Beenden", Command.EXIT, 0);
	private static final Command picCmd = new Command("Photo", Command.SCREEN, 3);
	private static final Command dateCmd = new Command("Hinzufügen", Command.SCREEN, 2);
	private static final Command expireCmd = new Command("Aufräumen", Command.ITEM, 2);
	private static final Command importCmd = new Command("Importieren", Command.ITEM, 2);
	private static final Command aboutCmd = new Command("Infos", Command.HELP, 3);
	private static final Command updateCmd = new Command("Aktuell?", Command.SCREEN, 2);
	
	public MensafuchsMobil() {
		Pics.loadPictures();
		
		cards = new Stack();
		
		sites = new SitePlan();
		
		mensa = new MensaPlan();
		// mensa.setSiteSelector(sites);
		mensa.addCommand(dayCmd);
		mensa.addCommand(refreshCmd);
		mensa.addCommand(aboutCmd);
		mensa.addCommand(exitCmd);
		
		about = new AboutBox(this.getAppProperty("MIDlet-Version"));
		about.setCommandListener(this);
		about.addCommand(backCmd);
		about.addCommand(updateCmd);
		
		wait = new PleaseWait();
		
		mensa.setCommandListener(this);
		
		offer = new OfferView();
		offer.setCommandListener(this);
		offer.addCommand(backCmd);
		offer.addCommand(rateCmd);
		offer.addCommand(picCmd);

		days = new DayList();
		days.addCommand(dateCmd);
		days.addCommand(importCmd);
		days.addCommand(expireCmd);
		days.setCommandListener(this);
		
		date = new DateForm();
		date.addCommand(dateCmd);
		date.addCommand(backCmd);
		date.setCommandListener(this);

		menu = new MenuPlan();
		menu.setMensaSelector(mensa);
		menu.setDaySelector(days);
		menu.addCommand(backCmd);
		menu.addCommand(dayCmd);
		menu.setCommandListener(this);

		storage = new MenuStorage();
		storage.addWatcher(sites);
		storage.addWatcher(mensa);
		storage.addWatcher(days);
		storage.addWatcher(menu);
		
		rate = new RateScreen();
		rate.setCommandListener(this);
		rate.addCommand(backCmd);
		rate.addCommand(sendCmd);
		
		pic = new PicturePreview();
		pic.setCommandListener(this);
		pic.addCommand(backCmd);
		
		files = new FileSelector();
		files.setCommandListener(this);
		files.addCommand(backCmd);
	}
	
	protected void changeTo(Displayable d) {
		synchronized (cards) {
			// Doesn't work on some Nokia devices 
			// Displayable current = Display.getDisplay(this).getCurrent();
			cards.push(d);
			lookOntoStack();
		}
	}
	
	protected void goBack() {
		synchronized (cards) {
			cards.pop();
			lookOntoStack();
		}
	}
	
	private void lookOntoStack() {
		Display.getDisplay(this).setCurrent( (Displayable) cards.peek() );
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// clean up
		wait.setCause("Sichere Speiseplan...");
		changeTo(wait);
		storage.freeze();
		notifyDestroyed();
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	protected void startApp() throws MIDletStateChangeException {
		try {
			changeTo(mensa);
			wait.setCause("Lade Speiseplan...");
			changeTo(wait);
			storage.thaw();
			if (storage.size() == 0) {
				Alert a = new Alert("Willkommen","Willkommen bei Mensafuchs Mobil!\nWähle den Menüpunkt 'Auffrischen', um den aktuellen Speiseplan herunterzuladen", Pics.MENSAFUCHS, AlertType.INFO);
				a.setCommandListener(this);
				a.setTimeout(Alert.FOREVER);
				changeTo(a);
			} else {
				goBack();
			}
		} catch (Exception e) {
			Alert a = new Alert("Fehler",e.getMessage(), null, AlertType.ERROR);
			a.setTimeout(Alert.FOREVER);
			Display.getDisplay(this).setCurrent(a);
		}
	}
	
	protected void showAlert(String title, String message, Image img, AlertType type) {
		Alert a = new Alert(title, message, img, type);
		a.setTimeout(Alert.FOREVER);
		a.setCommandListener(this);
		changeTo(a);
		// Display.getDisplay(this).setCurrent( a );
	}
	
	private void refreshMensaPlan() {
		wait.setCause("Lade neuen Speiseplan herunter...");
		changeTo(wait);
		Runnable r = new Runnable() {
			public void run() {
				try {
					storage.retrieve();
					goBack();
				} catch (Exception e) {
				 	showAlert("Kommunikationsfehler", "Herunterladen fehlgeschlagen: "+e.getMessage(), null, AlertType.ERROR);
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private void goUpDirectory() {
		Runnable r = new Runnable() {
			public void run() {
				files.goUp();
			}
		};
		Thread t2 = new Thread(r);
		t2.start();
	}
	
	private void importFromFile() {
		Runnable r = new Runnable() {
			public void run() {
				// true if a file has been selected 
				if (files.fileHasBeenSelected()) {
					String url = files.getPath() + files.getSelectedFile();
					wait.setCause("Importiere Speiseplan aus "+url+"...");
					changeTo(wait);
					FileConnection fc;
					try {
						fc = (FileConnection) Connector.open(url, Connector.READ);
						storage.importFromFile(fc);
						fc.close();
					} catch (IOException e) {
						// do something!
					}
					// leave the waiting dialog
					goBack();
					// and the file selector
					goBack();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private void downloadPhotos() {

		Offer o = menu.getSelectedOffer();
		final PseudoPic[] pics = o.getPictures();
		if (pics.length > 0) {
			pic.reset();
			pic.setTitle(o.getDescription());
			changeTo(pic);
			wait.setCause("Lade Photos...");
			changeTo(wait);
			
			Runnable r = new Runnable() {
				public void run() {
					try {
						for (int i=0; i<pics.length; i++) {
							System.err.println("Lade "+i);
							pics[i].download();
							pic.addPicture(pics[i].getImage());
						}
						goBack();
					} catch (Exception e) {
						showAlert("Fehler beim Herunterladen!", "Fehler bei der Kommunikation: "+e.getMessage(), null, AlertType.ERROR);
					}
				}
			};
			Thread t2 = new Thread(r);
			t2.start();
		} else {
			Alert a = new Alert("Keine Photos", "Es sind keine Photos zu diesem Gericht vorhanden!", null, AlertType.ERROR);
			a.setTimeout(Alert.FOREVER);
			a.setCommandListener(this);
			changeTo(a);
		}
	}
	
	private void addSpecificDate() {
		// add the entered date
		final Day day = date.getSelectedDay();
		if (day == null) {
			showAlert("Ungültiges Datum", "Kein gültiges Datum angegeben!", null, AlertType.ERROR);
		} else {
			wait.setCause("Lade Speiseplan ("+day.toFormattedString()+") herunter...");
			changeTo(wait);
			Runnable r = new Runnable() {
				public void run() {
					try {
						storage.retrieve(day);
						goBack();
						goBack();
					} catch (Exception e) {
						showAlert("Kommunikationsfehler", "Herunterladen fehlgeschlagen: "+e.getMessage(), null, AlertType.ERROR);
					}
				}
			};
			Thread t = new Thread(r);
			t.start();
		}
	}
	
	private void sendOfferRating() {
		Offer o = menu.getSelectedOffer();
		int score = rate.getRating();
		String comment = rate.getComment();
		if (score > 0) {
			wait.setCause("Übermittle Bewertung...");
			changeTo(wait);
			final Rating rating = new Rating(o.getId(), score, comment);
			
			Runnable r = new Runnable() {
				public void run() {
					try {
						rating.send();
						// push back the waiting screen
						goBack();
						// also hide the rating dialog
						goBack();
					} catch (Exception e) {
						showAlert("Bewertung nicht möglich!", "Fehler bei der Kommunikation: "+e.getMessage(), null, AlertType.ERROR);
					}
				}
			};
			Thread t2 = new Thread(r);
			t2.start();
		} else {
			showAlert("Bewertung nicht möglich!", "Keine Punktzahl angegeben!", null, AlertType.ERROR);
		}
	}

	public void commandAction(Command cmd, Displayable d) {
		
		if (cmd == exitCmd) {
			try {
				destroyApp(true);
			} catch (MIDletStateChangeException e) {
				e.printStackTrace();
			}
		} else if (cmd == refreshCmd) {
			refreshMensaPlan();
		} else if (cmd == backCmd || cmd == Alert.DISMISS_COMMAND) {
			if (d == files && !files.isTopLevel()) {
				goUpDirectory();
			} else {
				goBack();
				// if the next card on the stack is the wait screen,
				// we probably popped away an error message.
				// let's get rid of  the wait screen as well
				if (cards.peek() == wait) {
					goBack();
				}
			}
		} else if (cmd == List.SELECT_COMMAND) {
			if (d == mensa) {
				menu.refresh(storage);
				changeTo(menu);
			} else if (d == menu) {
				offer.setOffer( menu.getSelectedOffer() );
				changeTo(offer);
			} else if (d == days) {
				menu.refresh(storage);
				goBack();
			} else if (d == files) {
				importFromFile();
			}
		} else if (cmd == dayCmd) {
			changeTo(days);
		} else if (cmd == dateCmd) {
			if (d == date) {
				addSpecificDate();
			} else {
				changeTo(date);
			}
		} else if (cmd == rateCmd) {
			 if (menu.getSelectedOffer().getDay().toDate().getTime() < new Date().getTime()) {
					rate.reset();
					changeTo(rate);
			 } else {
				 	showAlert("Bewertung nicht möglich!", "Datum liegt in der Zukunft!", null, AlertType.ERROR);
			 }
		} else if (cmd == sendCmd) {
			sendOfferRating();
		} else if (cmd == picCmd) {
			downloadPhotos();
		} else if (cmd == importCmd) {
			Runnable r = new Runnable() {
				public void run() {
					files.refresh();
					changeTo(files);
				}
			};
			Thread t2 = new Thread(r);
			t2.start();
		} else if (cmd == expireCmd) {
			Day day = days.getSelectedDay();
			if (day != null) {
				storage.expire(day, ! days.isAutoSelected());
			}
		} else if (cmd == aboutCmd) {
			changeTo(about);
		} else if (cmd == updateCmd) {
			checkVersion();
		}
	}

	private void checkVersion() {
		wait.setCause("Prüfe auf neue Version...");
		changeTo(wait);
		final MensafuchsMobil mfm = this;
		Runnable r = new Runnable() {
			public void run() {
				try {
					UpdateStatus us = UpdateCheck.checkStatus(mfm);
					if (us == null) {
						showAlert("Aktualisierung", "Prüfung nicht möglich!", null, AlertType.ERROR);
					} else if (us.isUpToDate()) {
						showAlert("Aktualisierung", "Das Programm ist auf dem aktuellen Stand.", null, AlertType.INFO);
					} else {
						showAlert("Aktualisierung", "Das Programm ist nicht auf dem aktuellen Stand.\nInstallierte Version: "+us.getCurrent()+", aktuelle Version: "+us.getPublished(), null, AlertType.INFO);
					}
				} catch (Exception e) {
					showAlert("Kommunikationsfehler", "Herunterladen fehlgeschlagen: "+e.getMessage(), null, AlertType.ERROR);
				}
			}
		};
		Thread t = new Thread(r);
		t.start();	
	}

}
