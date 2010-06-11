/*
 * Created on 21.01.2008
 *
 */
package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;

public class MultipartPostRequest {
	private static final String BOUNDARY = "MENSAFUCHS";
	private static final String CRLF = "\r\n";
	
	private Hashtable fields;
	private Hashtable files;
	private String host;
	private String ressource;
	
	public MultipartPostRequest(String host, String ressource) {
		this.host = host;
		this.ressource = ressource;
		fields = new Hashtable();
		files = new Hashtable();
	}
	
	public void addField(String key, String value) {
		fields.put(key, value);
	}
	public void addFile(String key, FileConnection fc) {
		files.put(key, fc);
	}
	
	private void writeBytes(byte[] b, int n, OutputStream os) throws IOException {
		for (int i=0; i<b.length && i<n; i++) {
			os.write( b[i] );
		}		
	}
	private void writeBytes(byte[] b, OutputStream os) throws IOException {
		writeBytes(b, b.length, os);
	}
	
	private void sendFile(String key, FileConnection fc, OutputStream dos) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("--"); sb.append(BOUNDARY); sb.append(CRLF);
		sb.append("content-disposition: form-data; name=\""); sb.append(key); sb.append("\"; filename=\""); sb.append(fc.getName()); sb.append("\""); sb.append(CRLF);
		sb.append(CRLF);
		writeBytes(sb.toString().getBytes(), dos);
		
		InputStream dis = fc.openInputStream();
		int n;
		byte[] buf = new byte[1024];
		while ( (n = dis.read(buf)) > 0) {
			writeBytes(buf, n, dos);
		}
		dis.close();
		writeBytes(CRLF.getBytes(), dos);
	}
	
	private String constructField(String key, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("--"); sb.append(BOUNDARY); sb.append(CRLF);
		sb.append("content-disposition: form-data; name=\""); sb.append(key); sb.append("\""); sb.append(CRLF);
		sb.append(CRLF);
		sb.append(value); sb.append(CRLF);
		return sb.toString();
	}
	
	private void sendBody(OutputStream dos) throws IOException {
		Enumeration en = fields.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			byte[] field = constructField(key, (String) fields.get(key)).getBytes();
			writeBytes(field, dos);
		}

		Enumeration enf = files.keys();
		while (enf.hasMoreElements()) {
			String key = (String) enf.nextElement();
			FileConnection fc = (FileConnection) files.get(key);
			sendFile(key, fc, dos);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("--"); sb.append(BOUNDARY); sb.append("--"); sb.append(CRLF);
		writeBytes( sb.toString().getBytes(), dos );
	}
	
	private void sendHeader(OutputStream dos, int bodysize) throws IOException {
		StringBuffer header = new StringBuffer();
		header.append("POST "+ressource+" HTTP/1.1"+CRLF);
		header.append("Host: "+host+CRLF);
		header.append("content-type: multipart/form-data; boundary="+BOUNDARY+CRLF);
		header.append("content-legth: "+bodysize+CRLF);
		header.append(CRLF);
		dos.write(header.toString().getBytes());
	}	
	
	public String transmit() throws IOException {
		StringBuffer reply = new StringBuffer();
		
		StreamConnection sc = (StreamConnection) Connector.open("socket://"+host+":80");
		// h.setRequestProperty("content-type", "multipart/form-data; boundary="+BOUNDARY);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		sendBody(bos);
		byte[] body = bos.toByteArray(); 
		
		OutputStream dos = sc.openOutputStream();
		sendHeader(dos, body.length);
		dos.write(body);
		
		InputStream is  = sc.openDataInputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = is.read(b)) > 0) {
			reply.append(new String(b, 0, n));
		}
		dos.close();
		is.close();
		sc.close();
		return reply.toString();
	}

}
