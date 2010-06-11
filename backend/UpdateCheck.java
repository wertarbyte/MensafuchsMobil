/*
 * Created on 30.01.2008
 *
 */
package backend;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;

public class UpdateCheck {
	private static final String url = "http://mensafuchs.de/j2me/MensafuchsMobil.jad";
	
	public static class UpdateStatus {
		private String current;
		private String published;
		protected UpdateStatus(String current, String published) {
			this.current = current;
			this.published = published;
		}
		public String getCurrent() {
			return current;
		}
		public String getPublished() {
			return published;
		}
		
		public boolean isUpToDate() {
			/*
			System.err.println("Comparing: ");
			System.err.println(current);
			System.err.println(published);
			*/
			String[] partsC = Util.split(current, '.');
			String[] partsP = Util.split(published, '.');
			for (int i=0; i < partsC.length; i++) {
				int c = Integer.parseInt(partsC[i]);
				int p = Integer.parseInt(partsP[i]);
				if (p > c) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static String getPublishedVersion() throws IOException {
		StringBuffer buf = new StringBuffer();
		String key = "";
		
		HttpConnection c = null;
		InputStream is = null;
		try {
			c = ( HttpConnection ) Connector.open( url );
			is = c.openInputStream();
		
			int b;
			// Just read everything
			while ((b = is.read()) != -1) {
				if ((char)b == ':') {
					// save the key value
					key = buf.toString();
					buf = new StringBuffer();
				} else if ( (char) b == '\n') {
					if (key.toLowerCase().equals("MIDlet-Version".toLowerCase())) {
						return buf.toString();
					}
					buf = new StringBuffer();
				} else if ((char) b != ' ') {
					// ignore whitespaces
					buf.append((char)b);
				}
			}
		} finally {
			try { if (is != null) is.close(); } catch (Exception e) {}
			try { if (c != null) c.close(); } catch (Exception e) {}
		}
		return null;
	}
	
	public static UpdateStatus checkStatus(MIDlet m) throws IOException {
		String current = m.getAppProperty("MIDlet-Version");
		String published = getPublishedVersion();
		if (published != null && current != null) {
			return new UpdateStatus(current, published);
		}
		return null;
	}
}
