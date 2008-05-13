import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ElectionListener
{
	ServerSocket ss;
	int port = 5235;
	//Server Timeout
	public static int SERVER_TIMEOUT = 120000; //2 min in milliseconds
	public String nextIp ;
	InetAddress self_Ip=null;
	int Self_id=0;
	String threadName;
	public ElectionLock lock;
	//class constructor
	public ElectionListener(InetAddress inet,int node_id,String s, ElectionLock lock)
	{
		
		try
		{
			ss = new ServerSocket(port);
			self_Ip = inet;
			Self_id = node_id;
			threadName =s;
			this.lock =lock;
			
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}

	//method to run the server and accept connections from clients.
	public void runListener()
	{
		Socket client;
		try
		{
			System.out.println("Listener Started");


			while (true)
			{
				//set the server timeout
				ss.setSoTimeout(SERVER_TIMEOUT);

				//Accept the connection from client
				client = ss.accept();
						
					String clientName = client.getInetAddress().getHostName();
					String clientaddr = client.getInetAddress().getHostAddress();
					System.out.println("Received connect from " + clientName + " and " + clientaddr);
					
					Listener listen = new Listener(self_Ip,Self_id,client,threadName,lock);
					listen.nextIp = nextIp;
					
					listen.start();
					
			}

			// server should not get killed here ???

		}
		catch (IOException e)
		{
			System.err.println(e);
			//System.exit(0);
		}

	}

}
 class Listener extends Thread
{
	//public static Socket client;
	public static Node nodeObj;
	Socket sd;
	public static int port = 5235;
	public static InetAddress Coordinator_IP =null;
	public static int Coordinator_ID ;

	// temporary
	public  String nextIp ="132.178.248.36";
	public static int nextPort  =5235;
	public static int SERVER_TIMEOUT = 120000; //2 min in milliseconds
	
	public ElectionLock lock;
	//Constructor
	public Listener(InetAddress node_ip,int node_id,Socket client,String s, ElectionLock lock) throws SocketException
	{
		super(s);
		nodeObj = new Node(node_ip,node_id);
		this.sd = client;
		this.lock = lock;
		setPriority(NORM_PRIORITY - 1);
		System.out.println("Created thread " + this.getName());		
	}

	public void run()
	{
		
	
		try
		{		
			
			System.out.println("Accepting connections....");
			
			while(true)
			{
			  ObjectInputStream in = new ObjectInputStream(sd.getInputStream());
			  if(sd.isBound())
			  {
				  Object obj = in.readUnshared();
				  processObject(obj);
			  }
			  System.out.println("**************");
			}
		}
		catch (IOException e)
		{
			//System.out.println("run " + e);
			
			try {
				sd.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//System.exit(0);

		}
		catch (ClassNotFoundException c)
		{
			//System.out.println("run  " + c);
			//System.exit(0);
		}

	}
	//method to process the input object
	
	private boolean processObject(Object response) throws IOException
	{
		
		boolean retrnval = true;

		System.out.println("Hello doctoR");
		if (response instanceof Ping_Reply)
		{
			System.out.println(((Ping_Reply) response).getPing_Reply());
			retrnval = true;
		}
		else if (response instanceof Ping_Request)
		{
			System.out.println(((Ping_Request) response).getPing_Request());
			//received ping request from other node. Send a reply back.
			Ping_Reply mesg = new Ping_Reply("ECHO_REPLY");
			sendObject(mesg);
			retrnval = true;

		}
		else if (response instanceof Coordinator_Message)
		{
			//System.out.println(((Coordinator_Message) response).getCoordinator_Id());
			System.out.println("Received CO-ORDINATOR message :" +((Coordinator_Message) response).getCoordinator_Ip());
			SetCoordinator((Coordinator_Message) response);
			//Set the Coordinator
			System.out.println("New Coordinator is :"+Coordinator_IP);
			lock.endElection((Coordinator_Message)response);
			retrnval = true;
			
		}
		else if (response instanceof  Election_Message)
		{
			
			//Process Election response
			proceessElection((Election_Message)response);
				
			retrnval = true;
			
		}		
		else
		{
			System.out.println("Unknown data recieved");
			retrnval = true;

		}
	


		return retrnval;
	}

	/**
	 * client
	 * @param eObj
	 * @throws IOException
	 * Parse through all the objects and see if it contains
	 * the current node's ip. If it contains the current node ip
     * stop the election  and find the coordinator.
 	 * else forward the data to the next client.
	 */
	public   int proceessElection(Election_Message eObj) throws IOException
	{
		
		
		
		System.out.println("Received election message from "+eObj.getLastIp() );
		System.out.println("Received election message with Originator "+eObj.getOriginator());
		lock.startElection();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(eObj.getOriginator().equals(nodeObj.getIp()))
		{
			System.out.println("Received election message was originated by me....");
			System.out.println("ELECTION STOPPED ...RECEIVED IP MESSAGES ARE :");
			eObj.printList();
			
			//Get the coordinator
			Node electCoordinator = eObj.getCoordinator();
			
			int Coordinator_id = electCoordinator.getId();
			InetAddress Coordinator_ip = electCoordinator.getIp();
			
			//frame the coordinator message
			Coordinator_Message Coordinator = new Coordinator_Message(nodeObj.getIp(), Coordinator_id,Coordinator_ip);
			
			System.out.println("New Election Coordinator is :"+Coordinator_ip);
			
			//forward the coordinator message
			forwardMessage(Coordinator);
			
			return 0;
		}
		else
		{
			System.out.println("adding my object :" +nodeObj.toString());
			
			eObj.addObject(nodeObj);
			forwardMessage(eObj);
			return -1;
		}
		
		
	}

	public  void SetCoordinator(Coordinator_Message response) throws IOException
	{
		System.out.println("My Ip is "+nodeObj.getIp()+" and Originator for coordinator is "+response.getOriginator());
		if(response.getOriginator().equals(nodeObj.getIp()))
		{
			System.out.println("I STOPPED  FORWARDING <<<<<<<<<<<>>>>>>>>>>>>>>>>");
			
		}
		else
		{
			//forward the coordinator message to the next node.
			System.out.println("IAM  FORWARDING *****************************");
			
			forwardMessage(response);
			
			
		}
			
		//Set the coordinator
		Coordinator_IP = response.getCoordinator_Ip();
		Coordinator_ID = response.getCoordinator_Id();
		
		System.out.println("Set the coordinator.... Stopping election");
		
		
	}
	//Method to send Data to Client
	public  void sendObject(Object dataObj) throws IOException 
	{
		System.out.println("Sending data ....");
		System.out.println(((Ping_Reply)dataObj).getPing_Reply());
		
		ObjectOutputStream oout;
		if(sd.isConnected())
		{
			oout = new ObjectOutputStream(sd.getOutputStream());
			oout.writeUnshared(dataObj);
			oout.flush();
			System.out.println("Data Sent");
		}
		else
			throw new IOException();

	}
	//Method to forward Data to Client
	public  void forwardMessage(Object dataObj) 
	{

			//Create a Socket to connect to next node
			Socket s;
			try
			{
				s = new Socket(nextIp, nextPort);
				if(s.isConnected())
				{
				System.out.println("forwarding............ ....");
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				out.writeUnshared(dataObj);
				out.flush();
				}
				else
				{
					System.out.println("Getting the next neighbour in the list ...forward failed ");
				}
				
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Getting the next neighbour in the list ");
				//e.printStackTrace();
			}

		
	}

}

