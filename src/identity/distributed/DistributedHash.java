package identity.distributed;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import identity.calendar.CalendarEntry;
import identity.election.PrintColor;
import identity.server.SharedData;
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
	protected void sendAll(DHM dhm) throws SocketTimeoutException
	{
		// this method will send the message to the coord queue
		InetAddress[] servers = share.servers.toArray();
		
		// connect to each server in the list, including our own
		Sender tmp = null;
		for (InetAddress server : servers) {
		   // we don't need to do error checking here, the server will take care of that. 
		   tmp = new Sender(server,dhm);
		   tmp.start();
		}
		// sleep for a bit. we could join all threads, but this should give us enough delay?
		try {
			tmp.join(200);
		} catch (InterruptedException e) {
			// ignore error
		}
		
		// send the VOTE_BEGIN message to the coordinator
		/* note: this will result in a socketimeout error if unsucessfull */
		sendDHM(share.clock.getCoordInetAddress(), new DHM_vote(VOTE_BEGIN));
		
	}
	
	protected void sendDHM(InetAddress host, DHM msg) throws SocketTimeoutException {
      try {
			Socket s = new Socket(host, port);
			OutputStream out = s.getOutputStream();
			ObjectOutputStream stream_out = new ObjectOutputStream(out);
			stream_out.writeUnshared(msg);
			stream_out.close();
			out.close();
		}
		catch (IOException e1) {
		    PrintColor.red("error: sending DHM: "+msg+" to: "+host);
			System.out.println(e1);
		}	   
	}
	
	class Sender extends Thread {
	   DHM msg;
	   InetAddress host;
	   Sender(InetAddress host, DHM msg) { 
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
	{
		return null;
	}
	public UserInfo UserInfoGet (UUID uuid)
	{
		return null;}
	public UserInfo UserInfoQuickUUID (String username)
	{
		return null;}
	public UserInfo UserInfoDel (UUID uuid)
	{
		return null;
	}

	// methods for calendar entries
	public boolean addEntry(CalendarEntry entry)
	{
		return false;
	}

	public boolean delEntry(CalendarEntry entry)
	{
		return false;
	}
	
	public boolean delEntry(UUID uuid, Integer keyid)
	{
		return false;
	}

	public CalendarEntry getEntry(CalendarEntry entry)
	{
		return entry;
	}
	public CalendarEntry getEntry(UUID uid, Integer key)
	{
		return null;
	}
	
	
	
	// ----------------
	
	public static void main (String args[])
	{

	}

}
