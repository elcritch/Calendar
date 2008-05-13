import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.net.InetAddress;
/**
 * 
 */

/**
 * @author vnandana
 *
 */
class ElectionMonitor extends Thread
{
	public static Socket client;
	public static Node nodeObj;
	public static int port =5235;
	public static InetAddress Coordinator_IP =null;
	public static int Coordinator_ID ;
	public static Timer timer = new Timer();
	public  String nextIp ;
	public static int nextPort  =5235;
	public ElectionLock lock;
	//Constructor
	ElectionMonitor(InetAddress node_ip,int node_id,String s, ElectionLock lock) throws SocketException
	{
		super(s);
		
		nodeObj = new Node(node_ip,node_id);
		this.lock = lock ;


		setPriority(NORM_PRIORITY - 1);
		

	}

	public void run()
	{

			int delay = 11000;
			int period = 7000;


			  timer.scheduleAtFixedRate(new TimerTask()
			  {
				public void run()
				{
					
					try {
						if(Coordinator_IP ==null)
							throw new IOException("Initial ");
						if(!nodeObj.getIp().equals(Coordinator_IP))
						{
							Coordinator_Message objct =lock.waitForElection();
							
							Coordinator_IP = objct.getCoordinator_Ip();
							Coordinator_ID = objct.getCoordinator_Id();
							
							 client = new Socket(Coordinator_IP,port);
							 client.setSoTimeout(7000);		
							 
							 if(client.isConnected())
							 {
							   sendPingRequest() ;
							   waitForPingReply() ;
							 }
							 else
								 throw new IOException();

					  }
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						//e.printStackTrace();
						//{
							//Stop the timer
							System.out.println("Holding for Election...");
							//Start election message
							try
							{
								sendElectionMessage();
							} 
							catch (IOException e1)
							{
								// Get the next neighbour and send the election message
								System.out.println("get next neighbour to send election message");
							}
							lock.startElection();
							
							Coordinator_Message objct =lock.waitForElection();
							
							Coordinator_IP = objct.getCoordinator_Ip();
							Coordinator_ID = objct.getCoordinator_Id();
							System.out.println("Lock Released...");
							System.out.println("election monitor..cooop is :"+Coordinator_IP);
							
						//}
					} 
					
					finally {
						try {
							if(client!=null)
								client.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
						}
				}

			  },delay,period);
			  
			
		

	}
		
	//method to initiate Ping request
	private boolean sendPingRequest() throws IOException
	{
		System.out.println("Ready to send ping again..............");
	    ObjectOutputStream out;
	    if(client.isConnected())
	    {
			out = new ObjectOutputStream(client.getOutputStream());
			System.out.println("Sending Ping Request to "+client.getInetAddress() +" on Port : "+client.getPort());
			Ping_Request pir = new Ping_Request("ECHO_REQUEST");
			out.writeUnshared(pir);
			out.flush();
			return true;
	    }
	    else
	    	throw new IOException("Failed to send ping request.. ");
	    	
			
	}
	//method to accept ping reply
	private boolean waitForPingReply() throws IOException, ClassNotFoundException
	{

			System.out.println("Waiting for reply");
			if(client.isConnected())
			{
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			
		    Object obj = in.readUnshared();
			if (obj instanceof Ping_Reply)
			{
				System.out.println(((Ping_Reply) obj).getPing_Reply());
				return true;
			}
			
			}
			else
				throw new IOException();

		
		return true;
	}
	//method to send Election Message
	private void sendElectionMessage() throws IOException
	{
		System.out.println("Sending Election Request ");
		
		Election_Message eMesg = new Election_Message(nodeObj.getIp());
		eMesg.addObject(nodeObj);
		
		sendMessage(eMesg);
		
	}
	
	//Method to send data
	public  void sendMessage(Object dataObj) throws IOException 
	{

			//Create a Socket to connect to next node
			Socket s;

				s = new Socket(nextIp, nextPort);
				if(s.isConnected())
				{
					System.out.println(s.getInetAddress() +" on Port : "+s.getPort());
					ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
					out.writeUnshared(dataObj);
					out.flush();
				}
				else
					throw new IOException("Failed to send to neighbour ");
	
	}
}
