package identity.distributed;

import identity.calendar.CalendarEntry;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version 0.1
 */
public final class DHM_cal extends DHmsg {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -4150261328407458673L;
	
	protected Integer entryid;
    protected CalendarEntry entry;
    
	public DHM_cal(int vote_msg, CalendarEntry entry) {
		super(vote_msg);
		this.entry = entry;
	}
}
