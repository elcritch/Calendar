import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;


// Could hold basic stuff like authentication, time stamps, etc.
public class Request implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2193091130641718696L;
	
}
/**
 * 
 * @author Vamsi
 *	This class is used to receive Ping reply  sent from Coordinator
 */
class Ping_Reply extends Request
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2329269665396183273L;
	String message;
	
	public Ping_Reply(String mesg)
	{
		message = mesg;
	}
	public String getPing_Reply()
	{
		return message;
	}
	
}
/**
 * 
 * @author Vamsi
 * This class is used to send ping request to the coordinator from the Coordinator
 */
class Ping_Request extends Request
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2329269665396183273L;
	String message;
	
	public Ping_Request(String mesg)
	{
		message = mesg;
	}
	public String getPing_Request()
	{
		return message;
	}	
	
}
/**
 * 
 * @author Vamsi
 * This class is used for sending /receiving the coordinator message from other nodes.
 */
class Coordinator_Message extends Request
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2329269665396183273L;
	InetAddress  originator_ip;
	
	int coordinator_id;
	InetAddress coordinator_ip;
	
	
	public Coordinator_Message(InetAddress orig_ip,int coord_id,InetAddress coord_ip)
	{
		originator_ip = orig_ip;
		coordinator_id = coord_id;
		coordinator_ip = coord_ip;
	}
	public InetAddress getOriginator()
	{
		return originator_ip;
	}	
	public int getCoordinator_Id()
	{
		return coordinator_id;
	}	
	public InetAddress getCoordinator_Ip()
	{
		return coordinator_ip;
	}
	
}

/**
 * 
 * @author Vamsi
 * This class is used to forward the election message received.
 */
class Node  extends Request implements Comparable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2329269665396183273L;
	InetAddress Ip;
	int  Id;

	public Node(InetAddress ip, int id)
	{
		Ip = ip;
		Id = id;
		
	}

	public InetAddress getIp()
	{
		return Ip;
	}	
	public int getId()
	{
		return Id;
	}
	public int compareTo(Object obj)
	{
		Node  nodeObj;
		nodeObj =(Node)obj;
		
		if(nodeObj.getId() == Id)
			return 0;
		else if(nodeObj.getId() > Id)
			return -1;
		else
			return 1;
		
	}
}
/**
 * 
 * @author Vamsi
 * This class is used to forward the election message received.
 */
class Election_Message  extends Request 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2329269665396183273L;
	InetAddress  originator_ip;
	public ArrayList<Node> election = new ArrayList<Node>(1);
	public Election_Message(InetAddress orig_ip)
	{
		originator_ip = orig_ip;
	}
	
	public InetAddress getOriginator()
	{
		return originator_ip;
	}
	
	public void addObject(Node obj)
	{
		election.add(obj);
	}
	
	public InetAddress getLastIp()
	{
		return (election.get(election.size()-1)).getIp();
	}	
	
	public void printList()
	{
		for(int i=0; i<election.size(); i++)
		{
			System.out.println(election.get(i).getIp());
		}
	}
	public Node getCoordinator()
	{
		Object[] retObjs = election.toArray();
		Arrays.sort(retObjs);
		return (Node)retObjs[(retObjs.length-1)];
		
	}

}

