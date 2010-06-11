/*
 * Created on 12.01.2008
 *
 */
package backend;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class Rating {
	private int id;
	private int score;
	private String comment;

	public Rating(int id, int score, String comment) {
		this.id = id;
		this.score = score;
		this.comment = comment;
	}
	
	private static String toHex(int n) {
		String h = "" ;
	    int r=0;
	    int nn=n ;
	    do {
	      r=nn % 16 ;
	      nn= nn / 16 ;
	      switch (r) {
	      case 10: h = "A" + h; break ;
	      case 11: h = "B" + h; break ;
	      case 12: h = "C" + h; break ;
	      case 13: h = "D" + h; break ;
	      case 14: h = "E" + h; break ;
	      case 15: h = "F" + h; break ;
	      default: h = r + h; break ;
	     }
	   } while (nn > 0) ;
	   return h ;
	}
	
	private static String urlEncode(String input) {
		StringBuffer sb = new StringBuffer();
		char[] c = input.toCharArray();
		for (int i=0; i < c.length; i++) {
			if ((c[i] >= 'a' && c[i] <= 'z') || (c[i] >= 'A' && c[i] <= 'Z' )) {
				sb.append(c[i]);
			} else{
				sb.append('%');
				sb.append(toHex(c[i]));
			}
		}
		return sb.toString();
	}
	
	public void send() throws IOException {
		String url = "http://mensafuchs.de/ratingHandler.pl?tool=mensafuchs-mobile&score-"+id+"="+score+"&comment="+urlEncode(comment);
		HttpConnection c = ( HttpConnection ) Connector.open( url );
		InputStream is = c.openInputStream();
		
		is.close();
		c.close();
	}
}
