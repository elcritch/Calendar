package identity.distributed;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version $Rev$
 */

import java.io.*;
import java.net.*;


public class DistributedHashServer
{
	private int port;
	ServerSocket ss;

   // system data
   SharedData share;
   
   // storage databases
	private UserInfoDataBase userdb;
	private CalendarDBServer caldb;

   // queue databases
	private ConcurrentHashMap<Lamport, DHmsg> queue;
	
	public DistributedHashServer(SharedData share, int port)
	{
	   this.share = share;
	   this.port = port;
	   
		try {
		   // use the IP address as a "tmp directory"
		   String ip_addr = InetAddress.getLocalHost().toString();
		   File dir = new File("conf/"+ip_addr);
		   dir.mkdir();
		   // better way to do this?
		   userDBwrapper = new UserInfoDataBase(dir.getFileName()+"/defaultUserInfo.db");
		   
   		userdb = userDBwrapper.dbHash;
   		caldb = new CalendarDBServer(dir.getFileName()+"/CALENDAR");
   		
			ss = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println(e);
		}
	}

	public void run()
	{
		Socket client;
		try {
			while (true) {
				client = ss.accept();
				System.out.println("Received connect from " + client.getInetAddress());
				new ServerConnection(client).start();
			}
		}
		catch  (IOException e) {
			System.err.println(e);
		}

	}

	// main method
	public static void main (String args[])
	{
		server.start();
	}

}

class ServerConnection extends Thread
{
	Socket client;
	ObjectInputStream stream_in;
	ObjectOutputStream stream_out;
	
	ServerConnection (Socket client) throws SocketException
	{
		this.client = client;
		setPriority(NORM_PRIORITY - 1);
		System.out.println("Created thread " + this.getName());
	}

   /* ------------------------------------------------------------------------------- */
   /**
      Code to process 2 stage voting commit
      this section should include code to receive vote messages for both the queue and the databases 
      
      Terminology: master is the coordinator server. every "server" is a client and server. In this case we
         will use both.
         
      Basic sketch message queue:
         - every servlet has a message queue containing both user and calendar database DHmsg's.
         - any client can do a "sendAll" to send their message to all the other server's queues.
         - once a servlet has sent their message to all the servers, including the master server and itself.
            it then must send a "commit msgid from queue" message to the master server
            (the connection to the client will be maintained during this?
            this could be used to throw a timeout exception cancelling the process if it takes too long. )
            
         - the master server upon receiving a "vote_begin msgid" message will send a 
            "vote_request msgid" to all servers and setup a counting sempahore and wait for it. 
            at this point the master will also check for a checkpointing process that is going on. 
         - when a server gets a "vote_request msgid" from the master, it checks to see if it has this msgid.
            1. if it does then it will respond back with a "vote_committ msgid"
            2. else it will respond with a "get_msg msgid" to the server and wait for the msg. if it gets it 
                  will send "vote_committ msgid", repeating if necessary
         - once all the servers respond then the semaphore will be released, then a non-locking
            "do_commit msgid" will be sent
         - all servers once receiving a "do_commit msgid" will put the file in the appropriate hashtable.
         
      Basic checkpointing. 
         - checkpointing will be controlled by the master using a timer thread. 
            1. a "checkpoint_request" message will be sent to all.
            2. a lock will be set which will stop any new voting processes
         - all processess will respond with a "checkpoint_vote hashCode" with the hashcode being the unique state
            of each of it hashtables.
            a lock will be set locking any new commits to the hashtables.
            the servers will wait for a "checkpoint_commit" or "checkpoint_abort"
         - the master will respond back with the appropriate message depending on wether all the hashCode's agrees.
         
         
   */
   
   private void process(DHM_vote vote) {
      try {
         switch (vote.type) {
            case   VOTE_BEGIN: 
               performVoteBegin(vote);
               break;
            case VOTE_REQUEST: 
               break;
            case  VOTE_COMMIT: 
               break;
            case      GET_MSG: 
               break;
            case    DO_COMMIT: 
               break;
            default 
         }
      catch (ProcessException pe) {
         PrintColor.yellow("while processing vote message of type: "+vote.type)
         PrintColor.yellow(pe);
      }
   }

   private void process(DHM_checkpoint chkpnt);

   private void process(DHM msg) {
      PrintColor.red("Cannot Process Stand DHM: NOT IMPLEMENTED");
   }
   
   /* ------------------------------------------------------------------------------- */
   private void performVoteBegin(DHM_vote vote) {
      if (share.checkAmCoordinator()) {
         // begin vote process
         // send vote_request objects to all servers
         // receive responses back
         // if all checkout, then send vote_commit
      } else {
         // not coordinator, send back error message
         DHM_error error = new DHM_error(vote,"Not Coordinator. Cannot begin a commit process.")
         stream_out.writeObject(error);
      }
      
   }



   /* ------------------------------------------------------------------------------- */

	public void run()
	{
      // we connect to client, read in their message and process it. Then we leave.
		try {
		   stream_in = new ObjectInputStream(client.getInputStream());
		   stream_out = new ObjectInputStream(client.getOutputStream());

			// check is socket is alive still
			stillAlive = client.isConnected();

			// wait for user response
			Object obj = stream_in.readUnshared();
			processRequest(obj);
			
			if (client.isConnected()) {
            stream_in.close();
            stream_out.flush();
            stream_out.close();
            client.close();
         }
         
			// check for interruptions, exit gracefully?
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
		}
		catch (EOFException e3) { // Normal EOF
			thread_exit();
		}
		catch (IOException e) {
			System.out.println("I/O error " + e); // I/O error
		}
		catch (ClassNotFoundException e2) {
			System.out.println(e2); // Unknown type of request object
		}
		catch (InterruptedException e) {
			System.out.println(e);
		}
		
		// nicely exit
		thread_exit();
	}


	private void processRequest(Object request) throws IOException
	{
		if (request instanceof DHmsg) {
			Request msg = (DHmsg) request;

			System.out.println("Request type: " + msg.getClassName() );
         
         if (msg instanceof DHM_vote) {
            PrintColor.yellow("Processing DHM_vote");
            process((DHM_vote)msg);
         } else if (msg instanceof DHM_checkpoint) {
            PrintColor.yellow("Processing DHM_checkpoint");
            process((DHM_checkpoint)msg);            
         } else {
            PrintColor.yellow("Processing Other DHM");
            process(msg);
         }
		}
		else {
		   PrintColor.yellow("Unknown message received: "+request.getClass() + " from: "+client.getClientAddr());
		}
		return;
	}

   /* ------------------------------------------------------------------------------- */
      
   // methods for distributed messages
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
	
	class ProcessException extends Exception {
	   String error;
	   DHmsg msg;
	   ProcessEception(String error, DHmsg msg) {
	      this.error = error; this.msg = msg;
	   }
	   public String toString() {
	      return "error: "+error+" msg: "+msg;
	   }
	}
}


