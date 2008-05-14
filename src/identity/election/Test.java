package identity.election;



import identity.server.SharedData;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Test
{
	public static void main(String[] args) throws InterruptedException
	{
		try
		{
			SharedData share = new SharedData();
			ElectionLock lock = new ElectionLock();
			InetAddress selfIp = InetAddress.getLocalHost();
			PrintColor.ansi = true;
			
			share.elock = lock;
			share.selfaddress = selfIp;
			share.servers =  new ServerList(selfIp);
			share.clock = new CoordLock();
			Groupie g = new Groupie(share);
			System.out.println("Local ip addr :"+selfIp);
			
			ElectionListener listen = new ElectionListener("ElectionListener",share);
			ElectionMonitor monitor = new ElectionMonitor("ElectionMonitor",share);
			
			
		
			g.start();
			monitor.start();
			listen.runListener();
			

		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
