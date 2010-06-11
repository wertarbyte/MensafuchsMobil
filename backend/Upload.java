/*
 * Created on 15.01.2008
 *
 */
package backend;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Upload {
	private String filename;
	private Offer offer;

	protected Upload(String filename, Offer offer) {
		super();
		this.filename = filename;
		this.offer = offer;
	}
	
	private void doPost(FileConnection fc) throws IOException {
		MultipartPostRequest mpr = new MultipartPostRequest("mensafuchs.de", "/uploadHandler.pl");
		mpr.addField("upload_photo", "y");
		mpr.addField("offer_id", ""+offer.getId());
		mpr.addFile("picture_data", fc);
		mpr.transmit();
	}
	
	public void transmit() throws IOException {
		// open file
		FileConnection fc = (FileConnection) Connector.open(filename, Connector.READ);

		doPost(fc);
		
		fc.close();
	}
}
