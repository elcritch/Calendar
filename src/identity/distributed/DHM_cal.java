package identity.distributed;

import identity.calendar.CalendarEntry;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version 0.1
 */
public final class DHM_cal extends DHM {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4150261328407458673L;
	
	protected Integer entryid;
    protected CalendarEntry entry;
    
	public DHM_cal(int type, CalendarEntry entry) {
		super(type);
		this.entry = entry;
		this.entryid = entry.id;
	}
}
