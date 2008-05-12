package identity.distributed;

import identity.server.UserInfo;

public class DistributedHash implements Types
{
	private UserInfoDataBase userDBwrapper;
	private ConcurrentHashMap<UUID, UserInfo> userdb;
	private CalendarDBServer caldb;


	DistributedHash ()
	{
	}

	public static void main (String args[])
	{
		if (args.length != 2) {
			System.err.println("Usage: java TimeClient <serverhost> <port>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);

		try {
			Socket s = new Socket(host, port);
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();
			ObjectInputStream oin = new ObjectInputStream(in);
			Date date = (Date) oin.readObject();
			System.out.println("Time on host " + host + " is " + date);
		}
		catch (IOException e1) {
			System.out.println(e1);
		}
		catch (ClassNotFoundException e2) {
			System.out.println(e2);
		}
	}


	// methods for distributed commits
	private void sendDHM(DHmsg dhm)
	{
		// this method will send the message to the coord queue
	}

	// methods for UserInfo entries
	public UserInfo UserInfoPut (UUID uuid, UserInfo user)
	{}
	public UserInfo UserInfoGet (UUID uuid)
	{}
	public UserInfo UserInfoQuickUUID (String username)
	{}
	public UserInfo UserInfoDel (UUID uuid)
	{}

	// methods for calendar entries
	public boolean addEntry(CalendarEntry entry)
	{}

	public boolean delEntry(CalendarEntry entry)
	{}
	public boolean delEntry(UUID uuid, Integer keyid)
	{}

	public CalendarEntry getEntry(CalendarEntry entry)
	{}
	public CalendarEntry getEntry(UUID uid, Integer key)
	{}


}

