/**
 * 
 */
package identity.calendar;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author jaremy
 *
 */
public class CalendarEntry {

	/* Example Entry: 17#2006/11/4 4:55pm#public#The fun begins now#90# */
	public int id;
	public Date datetime;
	public boolean isPublic;
	public String descr;
	public int duration;
	
	
	/**
	 * CalendarEntry
	 *
	 * @param  
	 * @return 
	 */
	public void CalendarEntry(int id, Date datetime, boolean isPublic, String descr, int duration ) {
		this.id = id;
		this.datetime = datetime;
		this.isPublic = isPublic;
		this.descr = descr;
		this.duration = duration;
		
		// need to check and make sure this is correct.
	}

	
	
}
