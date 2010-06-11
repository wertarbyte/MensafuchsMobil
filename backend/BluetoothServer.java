/*
 * Created on 17.01.2008
 *
 */
package backend;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer {
	private BluetoothServer me;
	protected static final String BT_PROTOCOL = "btspp";
	protected static final String BT_ID = "1234";
	
	private StreamConnectionNotifier scn;
	
	private boolean listening;
	
	private BluetoothServer() {
		listening = false;
	}
	
	public BluetoothServer getInstance() {
		if (me == null) {
			me = new BluetoothServer();
		}
		return me;
	}
	
	public boolean isListening() {
		return listening;
	}
	
	public void setListening(boolean f) throws IOException {
		if (f == listening) {
			return;
		}

		if (f) {
			// start listening
			scn = (StreamConnectionNotifier)Connector.open( BT_PROTOCOL + "://localhost:" + BT_ID);
		} else {
			// stop listening
			scn.close();
			scn = null;
			listening = false;
		}
	}

}
