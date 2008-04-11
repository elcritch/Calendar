/**
 * 
 */
package identity.calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author jaremy
 *
 */
public class CalendarEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2165686360188303307L;
	
	/* Example Entry: 17#2006/11/4 4:55pm#public#The fun begins now#90# */
	public int id;
	public Date datetime;
	public boolean isPublic;
	public String descr;
	public int duration;
	
	public static DateFormat df = DateFormat.getDateTimeInstance();
	
	/**
	 * CalendarEntry
	 *
	 * @param  
	 * @return 
	 */
	CalendarEntry(int id, Date datetime, boolean isPublic, String descr, int duration ) {
		this.id = id;
		this.datetime = datetime;
		this.isPublic = isPublic;
		this.descr = descr;
		this.duration = duration;
		
		// need to check and make sure this is correct.
	}

	public static CalendarEntry parseStringArray( String[] p) {
		try {
			// parse the entry
			int id = Integer.parseInt(p[0]);
			Date datetime = df.parse(p[1]);
			boolean isPublic = Boolean.parseBoolean(p[2]);
			String descr = p[3];
			int duration = Integer.parseInt(p[4]);
			
			return new CalendarEntry(id,datetime,isPublic,descr,duration);
		} catch (Exception e) {
			return null;
		}
		
		// need to check and make sure this is correct.
	}

	
}
