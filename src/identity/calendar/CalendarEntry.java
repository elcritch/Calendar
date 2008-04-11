/**
 * 
 */
package identity.calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author jaremy
 *
 */
public class CalendarEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2165686360188303307L;
	
	/* Example Entry: 
	 * 17#2006/11/4 4:55pm#public#The fun begins now#90# 
	 * 8953a618-3c1e-4bd8-a80a-0fe19f926394#17#2006/11/4 4:55pm#public#The fun begins now#90#
	 */
	public UUID uuid;
	public Integer id;
	public Date datetime;
	public boolean isPublic;
	public String descr;
	public Integer duration;
	
	public static DateFormat df = DateFormat.getDateTimeInstance();
	
	CalendarEntry() {
		this.uuid = null;
		this.id = null;
		this.datetime = null;
		this.isPublic = false;
		this.descr = null;
		this.duration = null;
	}
	
	/**
	 * CalendarEntry
	 *
	 * @param  
	 * @return 
	 */
	CalendarEntry(UUID uuid, int id, Date datetime, boolean isPublic, String descr, int duration ) {
		this.uuid = uuid;
		this.id = id;
		this.datetime = datetime;
		this.isPublic = isPublic;
		this.descr = descr;
		this.duration = duration;
		// need to check and make sure this is correct.
	}

	public static CalendarEntry parseStringArray( String[] p, boolean hasUserId) {
		int i = 0;
		UUID uuid = null;		
		try {
			// optional set UUID
			if (hasUserId)
				uuid = UUID.fromString(p[i++]);
			// parse the entry
			int id = Integer.parseInt(p[i++]);
			Date datetime = df.parse(p[i++]);
			boolean isPublic = Boolean.parseBoolean(p[i++]);
			String descr = p[i++];
			int duration = Integer.parseInt(p[i++]);
			
			return new CalendarEntry(uuid, id,datetime,isPublic,descr,duration);
		} catch (Exception e) {
			return null;
		}
		
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String toString() {
		String str;
		str = (uuid == null) ? null : uuid.toString()+"#";
		str += id.toString() + "#";
		str += datetime.toString() + "#";
		str += (isPublic) ? "public#" : "private#";
		str += descr.toString() + "#";
		str += duration.toString() + "#";
		
		return str;
	}
	
}
