/*
 * Created on 12.01.2008
 *
 */
package backend;

import java.util.Hashtable;


public class Mensa {
	
	private static Hashtable mensas = null;
	
	public static Mensa getMensa(int id, String longName, String shortName, Site s) {
		if (mensas == null)  mensas = new Hashtable();

		if (mensas.containsKey( new Integer(id) )) {
			return (Mensa) mensas.get(new Integer(id));
		} else {
			Mensa m = new Mensa(id, longName, shortName, s);
			mensas.put(new Integer(id), m);
			return m;
		}
	}
	
	private int id;
	private String longName;
	private String shortName;
	private Site site;
	
	private Mensa(int id, String longName, String shortName, Site s) {
		this.id = id;
		this.longName = longName;
		this.shortName = shortName;
		this.site = s;
	}

	public int getId() {
		return id;
	}

	public String getLongName() {
		return longName;
	}

	public String getShortName() {
		return shortName;
	}
	
	public Site getSite() {
		return site;
	}
	
	public boolean equals(Object o) {
		Mensa m = (Mensa) o;
		return m.getId() == getId();
	}
	
	public int compareTo(Object o) {
		Mensa m = (Mensa) o;
		if (equals(m)) {
			return 0;
		} else {
			if (getId() > m.getId()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
}
