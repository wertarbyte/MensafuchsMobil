package backend;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;


/*
 * Created on 20.12.2007
 *
 */

public class MenuStorage {
	private static final String URLBASE = "http://mensafuchs.de/csv/client-mensafuchs-mobil/";
	private Vector offers;
	private Vector watcher;
	
	public MenuStorage() {
		offers = new Vector();
		watcher = new Vector();
	}
		
	public void addWatcher(StorageWatcher w) {
		watcher.addElement(w);
	}
	
	private void informWatchers() {
		Enumeration e = watcher.elements();
		while (e.hasMoreElements()) {
			StorageWatcher w = (StorageWatcher) e.nextElement();
			w.refresh(this);
		}
	}
	
	public void freeze() {
		try {
			RecordStore rs = RecordStore.openRecordStore("MensafuchsMenu",true);
			clearRecords(rs);
			
			synchronized (offers) {
				Enumeration e = offers.elements();
				while (e.hasMoreElements()) {
					Offer o = (Offer) e.nextElement();
					byte[] frozenOffer = o.freeze().getBytes();
					rs.addRecord(frozenOffer, 0, frozenOffer.length);
				}
			}
			rs.closeRecordStore();
		} catch (RecordStoreFullException e) {
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			e.printStackTrace();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void clearRecords(RecordStore rs) throws RecordStoreNotOpenException, RecordStoreException {
		RecordEnumeration re = rs.enumerateRecords(null, null, false);
		while (re.hasNextElement()) {
			rs.deleteRecord( re.nextRecordId() );
		}
	}
	
	public  void thaw() {
		synchronized (offers) {
			offers.removeAllElements();
			try {
				RecordStore rs = RecordStore.openRecordStore("MensafuchsMenu", true);
				
				RecordEnumeration re = rs.enumerateRecords(null, null, false);
				while (re.hasNextElement()) {
					String o = new String(re.nextRecord());
					processLine(o);
					//Offer offer = Offer.fromCSV(o);
					//insertOffer(offer, false);
				}
				
			} catch (RecordStoreFullException e) {
				e.printStackTrace();
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
			informWatchers();
		}
	}
	
	public void retrieve(Day d) throws IOException {
		String daystring = d.toString();
		download(URLBASE+daystring);
	}
	
	public void retrieve() throws IOException {
		download(URLBASE+"week");
	}
	
	private void download(String url) throws IOException {
		StringBuffer buf = new StringBuffer();
		HttpConnection c = null;
		InputStream is = null;
		try {
			c = ( HttpConnection ) Connector.open( url );
			is = c.openInputStream();
		
			int b;
			// Just read everything
			while ((b = is.read()) != -1) {
				if ( (char) b == '\n') {
					// create a new offer object
					processLine(buf.toString());
					buf = new StringBuffer();
				} else {
					buf.append((char)b);
				}
			}
		} finally {
			informWatchers();
			try { if (is != null) is.close(); } catch (Exception e) {}
			try { if (c != null) c.close(); } catch (Exception e) {}
		}
	}
	
	public void importFromFile(FileConnection fc) throws IOException {
		InputStream is = fc.openInputStream();
		StringBuffer buf = new StringBuffer();
		int b;
		while ((b = is.read()) != -1) {
			if ((char) b == '\n') {
				processLine(buf.toString());
				buf = new StringBuffer();
			} else {
				buf.append((char) b);
			}
		}
		is.close();
		informWatchers();
	}
	
	private void processLine(String input) {
		Offer o = Offer.fromCSV(input);
		insertOffer(o, false);
	}
	
	protected void insertOffer(Offer o) {
		insertOffer(o, true);
	}
	
	private void insertOffer(Offer o, boolean refresh) {
		synchronized (offers) {
			// optimization: We check the last element and append
			// if it precedes our new element, this will increase
			// performance for most cases
			if (offers.isEmpty() || ((Offer)offers.lastElement()).compareTo(o) == -1) {
				offers.addElement(o);
			} else {
				boolean inserted = false;
				// ok, we cannot do it the easy way, we have to scan the table
				for (int i=0; !inserted && i<offers.size(); i++) {
					Offer a = (Offer) offers.elementAt(i);
					if (o.getId() == a.getId()) {
						offers.removeElement(a);
					}
					if (! inserted && o.compareTo(a) == -1) {
						offers.insertElementAt(o, i);
						inserted = true;
					}
				}
				// if we reach this, we can append the offer
				if (! inserted) {
					offers.addElement(o);
				}
			}
			
		}
		if (refresh) {
			informWatchers();
		}
	}
	
	protected void clear() {
		synchronized (offers) {
			offers.removeAllElements();
			informWatchers();
		}
	}
	
	public int size() {
		return offers.size();
	}
	
	public void expire(Day d, boolean including) {
		synchronized (offers) {
			int i=0;
			while (i<offers.size()) {
				Offer a = (Offer) offers.elementAt(i);
				if (a.getDay().priorTo(d) || (including && a.getDay().equals(d))) {
					offers.removeElement(a);
				} else {
					i++;
				}
			}
			
			informWatchers();
		}
	}
	
	public Offer[] getOffers() {
		synchronized (offers) {
			Offer[] result = new Offer[offers.size()];
			
			int i = 0;
			Enumeration en = offers.elements();
			while (en.hasMoreElements()) {
				Offer o = (Offer) en.nextElement();
				result[i++] = o;
			}
			return result;
		}
	}
	
}
