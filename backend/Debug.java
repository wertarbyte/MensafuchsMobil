/*
 * Created on 27.01.2008
 *
 */
package backend;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.OutputConnection;

public class Debug {
	private static boolean enabled = false;
	private static String name = "AT1337";
	private static OutputStream port;
	
	private static void init() {
		port = null;
		if (! enabled) {
			return;
		}
		try {
			OutputConnection oc = (OutputConnection) Connector.open("comm:"+name+";baudrate=9600", Connector.WRITE);
			port = oc.openOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void println(String message) {
		print(message+"\r\n");
	}
	
	public static void print(String message) {
		System.err.println(message);
		if (port == null) {
			init();
		}
		if (port != null) {
			try {
				port.write((message+'\n').getBytes());
				port.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
