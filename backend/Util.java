/*
 * Created on 12.01.2008
 *
 */
package backend;

import java.util.Vector;

public class Util {
	public static String[] split(String str, char ch){
		  Vector v = new Vector();
		  while(str.indexOf(ch) != -1){
		    String tmp = str.substring(0, str.indexOf(ch)).trim();
		    // if(tmp.length()>0)
		    	v.addElement(tmp);
		    str = str.substring(str.indexOf(ch)+1,str.length());
		  }
		  if (str.length() > 0) {
			  v.addElement(str);
		  }
		  
		  String[] returned = new String[v.size()];
		  for (int i=0;i<v.size();i ++) {
		    returned[i]=(String)v.elementAt(i);
		  }
		  return returned;
	}
}
