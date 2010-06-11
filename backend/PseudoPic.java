/*
 * Created on 16.01.2008
 *
 */
package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;

public class PseudoPic {
	private String url;
	private Image img;
	
	private static Hashtable cache = new Hashtable();
	
	private PseudoPic(String url) {
		super();
		this.url = url;
		this.img = null;
	}
	
	public static PseudoPic fromUrl(String url) {
		if (cache.containsKey(url)) {
			return (PseudoPic) cache.get(url);
		} else {
			PseudoPic pic = new PseudoPic(url);
			cache.put(url, pic);
			return pic;
		}
	}
	
	public void download() throws IOException {
		if (this.img != null) {
			return;
		}
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		HttpConnection c = ( HttpConnection ) Connector.open( url );
		if (c.getResponseCode() == HttpConnection.HTTP_OK) {
			InputStream is = c.openInputStream();

			int b;
			// Just read everything
			while ((b = is.read()) != -1) {
				buf.write(b);
			}

			is.close();
		}
		c.close();
		
		byte[] data = buf.toByteArray();
		img = Image.createImage(data, 0, data.length);
	}
	
	public Image getImage() {
		return img;
	}
	
	/*
	public byte[] freeze() {
		byte[] key = url.getBytes();
		byte[] result = new byte[key.length + img.length];
		
		for (int i=0; i<key.length; i++) {
			result[i] = key[i];
		}
		for (int i=key.length; i<key.length + img.length; i++) {
			result[i] = img[ i - key.length ]; 
		}
		
		return result;
	}
	
	public static PseudoPic thaw(byte[] input) {
		int i=0;
		while (i<input.length && input[i] > 0) {
			i++;
		}
		String key = new String(input, 0, i);
		PseudoPic result = new PseudoPic(key);
		byte[] img = new byte[input.length-i];
		for (int j=0; j+i<input.length; j++) {
			img[j] = input[i+j];
		}
		result.img = img;
		
		return result;
	}
	
	public static void freezeCache() throws RecordStoreNotOpenException, RecordStoreFullException, RecordStoreException {
		RecordStore rs = RecordStore.openRecordStore("MensafuchsPictures",true);
		
		Enumeration e = cache.elements();
		while (e.hasMoreElements()) {
			DataOutputStream dos;
			
			PseudoPic p = (PseudoPic) e.nextElement();
			byte[] frozen = p.freeze();
			rs.addRecord(frozen, 0, frozen.length);
		}
	}
	*/

	
}
