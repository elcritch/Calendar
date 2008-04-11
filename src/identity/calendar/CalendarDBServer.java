/**
 * 
 */
package identity.calendar;

import java.security.InvalidParameterException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jaremy
 *
 */
public class CalendarDBServer extends CalendarDB {
	public ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, CalendarEntry>> db;
    private boolean isServer = true;
	/**
	 * @param filename
	 * @param isServer
	 */
	public CalendarDBServer(String filename) {
		db = new ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, CalendarEntry>>(50);
		
		// read in entries
		parseFile(filename);
		
		System.out.println("Spinning off timer?");
		//CheckPointer checkpointer = new CheckPointer(this,60);
		//new Thread(checkpointer).start();
	}
	
	public void addEntry(CalendarEntry entry) throws IllegalArgumentException {
		UUID key = entry.uuid;
		Integer keyid = entry.id;
		ConcurrentHashMap<Integer, CalendarEntry> userdb;
		
		// if the uuid doesn't exist in the database, create a new HashMap to add the entry
		db.putIfAbsent(key, new ConcurrentHashMap<Integer, CalendarEntry>(20));
		userdb = db.get(key);
		if ( !db.containsKey(keyid))
			userdb.put(keyid, entry);
		else
			throw new IllegalArgumentException("Key already in database");	
	}
	

	/**
	 * This method wraps deleting objects from the HashMap in a generic fashion. Overwrite this 
	 * to change HashMap key types for a server class.
	 * @param delete this entry from the hashmap
	 * @throws IllegalArgumentException
	 */
	public boolean delEntry(CalendarEntry entry) throws InvalidParameterException {
		UUID uuid = entry.uuid;
		Integer keyid = entry.id;
		if (uuid == null || keyid == null ) 
			throw new InvalidParameterException("cannot delete entry w");
		ConcurrentHashMap<Integer, CalendarEntry> userdb;
		
		if ( (userdb = db.get(uuid)) == null )
			return false;
		else {
			if (userdb.remove(keyid)==null)
				return false;
			else return true;
		}
	}

}
