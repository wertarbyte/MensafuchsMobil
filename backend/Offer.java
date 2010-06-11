package backend;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/*
 * Created on 20.12.2007
 *
 */

public class Offer {
	private int id;
	private Mensa mensa;
	private Day date;
	private String description;
	private int mealId;
	private boolean vegetarian;
	private boolean newMeal;
	private int votes;
	private double rating;
	private boolean top;
	private Hashtable groups;
	private Hashtable photos;

	public Offer(int id, Day date, Mensa mensa, String description, int mealId, boolean vegetarian) {
		this.id = id;
		this.date = date;
		this.mensa = mensa;
		this.description = description;
		this.mealId = mealId;
		this.vegetarian = vegetarian;
		this.rating = 0;
		this.votes = 0;
		this.top = false;
		this.groups = new Hashtable();
		this.photos = new Hashtable();
		this.newMeal = false;
	}
	
	public static Offer fromCSV (String line) {
		
		String[] items = Util.split(line, ';');
		
		int offerId = Integer.parseInt( items[0] );
		Day d = Day.fromString( items[1] );
		String mensaId = items[2];
		int mId = Integer.parseInt(mensaId);
		String mensaShort = items[3];
		String mensaLong = items[4];
		
		int sId = Integer.parseInt(items[15]);
		String siteShort = items[16];
		String siteLong = items[17];
		
		Site s = Site.getSite(sId, siteShort, siteLong);
		Mensa m = Mensa.getMensa(mId, mensaLong, mensaShort, s);
		
		int mealId = Integer.parseInt(items[5]);
		String mealDescription = items[6];
		String veg = items[7];
		// String npictures = items[8];
		String[] pictureIds = Util.split(items[9], ',');
		
		String mealRating = items[10];
		String nvotes = items[11];
		String newMeal = items[12];
		String[] groups = Util.split(items[13], ',');
		String top = items[14];
		
		Offer result = new Offer(offerId, d, m, mealDescription, mealId, veg.equals("1"));
		if (mealRating.length() > 0) result.rating = Double.parseDouble(mealRating);
		if (nvotes.length() > 0) result.votes = Integer.parseInt(nvotes);
		result.newMeal = (newMeal.equals("1"));
		result.top = (top.equals("1"));
		
		// Add photos
		for (int i=0; i<pictureIds.length; i++) {
			Integer id = new Integer(Integer.parseInt(pictureIds[i]));
			PseudoPic pic = PseudoPic.fromUrl("http://mensafuchs.de/picture-small-"+id.toString());
			result.photos.put(id, pic);
		}
		// and groups
		for (int i=0; i< groups.length; i++) {
			result.addToGroup(groups[i]);
		}
		
		return result;
	}
	
	public void addToGroup (String group) {
		groups.put(group, group);
	}
	
	public boolean isInGroup(String group) {
		return groups.contains(group);
	}
	
	public int getId() {
		return id;
	}

	public Day getDay() {
		return date;
	}
	
	public String getDescription() {
		return description;
	}

	public Mensa getMensa() {
		return mensa;
	}
	
	public double getRating() {
		return rating;
	}

	public String freeze() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId()); sb.append(';');
		sb.append(getDay().toString());
		sb.append( ';' );
		
		sb.append(mensa.getId()); sb.append(';');
		sb.append(mensa.getShortName()); sb.append(';');
		sb.append(mensa.getLongName()); sb.append(';');
		sb.append(mealId); sb.append(';');
		sb.append(description); sb.append(';');
		/* veg */ sb.append(';');
		/* number of pictures */ sb.append(';');
		/* picture ids */
		Enumeration pids = photos.keys();
		while (pids.hasMoreElements()) {
			sb.append(pids.nextElement());
			sb.append(',');
		}
		sb.append(';');
		sb.append(rating); sb.append(';');
		sb.append(votes); sb.append(';');
		sb.append(newMeal ? 1 : 0); sb.append(';');
		/* groups */
		Enumeration en = groups.keys();
		while (en.hasMoreElements()) {
			sb.append(en.nextElement());
			sb.append(',');
		}
		sb.append(';');
		sb.append(top ? 1 : 0);
		sb.append(';');
		sb.append(mensa.getSite().getSiteId()); sb.append(';');
		sb.append(mensa.getSite().getShortName()); sb.append(';');
		sb.append(mensa.getSite().getLongName()); sb.append(';');
		return sb.toString();
	}

	public boolean isVegetarian() {
		return vegetarian;
	}
	
	public void rate(int score, String comment) {
		Rating r = new Rating(id, score, comment);
		try {
			r.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void uploadPicture(String url) {
		Upload u = new Upload(url, this);
		try {
			u.transmit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PseudoPic[] getPictures() {
		Vector images = new Vector();
		
		Enumeration en = photos.elements();
		while (en.hasMoreElements()) {
			images.addElement(en.nextElement());
		}
		PseudoPic[] res = new PseudoPic[images.size()];
		for (int i=0; i < images.size(); i++) {
			res[i] = (PseudoPic) images.elementAt(i);
		}
		return res;
	}

	public boolean isNewMeal() {
		return newMeal;
	}

	public int getVotes() {
		return votes;
	}
	
	public int compareTo(Object ob) {
		Offer o = (Offer) ob;
		
		// Date
		if (! getDay().equals(o.getDay())) {
			return getDay().compareTo(o.getDay());
		} else {
			// same day
			if (! getMensa().equals(o.getMensa())) {
				return getMensa().compareTo(o.getMensa());
			} else {
				// even the same mensa!
				// return description.compareTo(o.description);
				if (mealId > o.mealId) {
					return 1;
				} else if (mealId < o.mealId) {
					return -1;
				}
				return 0;
			}
		}
	}
	
}
