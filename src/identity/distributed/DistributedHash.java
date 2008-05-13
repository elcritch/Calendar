package identity.distributed;

import identity.server.UserInfo;

public class DistributedHash implements Types
{
   private SharedData share;
   
	DistributedHash (SharedData share)
	{
	   this.share = share;
	}

	// methods for distributed commits
	protected void sendAll(DHmsg dhm)
	{
		// this method will send the message to the coord queue
		InetAddress[] servers = share.servers.getArray();
		
		// connect to each server in the list, including our own
		for (InetAddress server : servers) {
		   // we don't need to do error checking here, the server will take care of that. 
		   (new Server(server,dhm)).start();
		}
	}
	
	protected void sendDHM(InetAddress host, DHmsg dhm) {
      try {
			Socket s = new Socket(host, port);
			OutputStream out = s.getOutputStream();
			ObjectOutputStream outObj = new ObjectOutputStream(out);
			outObj.writeUnsharedObject(msg);
			outObj.close();
			out.close();
		}
		catch (IOException e1) {
		   PrintColor.red("sending DHM: "+msg+" to: "+host);
			System.out.println(e1);
		}	   
	}
	
	class Sender extends Thread {
	   DHmsg msg;
	   InetAddress host;
	   Sender(InetAddress host, DHmsg msg) { 
	      this.msg = msg;
	      this.host = host;
	   }
	   public void run() {
	      try {
   			Socket s = new Socket(host, port);
   			OutputStream out = s.getOutputStream();
   			ObjectOutputStream outObj = new ObjectOutputStream(out);
   			outObj.writeUnsharedObject(msg);
   			outObj.close();
   			out.close();
   		}
   		catch (IOException e1) {
   		   PrintColor.red("sending DHM: "+msg+" to: "+host);
   			System.out.println(e1);
   		}
	   } 	      
	}


   /* ---------------------------------------------------------------------------------- */
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
	
	
	
	// ----------------
	
	public static void main (String args[])
	{

	}

}
