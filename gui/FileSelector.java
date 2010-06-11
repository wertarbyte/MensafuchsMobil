/*
 * Created on 15.01.2008
 *
 */
package gui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.List;

public class FileSelector extends List {
	private Vector files;
	
	private Stack path;
	
	public FileSelector() {
		super("Datei auswählen", ChoiceGroup.IMPLICIT);
		path = new Stack();
		files = new Vector();
	}
	
	public void refresh() {
		files.removeAllElements();
		deleteAll();
		setTitle(getPath());
		
		if (path.isEmpty()) {
			Enumeration drives = FileSystemRegistry.listRoots();
			while(drives.hasMoreElements()) {
				String drive = (String) drives.nextElement();
				append(drive, null);
				files.addElement("file:///"+drive);
			}
		} else {
			try {
				FileConnection fc = (FileConnection) Connector.open(getPath(), Connector.READ);
				Enumeration en = fc.list("*", false);
				while (en.hasMoreElements()) {
					String f = (String) en.nextElement();
					int l = f.length();
					// filter directories and .csv files
					if (f.substring(l-1, l).equals("/") || f.substring(l-4, l).equals(".csv")) {
						files.addElement(f);
						append(f, null);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
		
	public String getPath() {
		StringBuffer sb = new StringBuffer();
		Enumeration en = path.elements();
		while (en.hasMoreElements()) {
			String next = (String) en.nextElement();
			sb.append( next );
		}
		return sb.toString();
	}


	public String getSelectedFile() {
		if (getSelectedIndex() >= 0) {
			return ( (String) files.elementAt( getSelectedIndex() ) );
		}
		return null;
	}
	
	public void goUp() {
		if (! path.isEmpty()) {
			System.err.println("pop :-)");
			path.pop();
			refresh();
		}
	}
	
	public boolean isTopLevel() {
		return path.isEmpty();
	}

	public boolean fileHasBeenSelected() {
		String file = getSelectedFile();

		if (file.equals("") || file == null) {
			return false;
		}
		FileConnection fc;
		try {
			fc = (FileConnection) Connector.open(getPath()+file, Connector.READ);
			if (fc.isDirectory()) {
				System.err.println("Pushing "+file);
				path.push(file);
				refresh();
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
