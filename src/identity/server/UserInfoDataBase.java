/**
 *
 */
package identity.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jaremy
 *
 */
//public class UserInfoDataBase extends Thread
public class UserInfoDataBase 
{

	// Hashtable is synchronized already!
	// TODO? synchronize the creation of the hashlist?
	ConcurrentHashMap<UUID, UserInfo> dbHash;

	private File dbFile;
	private String dbFileName;

	@SuppressWarnings("unchecked") // ha, gotta love Java! ;)
	public UserInfoDataBase( String filename )
	{
		dbFileName = filename;
		dbHash = new ConcurrentHashMap<UUID, UserInfo>(50);
		ObjectInputStream dbStreamIn = null;
		try {
			// open file
			dbFile = (new File(dbFileName)).getAbsoluteFile();
			System.out.println("file: " + dbFile);

			// read in file and close
			// Alright Java, is this elegant? Really, is it? Come on C does better than this!
			dbStreamIn = new ObjectInputStream(new FileInputStream(dbFile));
			UserInfo[] readarray = (UserInfo[]) dbStreamIn.readUnshared();
			dbStreamIn.close();

			dbStreamIn = new ObjectInputStream(new FileInputStream(dbFile));
			readarray = (UserInfo[]) dbStreamIn.readUnshared();
			dbStreamIn.close();

			// add all the values to the HashMap
			System.out.println("\nRecreating dbHash");
			for (UserInfo user: readarray) {
				dbHash.put(user.uuid, user);
			}		     

		}
		catch (FileNotFoundException filenotfound) {
			System.err.println("No user database found.");
		}
		catch (IOException ioe) {
			System.err.println("Error in opening the db file: " + ioe);
			ioe.printStackTrace();
			System.exit(2);
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Error trying to restore ArrayList");
			e.printStackTrace();
			System.exit(3);
		}
		
		int delay = 7*1000; // delay for 5 sec.
		int period = 49*1000; // repeatTimer timer = new Timer();
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				//method to write the objects from hashtable to a file
					try {
						checkpoint();
					} catch (UserInfoException e) {
						// TODO Auto-generated catch block
						System.err.println("UserInfoException while checkpoint." +
								"File might be corrupt!");					
					}
				
			}
		}, delay, period);
		
	}
	/*
	  NOTE: might need to manually synchronize the ArrayList in order to serialize it?
	*/

	/**
	*  Returns a synchronizedList containing the UUID database.
	* @return List synchronizedList containing the UUID database
	 */
   // public ConcurrentHashMap<UUID, UserInfo> getHashUUID ()
   // {
   //    return dbHash;
   // }


	/**
	 * Returns an array of UserInfo[] type from ConcurrentHashMap
	 * @return array holding the contents of the ConcurrentHashMap
	 */
	public UserInfo[] toArray()
	{
		return dbHash.values().toArray(new UserInfo[0]);
	}


	/**
	 * checkpoint This will checkpoint the file. Need to use thread to spin this off. 
	 * @throws UserInfoException 
	 */
	synchronized public void checkpoint() throws UserInfoException
	{
		// writeout file
		// dumpy hashmap into an array then write the array.
		UserInfo[] dumparray = toArray();

		try {
			// delete file, then checkpoint it
			dbFile.delete();
			ObjectOutputStream dbStreamOut = new ObjectOutputStream( new FileOutputStream(dbFile, false) );
			dbStreamOut.writeUnshared(dumparray);
			dbStreamOut.close();
			
			//System.out.println("Checkpointed User Information database: " + dbFile);
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


		// write over old contents with the new
		// use thread to do the timing.
	}

	public UserInfo getUUID(UUID uid)
	{
		return dbHash.get(uid);
	}
	
	

	public UserInfo getUserName(String uname)
	{
		for (UserInfo ele : dbHash.values()) {
			if (ele.username.equals(uname))
				return ele;
		}
		return null;
	}

	public boolean containsUserName(String uname)
	{
		for (UserInfo ele : dbHash.values()) {
			if (ele.username.equals(uname))
				return true;
		}
		return false;
	}

	public boolean containsUUID(UUID uname)
	{
		for (UserInfo ele : dbHash.values()) {
			if (ele.uuid.equals(uname))
				return true;
		}
		return false;
	}


	public static void main(String[] args)
	{
		// TODO Test this functionality now
		UserInfoDataBase foodb = new UserInfoDataBase("defaultUserInfo.db");

		try {
			
			UserInfo testuser0 = new UserInfo("testuser00", "mypasswd0", "No Real Name");
			UserInfo testuser1 = new UserInfo("testuser10", "mypasswd1", "Bob Jon");
			UserInfo testuser2 = new UserInfo("testuser30", "mypasswd2", "Welly Nilson");
			UserInfo testuser3 = new UserInfo("testuser40", "mypasswd3", "Mark Henry");
			UserInfo testuser2a = new UserInfo("testuser40", "mypasswd3a", "Teh Hax0R!");

			System.out.println("testuser " + testuser0);
			System.out.println("testuser1 " + testuser1);
			System.out.println("testuser2 " + testuser2);
			System.out.println("testuser3 " + testuser3);
			System.out.println("foo = " + testuser0.equals(testuser1));
			foodb.dbHash.put(testuser0.uuid, testuser0);
			foodb.dbHash.put(testuser1.uuid, testuser1);
			foodb.dbHash.put(testuser2.uuid, testuser2);
			foodb.dbHash.put(testuser3.uuid, testuser3);

			System.out.println("\n\nCheckpointing1");
			foodb.checkpoint();

			System.out.println("\nTesting Reading");
			UserInfoDataBase bar = new UserInfoDataBase("test.db");
			System.out.println("bar");
			for ( UserInfo user: bar.toArray() ) {
				System.out.println("bar: " + user.uuid);
			}

			//System.out.println("\n\nCheckpointing2");
			//for (int i = 0; i < 100; ++i)
			//	foodb.checkpoint();

			System.out.println("\nTesting Reading");
			UserInfoDataBase bar2 = new UserInfoDataBase("test.db");
			System.out.println("bar2");
			for ( UserInfo user: bar2.toArray() ) {
				System.out.println("bar2: " + user.uuid);
			}

			System.out.println("Equals: " + bar2.getUserName("testuser0"));

		}
		catch (UserInfoException e) {
			// TODO Auto-generated catch block
			System.err.println("" + e);
			e.printStackTrace();
		}

//		System.exit(0);
	}

}
