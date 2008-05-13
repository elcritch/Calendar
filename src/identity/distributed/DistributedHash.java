package identity.distributed;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import identity.server.UserInfo;

public class DistributedHash implements Types
{
   private SharedData share;
   int port = 5294;
   
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
		Sender tmp;
		for (InetAddress server : servers) {
		   // we don't need to do error checking here, the server will take care of that. 
		   tmp = new Sender(server,dhm);
		   tmp.start();
		}
		// sleep for a bit. we could join all threads, but this should give us enough delay?
		tmp.join(200);
		
		// send the VOTE_BEGIN message to the coordinator
		sendDHM(share.coordinator, new DHM(VOTE_BEGIN, dhm.lamport));
		
	}
	
	protected void sendDHM(InetAddress host, DHmsg dhm) throws SocketTimeoutException {
      try {
			Socket s = new Socket(host, port);
			OutputStream out = s.getOutputStream();
			ObjectOutputStream outObj = new ObjectOutputStream(out);
			outObj.writeUnsharedObject(msg);
			outObj.close();
			out.close();
		}
		catch (IOException e1) {
		   PrintColor.red("error: sending DHM: "+msg+" to: "+host);
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
   			outObj.writeUnshared(msg);
   			outObj.close();
   			out.close();
   			thread_exit();
   		}
   		catch (IOException e1) {
   		   PrintColor.red("error: sending DHM: "+msg+" to: "+host);
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
