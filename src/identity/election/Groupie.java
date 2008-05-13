import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.NoSuchElementException;


/**
* @author amit
* @author Jaremy Creechley 
*/
public class Groupie
{

	private static final int debug = 1;
	private static final int discoveryPort = 5298;
	private static final int timeout = 3000; //milliseconds
	private static final int default_timeout = 0; //infinite
	private static final String mcastAddress = "230.10.10.10";

	private static final int DISCOVER_GROUP = 1;
	private static final int LEAVE_GROUP = 2;
	private static final int JOIN_GROUP = 3;
	private static final int PRESENT = 4;
	private static final int CHECKSUM = 5;
	private static final int SELF_MSG = -1;

   private ServerList servers;
   private Set<Integer> checksums = Collections.synchronizedSet(new HashSet<Integer>());
	private InetAddress group;
	private MulticastSocket s;

   private CoordLock coordLock = new CoordLock();
   private boolean checksumProcess = false;
   private int checksumFailureCount = 0;
   /**
    * Setter for checksumProcess.
    * @param newChecksumProcess new value for checksumProcess
    */
   public synchronized void setChecksumProcess(boolean cp) {
       checksumProcess = cp;
   }

   /**
    * Getter for checksumProcess.
    * @return checksumProcess
    */
   public synchronized boolean checksumProcess() {
       return checksumProcess;
   }
   
	private class Pack
	{
      public Pack (int type, InetAddress addr)
      {
         this.type = type;
         this.other = null;
         this.addr = addr;
      }
      public Pack (int type, int other, InetAddress addr)
      {
         this.type = type;
         this.other = new Integer(other);
         this.addr = addr;
      }
		public int type;
		public Integer other;
		public InetAddress addr;
	}

	public Groupie()
	{

		try {
			group = InetAddress.getByName(mcastAddress);
			PrintColor.green("InetAddress: " + group);

			s = new MulticastSocket(discoveryPort);
			s.setLoopbackMode(true);
			s.joinGroup(group);

		}
		catch (UnknownHostException e) {
			PrintColor.red(e);
			System.exit(1);
		}
		catch (IOException e) {
			PrintColor.red(e);
			System.exit(1);
		}


		Runtime sys = Runtime.getRuntime();
		Thread CleanupThread = new CleanupThread(this);
		sys.addShutdownHook(CleanupThread);

		int delay = 1 * 1000; // delay for 5 sec.
		int period = 35 * 1000; // repeatTimer timer = new Timer();

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new GroupieTimer(), delay, period);

	}

   private class GroupieTimer extends TimerTask {
      public void run() {
         try {
            // coordLock.amCoordinator();
            PrintColor.blue("\n\nDancing...");
            sendIntMessage(DISCOVER_GROUP);
            // Thread.sleep(3*1000);
            // System.out.println("print group:");
            // printGroup();
         }
         // catch (InterruptedException e) {
         //    e.printStackTrace();
         //    System.out.println("Closing?");
         // }
         catch (IOException e) {
            PrintColor.red("Socket IOError: " + e);
         }
      }
   }
   
   /** 
    * send out our CHECKSUM some set time after receiving a DISCOVER_GROUP msg
    * this time will be our "DISCOVER_GROUP" time
    * Algorithm:
    * After receiving a DISCOVER_GROUP message, if not already in a "checksumProcess time"
    * begin this timer for a single repeat. After the alloted time, send a CHECKSUM message.
    * Sleep for a time and then compare results!
   */
   private class CheckDiscoverResults extends TimerTask {
      public void run() {
         try {
            PrintColor.yellow("Sending out checksum");
            sendTwoIntMessage(CHECKSUM,servers.hashCode());
            Thread.sleep(5*1000);
            compareChecksums();
         }
         catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Closing?");
         }
         catch (IOException e) {
            PrintColor.red("Socket IOError: " + e);
         }
      }
   }


	public void printDatagram(DatagramPacket pkt) throws UnknownHostException, IOException
	{
		String packetType = "";
		switch (Utility.getInt(pkt.getData())) {
		case 1:
			packetType = "DISCOVER_GROUP";
			break;
		case 2:
			packetType = "LEAVE_GROUP";
			break;
		case 3:
			packetType = "JOIN_GROUP";
			break;
      case 4:
         packetType = "PRESENT";
         break;
      case -1:
         packetType = "SELF_MSG";
         break;
      case 5:
         packetType = "CHECKSUM";
         break;
		default:
			break;
		}
		PrintColor.green("Server " + InetAddress.getLocalHost() + ": Received packet type " +
		                   packetType + " from " + pkt.getAddress());
	}

	private void initialize(boolean setcoord) 	throws IOException
	{
	   if (setcoord)
         coordLock.becomeCoordinator();
         
	   // Initialize Things
	   servers = new ServerList((Inet4Address) InetAddress.getLocalHost());
	   s.setSoTimeout(timeout);

	   // send intial message
	   sendIntMessage(DISCOVER_GROUP);
	}

	public void leave()
	{
		try {
			sendIntMessage(LEAVE_GROUP);
			servers.clear();
			s.leaveGroup(group);
		}
		catch (IOException e) {
			PrintColor.red(e);
		}
	}

	public void groupDance() throws IOException
	{
		byte[] buf = new byte[8];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		PrintColor.green("Beginning groupDance stage");
		while (true) {
			Pack pack = receiveIntMessage(default_timeout);
			PrintColor.blue("Recieved msg: "+pack.type);
			switch (pack.type) {
			case DISCOVER_GROUP:
			   // send a PRESENT msg with the current Coordinators IP overload as a byte. 
			   // NOTE! IP address should be IPv4!!!
			   int coordip = Utility.getInt(lock.getCoordInetAddress().getAddress());
				sendIntMessage(PRESENT,coordip);
				servers.add(pack.addr);
				// now begin the checksum process
				if (!checksumProcess()) {
				   PrintColor.yellow("Starting checksumProcess");
				   setChecksumProcess(true);
   				int delay = 2 * 1000; // delay for DISCOVER_GROUP before CHECKSUM'ing
         		Timer timer = new Timer();
         		timer.schedule(new CheckDiscoverResults(), delay);
      		} 
				break;
			case JOIN_GROUP:
				servers.add(pack.addr);
				break;
			case LEAVE_GROUP:
				servers.remove(pack.addr);
				break;
			case PRESENT:
				servers.add(pack.addr);
				lock.setCoordInetAddress(pack.other);
				break;
         case SELF_MSG:
            PrintColor.red("Debug: Self message");
         case CHECKSUM:
            PrintColor.red("Debug: CHECKSUM "+pack.other);
            checksums.add(pack.other);
            break;
			default:
				PrintColor.red("Error... unknown message! " + pack.type);
				break;
			}
			if (debug >= 2) printGroup();
		}
	}

	/**
	* receiveIntMessage
	*
	* @return the integer message type
	 * @throws IOException 
	*/
	public Pack receiveIntMessage(int timeout) throws IOException
	{
		s.setSoTimeout(timeout);
		byte[] buf = new byte[8];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		int recvInt, recvInt2;

		do {
			s.receive(recv);
			recvInt = Utility.getInt(buf);
			recvInt2 = Utility.getSecondInt(buf);
		}
		while ( recv.getAddress().equals( InetAddress.getLocalHost() ) );

		if (debug >= 1)
			printDatagram(recv);

		return new Pack(recvInt, recvInt2, recv.getAddress() );
	}

   /**
   * sendIntMessage
   *
   * @param msg integer data to send.
    * @throws IOException 
   */
   public void sendIntMessage(int msg) throws IOException
   {
      if (debug >= 2)
         PrintColor.red("\t# Debug: Sending msg: " + msg);
      byte[] msgBytes = Utility.getBytes(msg,0);
      DatagramPacket send_msg = new DatagramPacket(msgBytes, 8, group, discoveryPort);
      s.send(send_msg);
   }  
   
   /**
   * sendTwoIntMessage
   *
   * @param msg integer data to send with another int
    * @throws IOException 
   */
   public void sendTwoIntMessage(int msg, int other) throws IOException
   {
      if (debug >= 2)
         PrintColor.red("\t# Debug: Sending msg: " + msg);
      byte[] msgBytes = Utility.getBytes(msg,other);
      DatagramPacket send_msg = new DatagramPacket(msgBytes, 8, group, discoveryPort);
      s.send(send_msg);
   }

	public void printGroup()
	{
		Iterator<InetAddress> itr = servers.iterator();
		while (itr.hasNext()) {
			System.out.println("Group: " + itr.next());
			System.out.flush();
		}

	}
   
   /**
    * compareChecksums
    *
    * @param  
    * @return the results of the comparison
    */
   public synchronized boolean compareChecksums( ) {
      try {
         System.out.println("DEBUG: beginning checksum");
         Iterator<Integer> itr = checksums.iterator();
         Integer last = itr.next();
		   PrintColor.yellow("checksum: "+last);
   		while (itr.hasNext()) {
   		   Integer next = itr.next();
   		   PrintColor.yellow("checksum: "+next);
            if ( !last.equals(next) ) {
            // if ( !last.equals(itr.next()) ) {
               // turn off checksumProcess and rediscover group
               checksumFailureCount++;
               PrintColor.yellow("CHECKSUM Failure! Toltal number of failurs: "+checksumFailureCount);
               setChecksumProcess(false);
               sendIntMessage(DISCOVER_GROUP);
            }
   		}
   		// DEBUG
   		System.out.println("DEBUG: print group after checksumed");
   		printGroup();
         // System.out.println("\n");
      } catch (NoSuchElementException e) {
         System.out.println("Error not enough elements in iterator");
      }
      catch (IOException e) {
         PrintColor.red("Socket IOError: " + e);
         e.printStackTrace();
      } 
      finally {
         checksums.clear();
         setChecksumProcess(false);
      }
		System.out.println("group checksumed well");
		return true;
   }

   
   
   
	/**
	* @param args
	*/
	public static void main(String[] args)
	{
	   boolean setcoord= false;
      PrintColor.ansi = true;
	   
	   try {
			Groupie g = new Groupie();
			// NEW TEST!
			// NEW DOUBLE!
			if (args.length>0) {
			   setcoord = true;
			   System.out.println("%% I am Coordinator! %%");
			}
         g.initialize(setcoord);
			g.groupDance();
		}
		catch (IOException e) {
		   e.printStackTrace();
		}
	}


}

class CleanupThread extends Thread
{
	Groupie server;
	public CleanupThread(Groupie server)
	{
		this.server = server;
	}

	public void run()
	{
		System.out.println("Groupie server shutting down...");
		server.leave();
	}



}





