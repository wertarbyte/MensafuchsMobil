/*
 * Created on 20.01.2009
 *
 */
package backend;

import java.util.Hashtable;

public class Site {
	private static Hashtable sites;
	
	private int siteId;
	private String shortName;
	private String longName;
	
	public static Site getSite(int siteId, String shortName, String longName) {
		if (sites == null) sites = new Hashtable();
		
		Integer id = new Integer(siteId);
		if (sites.containsKey(id)) {
			return (Site) sites.get(id);
		} else {
			Site s = new Site(siteId, shortName, longName);
			sites.put(id, s);
			return s;
		}
	}

	private Site(int siteId, String shortName, String longName) {
		this.siteId = siteId;
		this.shortName = shortName;
		this.longName = longName;

	}

	public String getLongName() {
		return longName;
	}

	public String getShortName() {
		return shortName;
	}

	public int getSiteId() {
		return siteId;
	}
	
	public boolean equals(Object o) {
		Site s = (Site) o;
		
		return s.siteId == this.siteId;
	}

}
