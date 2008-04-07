/**
 * 
 */
package identity.calendar;

import java.text.DateFormat;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jaremy
 * Use this class to create a calendar database for both server's and clients. The type
 * should be specified
 */
public class CalendarDB {
	
	/**
	 * This contains a generic flat hashmap that can contain either server or client event 
	 * types. The flat database model allows this class to be used for both client/server
	 * applications with generality. 
	 */
	public ConcurrentHashMap<UUID, CalendarEntry> db;
	
	private DateFormat datefmt;
	// Constructor!

	/**
	 * @param db
	 * @param datefmt
	 */
	public CalendarDB( ) {
		// create new db
		
		// datefmt code
		
		// shouldn't I steal this from the UserInfoDataBase code?
	}
	
	

	
}
