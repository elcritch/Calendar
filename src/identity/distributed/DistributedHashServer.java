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

	public TimeServer(int port)
	{
		try {
			ss = new ServerSocket(port);
		}
		catch (IOException e) {
			System.err.println(e);
		}
	}


	public void runServer()
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
		if (args.length < 1) {
			System.err.println("Usage: java TimeServer <port>");
			System.exit(1);
		}
		TimeServer server = new TimeServer(Integer.parseInt(args[0]));
		server.runServer();
	}

}

class ServerConnection extends Thread
{
	Socket client;
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
      
      Basic sketch message queue:
         - every server has a message queue containing both user and calendar database DHmsg's.
         - any server can do a "sendAll" to send their message to all the other server's queues.
         - once a server has sent their message to all the servers, including the master server
            it then must send a "commit msgid from queue" message to the master server
            (the connection to the client will be maintained during this?
            this could be used to throw a timeout exception cancelling the process if it takes too long. )
         - the master server upon receiving a "commit msgid from queue" message will send a 
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







   /* ------------------------------------------------------------------------------- */

	public void run()
	{
		try {
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(new java.util.Date());
			oout.flush();
			client.close();
			Thread.sleep(10000); // 10 secs
		}
		catch (InterruptedException e) {
			System.out.println(e);
		}
		catch (IOException e) {
			System.out.println("I/O error " + e);
		}
	}
	
	public void run()
	{

		// This code actually begin serving to client.
		// First we want to create the object streams.
		// then we need to loop and wait for a requestObject
		// then we create an appropriate sendObject
		try {
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());

			// this is the parent path location.
			parentPath = new File(".");


			System.out.println(parentPath.getPath());
			parentPath = parentPath.getCanonicalFile();

			// send initial response so we know we're alive
			out.writeObject( new RequestError("Welcome, your wish is my command!", 0) );
			while (stillAlive) {
				// check is socket is alive still
				stillAlive = client.isConnected();

				// wait for user response
				processRequest(in.readObject());
				out.flush();

				// check for interruptions, exit gracefully?
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			}

			// nicely exit
			thread_exit();

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
		//finally {
		//	thread_exit();
		//}

	}


	private void processRequest(Object request) throws IOException
	{
		File userRequestPath;
		/*    System.out.println("Processing Request.");*/
		// process request type to decide what to do:
		if (request instanceof Request) {
			Request userRequest = (Request) request;
			userRequestPath = checkPathname(userRequest.path);
			//System.out.println("URP: "+userRequestPath.getPath());
			System.out.println("Request type: " + userRequest);
			// check path
			if ( userRequestPath == null ) {
				return;
			}

			// check request type
			if ( userRequest.type < 0 ||
			     userRequest.type > Request.requestTypes.length ) {
				System.out.println("Unknown Client request type. Type = " + userRequest.type );
				return;
			}

			// execute request type
			if ( userRequest.type == 0 )
				return;
			else if ( userRequest.type == 1)
				processDirListing(userRequestPath, false);
			else if ( userRequest.type == 2)
				processSendFile(userRequestPath);
			else if ( userRequest.type == 3)
				thread_exit();
		}
		else {
			out.writeObject( new RequestError("Unkown Object!", 0) );
		}
		return;
	}

}


