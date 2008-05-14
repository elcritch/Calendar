package identity.distributed;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version $Rev$
 */

import identity.calendar.CalendarDBServer;
import identity.distributed.ServerConnection.ProcessException;
import identity.election.PrintColor;
import identity.server.SharedData;
import identity.server.UserInfoDataBase;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import sun.misc.PerformanceLogger;
import sun.tools.tree.CastExpression;


public class DistributedHashServer extends Thread
{
	private int port;
	ServerSocket ss;

	// system data
	private SharedData share;

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
			String ip_addr = InetAddress.getLocalHost().getHostAddress();
			File dir = new File("conf/"+ip_addr);
			dir.mkdir();
			// better way to do this?
			userdb = new UserInfoDataBase( dir.getName()+"/defaultUserInfo.db" );
			caldb = new CalendarDBServer(dir.getName()+"/CALENDAR");

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
				new ServerConnection(client, port, share).start();
			}
		}
		catch  (IOException e) {
			System.err.println(e);
		}

	}

	// main method
	public static void main (String [] args)
	{
		SharedData data = new SharedData();
		
		DistributedHashServer dhserver = new DistributedHashServer( data, 5294);
		
		dhserver.start();
		
	}

}

class ServerConnection extends Thread implements Types
{
	Socket client;
	ObjectInputStream stream_in;
	ObjectOutputStream stream_out;
	private SharedData share;
	private int port;

	ServerConnection (Socket client, int port, SharedData share) throws SocketException
	{
		this.client = client;
		this.share = share;
		this.port = port;
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
	/**
	 * 	 
	 * @throws IOException 
	 */
	private void process(DHM_vote vote) throws IOException {
		try {
			switch (vote.type) {
			case   VOTE_BEGIN: 
				performVoteBegin(vote);
				break;
			case VOTE_REQUEST:
			   // respond back with wether we have our given message
				break;
			case  VOTE_COMMIT: 
				break;
			case      GET_MSG: 
				break;
			case    DO_COMMIT: 
				break;
			default :
				break;
			}
		}
		catch (ProcessException pe) {
			PrintColor.yellow("while processing vote message of type: "+vote.type);
			PrintColor.yellow(pe);
			stream_out.writeUnshared(pe);
		}
		}

		private void process(DHM_checkpoint chkpnt) {
		}

		/* ------------------------------------------------------------------------------- */
		/**
		
		
		*/
		private void performVoteBegin(DHM_vote initmsg) throws ProcessException {
			if (share.clock.checkCoordinator() ) {
				// begin vote process
				// send vote_request objects to all servers
				// receive responses back
				DHM_vote vote = new DHM_vote(VOTE_BEGIN,initmsg.lamport);
				
				// sendAndReceiveAll should perform error checking in that it should
				// try to send data to nodes until they respond back with VOTE_COMMIT
				DHmsg[] results = sendAndReceiveAll(vote);
		
				// if all checkout, then send do_commit
				boolean check = true;
            for (DHMsg r : results) {
               if (!( r!=null && (r instanceof DHM_vote) && r.type == VOTE_COMMIT ))
                  check = false;
            }
            
            // we shouldn't need to do error checking here?
            // checkpoint should catch any more errors
            sendAll(new DHM_vote(DO_COMMIT) );

			} else {
				// not coordinator, send back error message
				DHM_error error = new DHM_error(vote,"Not Coordinator. Cannot begin a commit process.");
				throw new ProcessException(error);
			}
		}

		@SuppressWarnings("unused")
		private void process(DHmsg msg) {
			PrintColor.red("Cannot Process Stand DHM: NOT IMPLEMENTED");
		}


ß
      /**
      Run method, this starts a new server response thread
      */
		public void run()
		{
			// we connect to client, read in their message and process it. Then we leave.
			try {
				stream_in = new ObjectInputStream(client.getInputStream());
				stream_out = new ObjectOutputStream(client.getOutputStream());

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

		}


		private void processRequest(Object request) throws IOException
		{
			if (request instanceof DHmsg) {
				DHmsg msg = (DHmsg) request;

				System.out.println("Request type: " + msg.getClass().getSimpleName());

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
				PrintColor.yellow("Unknown message received: "+request.getClass() 
						+ " from: "+client.getInetAddress() );
			}
			return;
		}

		/* ------------------------------------------------------------------------------- */
		// methods for distributed messages
		
		/**
		
		
		*/
		protected DHmsg[] sendAndReceiveAll(DHmsg dhm) throws ProcessException
		{
			// this method will send the message to the coord queue
			InetAddress[] servers = share.servers.toArray();

			// connect to each server in the list, including our own
			SenderVote[] sender = new SenderVote[servers.length];
			DHmsg[] results = new DHmsg[servers.length];
			Semaphore semaphore = new Semaphore(servers.length);
			
			for (int i = 0; i<servers.length; i++) {
				// we don't need to do error checking here, the server will take care of that. 
				sender[i] = new SenderVote(servers[i],dhm, semaphore);
				sender[i].start();
			}
			// sleep for a bit. we could join all threads, but this should give us enough delay?
			try {
				System.out.println("DEBUG THREAD: Trying to join sendAndReceiveAll threads");
				if (!semaphore.tryAcquire(servers.length, 1000, TimeUnit.MILLISECONDS)) {
					throw new ProcessException("Couldn't aquire locks for message", dhm);
				}
				
				int i = 0;
				for (SenderVote sent : sender) {
					results[i++] = sent.result;
				}
				return results;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		protected void sendDHM(InetAddress host, DHmsg msg) throws SocketTimeoutException {
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


      /**
      SenderVote subclass sends a message to a server, if there is any response 
      it processess it according to an overridable "respond()" method. 
      
      In the SenderVote case, we want to send a vote_co
      
      */
      class Sender extends Thread {
         DHmsg msg = null;
         DHmsg result = null;
         public Semaphore semaphore = null;
         InetAddress host = null;
         
         public ObjectInputStream connect_in;
         public ObjectOutputStream connect_out;
         public Socket s;
         
         SenderVote(InetAddress host, DHmsg msg) { 
            this.msg = msg;
            this.host = host;
         }

         public void run() {
            try {
               if (semaphore != null)
                  semaphore.acquire();
               s = new Socket(host, port);
               connect_in = new ObjectInputStream(s.getInputStream());
               connect_out = new ObjectOutputStream(s.getOutputStream());

               connect_out.writeUnshared(msg);
               
               result = (DHmsg) connect_in.readUnshared();
               respond();
               
               // close
               connect_in.close();
               connect_out.flush();
               connect_out.close();
               s.close();
               
               // shoule we release semaphore here or before we close the connection?
               semaphore.release();
            }
            catch (ClassCastException cce) {
               result = null;
            }
            catch (IOException e1) {
               PrintColor.red("error: sending DHM: "+msg+" to: "+host);
               System.out.println(e1);
            } catch (ClassNotFoundException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         protected void respond() {
            return; // do nothing for basic Sender Thread
         }
      }
      
      class SenderVote extends Sender {
         public Semaphore semaphore = null;
         
         public SenderVote(InetAddress inetAddress, DHmsg dhm, Semaphore semaphore) {
            super(inetAddress, dhm);
            this(inetAddress, dhm);
            this.semaphore  = semaphore;
         }
         
         protected void respond() {
            try {
               DHmsg dhm;
                while (s.isConnected()) {
                  dhm = (DHmsg) connect_in.readUnshared();
                  
               }
            } catch (EOFException e) {
               
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (ClassNotFoundException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }

		class ProcessException extends Exception {
			private static final long serialVersionUID = 839187662031418327L;
			String error;
			DHmsg msg;
			ProcessException(DHM_error errormsg) {
				this.error = errormsg.error_msg; 
				this.msg = errormsg;
			}
			ProcessException(String error, DHmsg msg) {
				this.error = error; this.msg = msg;
			}
			public String toString() {
				return "error: "+error+" msg: "+msg;
			}
		}
	}


