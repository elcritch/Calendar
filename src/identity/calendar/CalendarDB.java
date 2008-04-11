/**
 * 
 */
package identity.calendar;

import identity.server.UserInfoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	 * applications with generality. Overwrite this with correct generics type.
	 */
	public ConcurrentHashMap<Integer, CalendarEntry> db;
	
	public DateFormat datefmt;
	public UUID useruuid;
	public String username;
	
	private String dbFileName;
    private File dbFile;
    private boolean isServer = false;
    /**
	 * @param datefmt
	 */
	public CalendarDB( ) {		
	}
	
	public CalendarDB( String filename ) {
		db = new ConcurrentHashMap<Integer, CalendarEntry>(50);
		
		// read in entries
		parseFile(filename);
		
		
		System.out.println("Spinning off timer?");
		//CheckPointer checkpointer = new CheckPointer(this,60);
		//new Thread(checkpointer).start();
	}
	
	public void parseFile(String filename) {
		dbFileName = filename;
		String line;
		String [] parts;
		
		BufferedReader dbStreamIn = null;
		try {
			// open file
			dbFile = (new File(dbFileName)).getAbsoluteFile();
			System.out.println(" Calendar file: " + dbFile);

			// read in file and close
			// Alright Java, is this elegant? Really, is it? Come on C does better than this!
			dbStreamIn = new BufferedReader(new FileReader(dbFileName));
			
			// read the first line then read all the events into the hashmap
			line = dbStreamIn.readLine();
			if ( (parts = line.split("#")).length == 2 ) {
				useruuid = UUID.fromString(parts[0]); 
				username = parts[1];
			} else {
				System.err.println("Incorrect file format: "+dbFile);
			}
			
			// add all the values to the HashMap
			System.out.println("\nRecreating CalendarHash");
			CalendarEntry entry;
			
			try {
			while ( (line = dbStreamIn.readLine()) != null ) {
				entry = CalendarEntry.parseStringArray(line.split("#"), isServer);
				addEntry(entry);
				System.out.println("create: " + entry);
			}
			} catch (IllegalArgumentException e) {
				System.err.println("Invalid key while recreating list");
			}
			
			dbStreamIn.close();

		     
			// TODO! use thread to spin off the timing for checkpointing.

		}
		catch (FileNotFoundException filenotfound) {
			System.err.println("No File found. Oh Well, hope your users don't mind!");
		}
		catch (IOException ioe) {
			System.err.println("Error in opening the db file: " + ioe);
			ioe.printStackTrace();
			System.exit(2);
		}
		
	}
	
	/**
	 * This method wraps adding objects to the hashmap in a generic fashion. Overwrite this 
	 * to change hashmap key types.
	 * @param entry add this entry to the hashmap
	 * @throws IllegalArgumentException
	 */
	public void addEntry(CalendarEntry entry) throws IllegalArgumentException {
		Integer key = entry.id;
		if ( !db.containsKey(key))
			db.put(key, entry);
		else
			throw new IllegalArgumentException("Key already in database");
	}

	/**
	 * This method wraps deleting objects from the HashMap in a generic fashion. Overwrite this 
	 * to change HashMap key types for a server class.
	 * @param delete this entry from the hashmap
	 * @throws IllegalArgumentException
	 */
	public boolean delEntry(CalendarEntry entry) {
		return delEntry(entry.id);
	}
	
	public boolean delEntry(Integer id) {
		if (db.remove(id)!=null)
			return true;
		else 
			return false;
	}
	
	/**
	*  Returns a synchronizedList containing the UUID database.
	* @return List synchronizedList containing the UUID database
	 */
	public ConcurrentHashMap<Integer, CalendarEntry> getHashUUID()
	{
		return db;
	}


	/**
	 * Returns an array of UserInfo[] type from ConcurrentHashMap
	 * @return array holding the contents of the ConcurrentHashMap
	 */
	public CalendarEntry[] toArray()
	{
		return db.values().toArray(new CalendarEntry[0]);
	}


	/**
	 * checkpoint This will checkpoint the file. Need to use thread to spin this off. 
	 * @throws UserInfoException 
	 */
	synchronized public void writeFile()
	{
		// writeout file
		BufferedWriter dbStreamOut;
		// dumpy hashmap into an array then write the array.
		CalendarEntry[] dumparray = toArray();

		try {
			dbFile.delete();
			dbStreamOut = new BufferedWriter( new FileWriter(dbFile,false) );
			
			for (CalendarEntry entry : dumparray)
				dbStreamOut.write(entry.toString()+"\n");
			
			dbStreamOut.close();
			System.out.println("Checkpointed UUID ArrayList File: " + dbFile);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error in checkpointing. " + e);
			e.printStackTrace();
		}
	}

	public boolean contains(Integer eid)
	{
		return db.containsKey(eid);
	}


	public static void main(String[] args)
	{

		System.exit(0);
	}
	

	
}
